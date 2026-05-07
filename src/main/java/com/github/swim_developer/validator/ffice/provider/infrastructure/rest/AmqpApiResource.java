package com.github.swim_developer.validator.ffice.provider.infrastructure.rest;

import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConnectionTrackerPort;
import com.github.swim_developer.validator.ffice.provider.domain.port.in.MessagePort;
import com.github.swim_developer.validator.ffice.provider.infrastructure.rest.dto.ReceivedMessageDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/api/amqp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AmqpApiResource {

    @Inject ConnectionTrackerPort connectionTracker;
    @Inject MessagePort messagePort;

    @GET @Path("/status")
    public Map<String, Object> status(@QueryParam("userId") String userId) {
        return Map.of("connected", connectionTracker.isConnected(userId),
            "receivers", connectionTracker.getReceiverStatus(userId));
    }

    @POST @Path("/connect")
    public Response connect(Map<String, Object> body) {
        connectionTracker.connect(
            (String) body.get("userId"), (String) body.get("token"),
            (String) body.get("host"), ((Number) body.get("port")).intValue(),
            (String) body.get("username"), (String) body.get("password"));
        return Response.ok(Map.of("status", "connected")).build();
    }

    @POST @Path("/disconnect")
    public Response disconnect(Map<String, String> body) {
        connectionTracker.disconnect(body.get("userId"));
        return Response.ok(Map.of("status", "disconnected")).build();
    }

    @POST @Path("/heartbeat")
    public Response heartbeat(Map<String, String> body) {
        connectionTracker.heartbeat(body.get("userId"), body.get("token"));
        return Response.noContent().build();
    }

    @GET @Path("/messages/{subscriptionId}")
    public Response getMessages(@PathParam("subscriptionId") String subscriptionId) {
        var messages = messagePort.findBySubscriptionId(subscriptionId)
            .stream().map(ReceivedMessageDto::from).toList();
        return Response.ok(messages).build();
    }
}
