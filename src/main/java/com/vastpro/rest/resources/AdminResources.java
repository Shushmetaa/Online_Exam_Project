package com.vastpro.rest.resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;

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
	    	
	    	return ExamMaster.createExam(request, response);
	    	
	    }
	    
	    @PUT
	    @Path("/update")
	    public Map<String, Object> updateExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {
	    	
	    	try {
	    		
	    		String examId = request.getParameter("examId");
	    		String examName = request.getParameter("examName");
	    		String description = request.getParameter("description");
	    		String noOfQuestions = request.getParameter("noOfQuestions");
	    		String duration = request.getParameter("duration");
	    		String passPercentage = request.getParameter("passPercentage");
		    	
		    	LocalDispatcher dispatcher = getDispatcher();

		    	
		    	GenericValue userLogin = EntityQuery.use(getDelegator())
	                    .from("UserLogin")
	                    .where("userLoginId", "admin")
	                    .queryOne();
		    	
		    	Map<String, Object> updateData = new HashMap<>();
		    
		    	updateData.put("examId", examId);
		    	updateData.put("examName", examName);
		    	updateData.put("description", description);
		    	updateData.put("noOfQuestions", noOfQuestions);
		    	updateData.put("duration", duration);
		    	updateData.put("passPercentage", passPercentage);
		    	updateData.put("userLogin", userLogin);
		    	
		    	Map<String, Object> result = dispatcher.runSync("updateExam", updateData);
		    	
		    	response.setStatus(200);
		    	response.getWriter().write("success");
		    	
		    	return result;
		    	
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    		return null;
	    	}
			
	    }
	    @PUT
	    @Path("/retire")
	    public Map<String, Object> retireExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {
			try {
			String examId = request.getParameter("examId");
			String lastModifiedByUserLogin = request.getParameter("lastModifiedByUserLogin");
			
			if (examId == null || examId.isEmpty())
	            return ServiceUtil.returnError("Exam ID is required");
			
			LocalDispatcher dispatcher=getDispatcher();
			
			GenericValue userLogin = EntityQuery.use(getDelegator())
                    .from("UserLogin")
                    .where("userLoginId", "admin")
                    .queryOne();

			Map<String, Object> retireData = new HashMap<>();
            retireData.put("examId",examId);
            retireData.put("lastModifiedByUserLogin","admin");
            retireData.put("userLogin", userLogin);
			
            Map<String, Object> result = dispatcher.runSync("retireExam", retireData);

            response.setStatus(200);
            response.getWriter().write("success");
            
	    	
            return result;
				
			}catch (GenericEntityException | GenericServiceException | IOException e) {
	            return ServiceUtil.returnError("Error retiring exam: " + e.getMessage());
				
			}
		}
	    

}
