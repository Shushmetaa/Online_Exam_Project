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
import com.vastpro.servicecall.QuestionMaster;


@Path("/admin/questions")
public class QuestionResources {
	
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/create")
    public Map<String, Object> createQuestions(@Context HttpServletRequest request, @Context HttpServletResponse response){
    	
    	return QuestionMaster.createQuestion(request, response);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/getAllExams")
    public Map<String, Object> getQuestions(@Context HttpServletRequest request, @Context HttpServletResponse response){
    	 
    	String examId = request.getParameter("examId");
    	String topicId = request.getParameter("topicId");
//    	String questionType = request.getParameter("questionType"); 
//    	String difficultyLevel = request.getParameter("difficultyLevel");
    	System.out.println("DEBUG getAllQuestions examId: " + examId);
    	return QuestionMaster.getQuestion(request, response);
    	
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/update")
    public Map<String, Object> updateQuestions(@Context HttpServletRequest request, @Context HttpServletResponse response){
    	
    	return QuestionMaster.updateQuestion(request, response);
    	
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/delete")
    public Map<String, Object> deleteQuestions(@Context HttpServletRequest request, @Context HttpServletResponse response){
    	
    	return QuestionMaster.deleteQuestion(request, response);
    	
    }

}
