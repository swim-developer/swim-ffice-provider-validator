package com.github.swim_developer.validator.ffice.provider.infrastructure.client;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ProviderHttpClient {

    private static final Logger LOG = Logger.getLogger(ProviderHttpClient.class);
    private static final int TIMEOUT_MS = 30000;

    @Inject Vertx vertx;

    @ConfigProperty(name = "proxy.mtls.keystore.path", defaultValue = "certs/keystore.p12")
    String keystorePath;
    @ConfigProperty(name = "proxy.mtls.keystore.password", defaultValue = "changeit")
    String keystorePassword;
    @ConfigProperty(name = "proxy.mtls.keystore.type", defaultValue = "PKCS12")
    String keystoreType;
    @ConfigProperty(name = "proxy.mtls.truststore.path", defaultValue = "certs/truststore.p12")
    String truststorePath;
    @ConfigProperty(name = "proxy.mtls.truststore.password", defaultValue = "changeit")
    String truststorePassword;
    @ConfigProperty(name = "proxy.mtls.truststore.type", defaultValue = "PKCS12")
    String truststoreType;

    private WebClient client;

    @PostConstruct
    void init() {
        WebClientOptions opts = new WebClientOptions()
            .setSsl(true).setTrustAll(false).setVerifyHost(false)
            .setConnectTimeout(TIMEOUT_MS).setIdleTimeout(TIMEOUT_MS);
        if ("PKCS12".equalsIgnoreCase(keystoreType)) {
            opts.setPfxKeyCertOptions(new PfxOptions().setPath(keystorePath).setPassword(keystorePassword));
            opts.setPfxTrustOptions(new PfxOptions().setPath(truststorePath).setPassword(truststorePassword));
        } else {
            opts.setKeyStoreOptions(new JksOptions().setPath(keystorePath).setPassword(keystorePassword));
            opts.setTrustStoreOptions(new JksOptions().setPath(truststorePath).setPassword(truststorePassword));
        }
        this.client = WebClient.create(vertx, opts);
    }

    public Response get(String baseUrl, String path, String bearerToken) {
        return execute("GET", baseUrl, path, bearerToken, null);
    }

    public Response post(String baseUrl, String path, String bearerToken, String body) {
        return execute("POST", baseUrl, path, bearerToken, body);
    }

    public Response put(String baseUrl, String path, String bearerToken, String body) {
        return execute("PUT", baseUrl, path, bearerToken, body);
    }

    public Response delete(String baseUrl, String path, String bearerToken) {
        return execute("DELETE", baseUrl, path, bearerToken, null);
    }

    private Response execute(String method, String baseUrl, String path, String bearerToken, String body) {
        try {
            URI uri = URI.create(baseUrl);
            int port = uri.getPort() > 0 ? uri.getPort() : ("https".equals(uri.getScheme()) ? 443 : 80);
            CompletableFuture<HttpResponse<Buffer>> future = new CompletableFuture<>();
            var req = switch (method) {
                case "POST" -> client.post(port, uri.getHost(), uri.getPath() + path);
                case "PUT"  -> client.put(port, uri.getHost(), uri.getPath() + path);
                case "DELETE" -> client.delete(port, uri.getHost(), uri.getPath() + path);
                default -> client.get(port, uri.getHost(), uri.getPath() + path);
            };
            req.putHeader("Authorization", "Bearer " + bearerToken)
               .putHeader("Content-Type", "application/json");
            if (body != null) {
                req.sendBuffer(Buffer.buffer(body), ar -> {
                    if (ar.succeeded()) future.complete(ar.result());
                    else future.completeExceptionally(ar.cause());
                });
            } else {
                req.send(ar -> {
                    if (ar.succeeded()) future.complete(ar.result());
                    else future.completeExceptionally(ar.cause());
                });
            }
            HttpResponse<Buffer> r = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            return Response.status(r.statusCode()).entity(r.bodyAsString())
                .header("Content-Type", "application/json").build();
        } catch (Exception e) {
            LOG.errorf("Provider HTTP %s %s%s failed: %s", method, baseUrl, path, e.getMessage());
            return Response.serverError().entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
