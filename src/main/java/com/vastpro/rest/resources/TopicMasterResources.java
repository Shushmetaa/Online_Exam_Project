package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import com.vastpro.servicecall.TopicMasterCall;

@Path("/admin/topicmaster")
public class TopicMasterResources {

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Map<String, Object> createTopic(
            @FormParam("topicName") String topicName,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) {

        return TopicMasterCall.createTopic(topicName, request, response);
    }

    @GET
    @Path("/getAllTopics")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Map<String, Object> getAllTopics(
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) {

        return TopicMasterCall.getAllTopics(request, response);
    }
}