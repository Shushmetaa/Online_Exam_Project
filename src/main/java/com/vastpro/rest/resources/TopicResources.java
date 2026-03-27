package com.vastpro.rest.resources;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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

import com.vastpro.javaservice.TopicMaster;

@Path("/admin/topic")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class TopicResources {

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
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/create")
	    public Map<String, Object> createTopics(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.createTopic(request, response);
	    }
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/examTopics/{examId}")
	    public Map<String, Object> getTopics(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.getTopic(request, response);
	    	
	    }
	    
	    @PUT
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/update")
	    public Map<String, Object> updateTopics(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.updateTopic(request, response);
	    	
	    }
	    
	    @DELETE
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Path("/delete")
	    public Map<String, Object> deleteTopics(@Context HttpServletRequest request, @Context HttpServletResponse response){
	    	
	    	return TopicMaster.deleteTopic(request, response);
	    	
	    }
	    
}
