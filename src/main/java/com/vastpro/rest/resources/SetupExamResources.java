package com.vastpro.rest.resources;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.vastpro.servicecall.SetupExam1;
import com.vastpro.servicecall.TopicMaster;

@Path("/exam")
public class SetupExamResources {
	
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/setupexam")
	public Map<String, Object> setupExamination(@Context HttpServletRequest request, @Context HttpServletResponse response){
		
		return SetupExam1.createSetup(request, response);
		
	}
	
	 @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/update")
	    public Map<String, Object> updateTopics(
	    		@FormParam("examId")  String examId,
	    	    @FormParam("setupType")  String setupType,
	    	    @FormParam("setupDetails")  String setupDetails,
	    	    @FormParam("partyId") String partyId,
	    	    @FormParam("allowedAttempts") String allowedAttempts,
	    	    @FormParam("noOfAttempts") String noOfAttempts,
	    	    @FormParam("timeoutDays") String timeoutDays,
	    		@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return SetupExam1.updateSetup(examId, setupType, setupDetails, partyId, allowedAttempts, noOfAttempts,
	    			timeoutDays, request, response);
	    	
	    }

}
