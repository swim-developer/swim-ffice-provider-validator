package com.github.swim_developer.validator.ffice.provider.infrastructure.rest;

import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConformanceTestPort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.List;
import java.util.Map;

@Path("/api/conformance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConformanceTestResource {

    @Inject ConformanceTestPort conformanceTest;
    @ConfigProperty(name = "swim.provider.api.urls") String providerUrls;

    @GET @Path("/scenarios")
    public Response listScenarios() {
        return Response.ok(Map.of("scenarios", List.of(
            Map.of("id", "API-01", "name", "Subscribe — Happy Path"),
            Map.of("id", "API-02", "name", "List Subscriptions"),
            Map.of("id", "API-03", "name", "Get Topics"),
            Map.of("id", "API-04", "name", "Unsubscribe"),
            Map.of("id", "DM-01", "name", "Required Fields in Response"),
            Map.of("id", "DM-02", "name", "Initial PAUSED Status"),
            Map.of("id", "DM-03", "name", "Topics Returns FficeService"),
            Map.of("id", "DM-04", "name", "message_type Filter Persisted"),
            Map.of("id", "WFS-01", "name", "WFS GetFeature Query")
        ))).build();
    }

    @POST @Path("/run/{scenarioId}")
    public Response runScenario(@PathParam("scenarioId") String scenarioId,
                                @QueryParam("providerUrl") String url,
                                @HeaderParam("Authorization") String auth) {
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        String targetUrl = (url != null && !url.isBlank()) ? url : providerUrls.split(",")[0].trim();
        return Response.ok(conformanceTest.executeTest(scenarioId, targetUrl, token)).build();
    }
}
