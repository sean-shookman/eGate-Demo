package com.demo.api.service;

import com.demo.api.message.MessageResponse;
import com.sun.jersey.spi.resource.Singleton;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Sean Shookman
 */
@Named
@Singleton
@Path("/message")
public class MessageService {

    private static final String MESSAGE = "Hello World. I am a RESTFUL API Get Request!";

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getMessage() {

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage(MESSAGE);

        return Response.ok(messageResponse).build();
    }
}
