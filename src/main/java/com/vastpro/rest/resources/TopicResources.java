package com.vastpro.rest.resources;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import com.vastpro.servicecall.TopicMaster;

@Path("/admin/topic")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class TopicResources {

	    
	    @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/create")
	    public Map<String, Object> createTopics(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.createTopic(request, response);
	    }
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("/examTopics/{examId}")
	    public Map<String, Object> getTopics(@PathParam("examId") String examId, @Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.getTopic(examId, request, response);
	    }
	    
	    @PUT
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/update")
	    public Map<String, Object> updateTopics(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.updateTopic(request, response);
	    	
	    }
	    
	    @DELETE
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/delete")
	    public Map<String, Object> deleteTopics(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.deleteTopic(request, response);
	    	
	    }
	    
}
