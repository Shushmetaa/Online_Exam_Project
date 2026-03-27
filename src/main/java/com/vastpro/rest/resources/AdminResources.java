package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

import com.vastpro.javaservice.ExamMaster;


@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class AdminResources {


	  @Context
	  private HttpServletRequest request;
	  
	  @Context
	  private HttpServletResponse response;

	   @Context
	   private ServletContext servletContext;  

	    
	    private Delegator getDelegator() {
	        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
	        if (delegator == null) {
	            delegator = DelegatorFactory.getDelegator("default");
	        }
	        return delegator;
	    }

	  
	    private LocalDispatcher getDispatcher() {
	        LocalDispatcher dispatcher = 
	            (LocalDispatcher) servletContext.getAttribute("dispatcher");
	        if (dispatcher == null) {
	            dispatcher = ServiceContainer.getLocalDispatcher(
	                "exam",   
	                getDelegator()
	            );
	        }
	        return dispatcher;
	    }
	    
	    @POST
	    @Path("/create")
	    public Map<String,Object> createExam(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	request.setAttribute("delegator", getDelegator());
	        request.setAttribute("dispatcher", getDispatcher());

	    	return ExamMaster.createExam(request, response);
	    	
	    }
	    
	    @PUT
	    @Path("/update")
	    public Map<String, Object> updateExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {
	    	request.setAttribute("delegator", getDelegator());
	        request.setAttribute("dispatcher", getDispatcher());
	    	return ExamMaster.updateExam(request, response);
	    }
	    @PUT
	    @Path("/retire")
	    public Map<String, Object> retireExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {
			
	    	request.setAttribute("delegator", getDelegator());
	    	request.setAttribute("dispatcher", getDispatcher());
	    	return ExamMaster.retireExam(request, response);
		}
	    
}
