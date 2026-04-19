package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.vastpro.servicecall.ExamMaster;


@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class AdminResources {

	    
	    @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/create")
	    public Map<String,Object> createExam(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    
	    	return ExamMaster.createExam(request, response);
	
	    }
	    
	    @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/update")
	    public Map<String, Object> updateExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {
	    
	    	return ExamMaster.updateExam(request, response);
	    }
	    
	    @PUT
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/retire")
	    public Map<String, Object> retireExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {

	    	return ExamMaster.retireExam(request, response);
		}

	    @DELETE
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("/delete/{examId}")
	    public Map<String, Object> deleteExam( @PathParam("examId") String examId,@Context HttpServletRequest request, 
	        @Context HttpServletResponse response) {
	    	 return ExamMaster.deleteExam(request, response, examId);
	    }
	    
	    @GET
	    @Path("/getExams")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Map<String, Object> getExams(@Context HttpServletRequest request,
	                                         @Context HttpServletResponse response) {
	       
	        return ExamMaster.getExams(request, response);
	    }
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("/getNum")
	    public Map<String, Object> getStats(
	        @Context HttpServletRequest request,
	        @Context HttpServletResponse response) {
	        return ExamMaster.getNums(request, response);
	    }
	    @GET
	    @Path("/searchExams")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Map<String, Object> searchExams(@QueryParam("keyword") String keyword,@Context HttpServletRequest request,
	            @Context HttpServletResponse response) {
	        request.setAttribute("keyword", keyword);
	        return ExamMaster.searchAllExams(request, response);
	    }
	    
	    @GET
	    @Path("/getAllUsers")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Map<String, Object> getAllUsers(@QueryParam("keyword") String keyword,@Context HttpServletRequest request,
	            @Context HttpServletResponse response) {
	        request.setAttribute("keyword", keyword);
	        return ExamMaster.getAllUsers(request, response);
	    }
	    
}
