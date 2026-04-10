package com.vastpro.rest.resources;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.vastpro.servicecall.LoginMaster;
import com.vastpro.servicecall.SignupMaster;

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
    @GET
    @Path("/getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getUsers(@Context HttpServletRequest request,
                                         @Context HttpServletResponse response) {
        return LoginMaster.getUsers(request, response);
    }
}