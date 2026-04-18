package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.vastpro.servicecall.ExamMaster;
import com.vastpro.servicecall.LoginMaster;
import com.vastpro.servicecall.LogoutMaster;
import com.vastpro.servicecall.SignupMaster;
import com.vastpro.servicecall.UserMaster;

@Path("/user")

public class UserResources {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/signup")
    public Map<String,Object> signupUser(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    
    	return SignupMaster.signupUser(request, response);

    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/login")
    public Map<String,Object> loginUser(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    
    	return LoginMaster.loginUser(request, response);

    }
//    @GET
//    @Path("/getUsers")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Map<String, Object> getUsers(@Context HttpServletRequest request,
//                                         @Context HttpServletResponse response) {
//        return LoginMaster.getUsers(request, response);
//    }
    @GET
    @Path("/getExams")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getAssignedExams(@Context HttpServletRequest request,@Context HttpServletResponse response) {
        return UserMaster.getAssignedExams(request, response);
    }
    
    @GET
    @Path("/getNum")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getUserStats(@Context HttpServletRequest request,@Context HttpServletResponse response) {
        return UserMaster.getUserStats(request, response);
    }
    
    @POST
    @Path("/verifyExamPassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Map<String, Object> verifyExamPassword( @FormParam("password") String password, @FormParam("examId") String examId, 
    		@Context HttpServletRequest request, @Context HttpServletResponse response) {
            	
        return UserMaster.verifyExamPassword(password, examId, request, response);
    }

    @GET
    @Path("/getUserInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getUserInfo(@Context HttpServletRequest request,@Context HttpServletResponse response ){
        return UserMaster.getUserInfo(request, response);	         
    }
    
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> logout(@Context HttpServletRequest request,@Context HttpServletResponse response ){
       return LogoutMaster.logout(request, response);	         
    }

@GET
@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public Map<String, Object> checkSession(@Context HttpServletRequest request,
                                         @Context HttpServletResponse response) {
    return LoginMaster.checkSession(request, response);
}
@GET
@Path("/searchExams")
@Produces(MediaType.APPLICATION_JSON)
public Map<String, Object> searchExams(@QueryParam("keyword") String keyword,@Context HttpServletRequest request,
        @Context HttpServletResponse response) {
    request.setAttribute("keyword", keyword);
    return ExamMaster.searchAssignedExams(request, response);
}
//@GET
//@Path("/session1")
//@Produces(MediaType.APPLICATION_JSON)
//public Map<String, Object> checkSession(@Context HttpServletRequest request,
//                                         @Context HttpServletResponse response) {
//    return LoginMaster.checkSession(request, response);
//}
}