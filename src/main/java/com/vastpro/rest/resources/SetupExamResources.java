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
import javax.ws.rs.QueryParam;
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
		 @Path("/getUnsetupExams")
		 @Produces(MediaType.APPLICATION_JSON)
		 public Map<String, Object> getUnsetupExams(@Context HttpServletRequest request, @Context HttpServletResponse response) {
			 
		     return SetupExam1.getUnsetupExams(request, response);
		 }
		 	
		 // user removal
		 @DELETE
		 @Produces(MediaType.APPLICATION_JSON)
		 @Path("/userdelete")
		 public Map<String, Object> softDeleteSetup(@QueryParam("examId") String examId, @QueryParam("partyId") String partyId,
		         @Context HttpServletRequest request, @Context HttpServletResponse response) {

		     return SetupExam1.softUserDeleteSetup(examId, partyId, request, response);
		 }
	 	
	 	//setup exam removes completely
	 	@DELETE
	 	@Produces(MediaType.APPLICATION_JSON)
	 	@Path("/softdelete")
	 	public Map<String, Object> softDeleteExamSetup(@QueryParam("examId") String examId, @Context HttpServletRequest request, @Context HttpServletResponse response){

	 	    return SetupExam1.softDeleteExamSetup(examId, request, response);
	 	}
	 	
	 	
	 	@POST
	 	@Produces(MediaType.APPLICATION_JSON)
	 	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 	@Path("/assignuser")
	 	public Map<String, Object> assignUser(
	 	    @FormParam("examId") String examId,
	 	    @FormParam("partyId") String partyId,
	 	    @FormParam("allowedAttempts") String allowedAttempts,
	 	    @FormParam("noOfAttempts") String noOfAttempts,
	 	    @FormParam("timeoutDays") String timeoutDays,
	 	    @Context HttpServletRequest request,
	 	    @Context HttpServletResponse response) {
	 	    return SetupExam1.assignUser(examId, partyId, allowedAttempts,  noOfAttempts, timeoutDays, 
	 	    		request, response);
	 	}
	 	
	 	@GET
	 	@Produces(MediaType.APPLICATION_JSON)
	 	@Path("/assignedusers/{examId}")
	 	public Map<String, Object> getAssignedUsers(
	 	    @PathParam("examId") String examId,
	 	    @Context HttpServletRequest request,
	 	    @Context HttpServletResponse response) {
	 	    return SetupExam1.getAssignedUsers(examId, request, response);
	 	}
	 	
	 	@POST
	 	@Produces(MediaType.APPLICATION_JSON)
	 	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 	@Path("/saveandpublish")
	 	public Map<String, Object> saveAndPublish(
	 	        @FormParam("examId")          String examId,
	 	        @FormParam("status") String status,
	 	        @FormParam("partyIds")        String partyIds,
	 	        @FormParam("allowedAttempts") String allowedAttempts,
	 	        @FormParam("noOfAttempts")    String noOfAttempts,
	 	        @FormParam("timeoutDays")     String timeoutDays,
	 	        @FormParam("openDate")        String openDate,
	 	        @FormParam("closeDate")       String closeDate,
	 	        @FormParam("whenExpires")     String whenExpires,
	 	        @FormParam("gradingMethod")   String gradingMethod,
	 	        @FormParam("shuffleQ")        String shuffleQ,
	 	        @FormParam("shuffleA")        String shuffleA,
	 	        @FormParam("sequential")      String sequential,
	 	        @FormParam("showResults")     String showResults,
	 	        @Context HttpServletRequest request,
	 	        @Context HttpServletResponse response) {

	 	    return SetupExam1.saveAndPublish( examId, status, partyIds, allowedAttempts, noOfAttempts, timeoutDays, openDate, closeDate, whenExpires, gradingMethod,
	 	        shuffleQ, shuffleA, sequential, showResults,
	 	        request, response);
	 	}
	 	
	 	@GET
	 	@Produces(MediaType.APPLICATION_JSON)
	 	@Path("/getAllAssignedUsers")
	 	public Map<String, Object> getAllAssignedUsers(
	 	    @Context HttpServletRequest request,
	 	    @Context HttpServletResponse response) {
	 	    return SetupExam1.getAllAssignedUsers(request, response);
	 	}
}
