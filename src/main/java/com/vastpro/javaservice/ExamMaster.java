package com.vastpro.javaservice;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class ExamMaster {
	
    public static Map<String,Object> createExam(HttpServletRequest request, HttpServletResponse response){
    	try {
<<<<<<< Updated upstream
    	
=======
  
>>>>>>> Stashed changes
    		String examId = request.getParameter("examId");
	    	String examName = request.getParameter("examName");
	    	String description = request.getParameter("description");
	    	String noOfQuestions = request.getParameter("noOfQuestions");
	    	String duration = request.getParameter("duration");
	    	String passPercentage = request.getParameter("passPercentage");
	    	
    	    LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
    	    
    	    if (dispatcher == null) {
                return ServiceUtil.returnError("Dispatcher is null");
            }

    	    GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
    	    		.from("UserLogin")
    	    		.where("userLoginId", "admin")
    	    		.queryOne();
    	    
    	    Map<String, Object> createData =new HashMap<>();
    	    createData.put("examId", examId);
    	    createData.put("examName", examName);
    	    createData.put("description", description);
    	    createData.put("noOfQuestions",noOfQuestions);
    	    createData.put("duration",  duration);
    	    createData.put("passPercentage", passPercentage);
    	    createData.put("userLogin", userLogin);
    	    
    	    Map<String,Object> serviceResult=dispatcher.runSync("createExam", createData);
    	    
    	    if(ServiceUtil.isError(serviceResult)) {
    	    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
    	    }
    	    
    	    Map<String,Object> responseMap = ServiceUtil.returnSuccess("Exam Created Successfully");
            responseMap.put("examId", serviceResult.get("examId"));

            return responseMap;
    	}catch(Exception e) {
      		return ServiceUtil.returnError("Exam Creation Failed:" +e.getMessage());
    	}	 
    }
    	public static Map<String,Object> updateExam(@Context HttpServletRequest request, @Context HttpServletResponse response){
			
    		try {
	    		
	    		String examId = request.getParameter("examId");
	    		String examName = request.getParameter("examName");
	    		String description = request.getParameter("description");
	    		String noOfQuestions = request.getParameter("noOfQuestions");
	    		String duration = request.getParameter("duration");
	    		String passPercentage = request.getParameter("passPercentage");
		    	
	    		if (examId == null || examId.isEmpty()) {
	    	            return ServiceUtil.returnError("Exam ID is required");
	    	        }
	    		LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");

	    		GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
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
		    	
		    	return result;
		    	
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    		return null;
	    	}
    }
    	public static Map<String,Object> retireExam(@Context HttpServletRequest request, @Context HttpServletResponse response){
    		try {
    			String examId = request.getParameter("examId");
    			String lastModifiedByUserLogin = request.getParameter("lastModifiedByUserLogin");
    			
    			if (examId == null || examId.isEmpty())
    	            return ServiceUtil.returnError("Exam ID is required");
    			
    			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");

	    		GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
	    	    		.from("UserLogin")
	    	    		.where("userLoginId", "admin")
	    	    		.queryOne();

    			Map<String, Object> retireData = new HashMap<>();
                retireData.put("examId",examId);
                retireData.put("lastModifiedByUserLogin","admin");
                retireData.put("userLogin", userLogin);
    			
                Map<String, Object> result = dispatcher.runSync("retireExam", retireData);
        
    	    	
                return result;
    				
    			}catch (GenericEntityException | GenericServiceException e) {
    	            return ServiceUtil.returnError("Error retiring exam: " + e.getMessage());
    				
    			}
    		}
		public static Map<String, Object> deleteExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {
			try {
		        String examId = request.getParameter("examId");

		        if (examId == null || examId.isEmpty())
		            return ServiceUtil.returnError("Exam ID is required");

		        LocalDispatcher dispatcher =
		            (LocalDispatcher) request.getAttribute("dispatcher");

		        GenericValue userLogin = EntityQuery
		                .use((Delegator) request.getAttribute("delegator"))
		                .from("UserLogin")
		                .where("userLoginId", "admin")
		                .queryOne();

		        Map<String, Object> deleteData = new HashMap<>();
		        deleteData.put("examId",    examId);
		        deleteData.put("userLogin", userLogin);

		        Map<String, Object> result =
		            dispatcher.runSync("deleteExam", deleteData);

		        return result;

		    } catch (GenericEntityException | GenericServiceException e) {
		        return ServiceUtil.returnError(
		            "Error deleting exam: " + e.getMessage());
		    }
		}

}
