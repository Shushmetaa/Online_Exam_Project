package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

import com.vastpro.servicecall.ExamMaster;
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
}