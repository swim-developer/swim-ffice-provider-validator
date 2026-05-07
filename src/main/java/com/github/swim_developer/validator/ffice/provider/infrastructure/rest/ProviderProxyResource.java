package com.github.swim_developer.validator.ffice.provider.infrastructure.rest;

import com.github.swim_developer.validator.ffice.provider.infrastructure.client.ProviderHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/provider")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProviderProxyResource {

    private static final String BASE_PATH = "/swim/v1";

    @Inject ProviderHttpClient providerClient;
    @ConfigProperty(name = "swim.provider.api.urls") String providerUrls;

    @POST @Path("/subscriptions")
    public Response createSubscription(@HeaderParam("Authorization") String auth,
                                       @QueryParam("providerUrl") String url, String body) {
        return providerClient.post(resolve(url), BASE_PATH + "/subscriptions", token(auth), body);
    }

    @GET @Path("/subscriptions")
    public Response listSubscriptions(@HeaderParam("Authorization") String auth,
                                      @QueryParam("providerUrl") String url) {
        return providerClient.get(resolve(url), BASE_PATH + "/subscriptions", token(auth));
    }

    @GET @Path("/subscriptions/{id}")
    public Response getSubscription(@PathParam("id") String id,
                                    @HeaderParam("Authorization") String auth,
                                    @QueryParam("providerUrl") String url) {
        return providerClient.get(resolve(url), BASE_PATH + "/subscriptions/" + id, token(auth));
    }

    @PUT @Path("/subscriptions/{id}")
    public Response updateSubscription(@PathParam("id") String id,
                                       @HeaderParam("Authorization") String auth,
                                       @QueryParam("providerUrl") String url, String body) {
        return providerClient.put(resolve(url), BASE_PATH + "/subscriptions/" + id, token(auth), body);
    }

    @DELETE @Path("/subscriptions/{id}")
    public Response deleteSubscription(@PathParam("id") String id,
                                       @HeaderParam("Authorization") String auth,
                                       @QueryParam("providerUrl") String url) {
        return providerClient.delete(resolve(url), BASE_PATH + "/subscriptions/" + id, token(auth));
    }

    @GET @Path("/topics")
    public Response getTopics(@HeaderParam("Authorization") String auth,
                               @QueryParam("providerUrl") String url) {
        return providerClient.get(resolve(url), BASE_PATH + "/topics", token(auth));
    }

    @GET @Path("/features")
    public Response getFeatures(@HeaderParam("Authorization") String auth,
                                 @QueryParam("providerUrl") String url) {
        return providerClient.get(resolve(url), BASE_PATH + "/features", token(auth));
    }

    private String resolve(String url) {
        return (url != null && !url.isBlank()) ? url : providerUrls.split(",")[0].trim();
    }

    private String token(String auth) {
        return auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
    }
}
