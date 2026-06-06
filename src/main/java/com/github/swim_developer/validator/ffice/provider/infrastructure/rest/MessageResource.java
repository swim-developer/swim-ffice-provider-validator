package com.github.swim_developer.validator.ffice.provider.infrastructure.rest;

import com.github.swim_developer.validator.ffice.provider.domain.port.in.MessagePort;
import com.github.swim_developer.validator.ffice.provider.infrastructure.rest.dto.ReceivedMessageDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/messages")
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

    @Inject MessagePort messagePort;

    @GET
    public Response recentMessages(@QueryParam("limit") @DefaultValue("50") int limit) {
        List<ReceivedMessageDto> messages = messagePort.findRecent(limit)
            .stream().map(ReceivedMessageDto::from).toList();
        return Response.ok(messages).build();
    }

    @GET @Path("/subscription/{subscriptionId}")
    public Response bySubscription(@PathParam("subscriptionId") String subscriptionId) {
        List<ReceivedMessageDto> messages = messagePort.findBySubscriptionId(subscriptionId)
            .stream().map(ReceivedMessageDto::from).toList();
        return Response.ok(messages).build();
    }
}
