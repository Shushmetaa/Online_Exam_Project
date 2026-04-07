package com.vastpro.rest.resources;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
	    
	    @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/update")
	    public Map<String, Object> updateTopics(
	    		@FormParam("examId")  String examId,
	    	    @FormParam("topicId")  String topicId,
	    	    @FormParam("topicName")  String topicName,
	    	    @FormParam("percentage") String percentage,
	    	    @FormParam("startingQid") String startingQid,
	    	    @FormParam("endingQid") String endingQid,
	    	    @FormParam("questionsPerExam") String questionsPerExam,
	    	    @FormParam("topicPassPercentage") String topicPassPercentage,
	    		@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.updateTopic(examId, topicId, topicName, percentage, startingQid, endingQid, questionsPerExam, 
	    			topicPassPercentage, request, response);
	    	
	    }
	    
	    @DELETE
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/delete")
	    public Map<String, Object> deleteTopics(@FormParam("examId")  String examId, @FormParam("topicId") String topicId, 
	    		@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.deleteTopic(examId, topicId, request, response);
	    	
	    }
	    
}
