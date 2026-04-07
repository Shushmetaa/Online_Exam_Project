package com.vastpro.rest.resources;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.vastpro.servicecall.ExamMaster;
import com.vastpro.servicecall.SetupExam1;
import com.vastpro.servicecall.TopicMaster;

@Path("/admin/setup")
public class SetupExamResources {
	
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/setupexam")
	public Map<String, Object> setupExamination(
			@FormParam("examId") String examId,
		    @FormParam("setupType") String setupType,
		    @FormParam("setupDetails") String setupDetails,
		    @FormParam("partyId") String partyId,
		    @FormParam("allowedAttempts") String allowedAttempts,
		    @FormParam("noOfAttempts") String noOfAttempts,
		    @FormParam("timeoutDays") String timeoutDays,
			@Context HttpServletRequest request, @Context HttpServletResponse response){
		
		return SetupExam1.createSetup(examId, setupType, setupDetails, partyId, allowedAttempts, noOfAttempts, timeoutDays, request, response);
		
	}
	
	 @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/update")
	    public Map<String, Object> updateTopics(
	    		@FormParam("examId")  String examId,
	    	    @FormParam("setupType")  String setupType,
	    	    @FormParam("setupDetails") String setupDetails,
	    	    @FormParam("partyId") String partyId,
	    	    @FormParam("allowedAttempts") String allowedAttempts,
	    	    @FormParam("noOfAttempts") String noOfAttempts,
	    	    @FormParam("timeoutDays") String timeoutDays,
	    		@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return SetupExam1.updateSetup(examId, setupType, setupDetails, partyId, allowedAttempts, noOfAttempts,
	    			timeoutDays, request, response);
	    	
	    }
	 
	 	@GET
	    @Path("/getExams")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Map<String, Object> getExams(@Context HttpServletRequest request, @Context HttpServletResponse response) {
	       
	        return ExamMaster.getExams(request, response);
	    }
	 	
	 	// user removal
	 	@DELETE
	 	@Produces(MediaType.APPLICATION_JSON)
	 	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 	@Path("/userdelete")
	 	public Map<String, Object> softDeleteSetup(@FormParam("examId")  String examId,@FormParam("partyId") String partyId,
	 			@Context HttpServletRequest request, @Context HttpServletResponse response) {

	 	    return SetupExam1.softUserDeleteSetup(examId, partyId, request, response);
	 	}
	 	
	 	//setup exam removes completely
	 	@DELETE
	 	@Produces(MediaType.APPLICATION_JSON)
	 	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 	@Path("/softdelete")
	 	public Map<String, Object> softDeleteExamSetup(@FormParam("examId") String examId, @Context HttpServletRequest request, @Context HttpServletResponse response) {

	 	    return SetupExam1.softDeleteExamSetup(examId, request, response);
	 	}
}
