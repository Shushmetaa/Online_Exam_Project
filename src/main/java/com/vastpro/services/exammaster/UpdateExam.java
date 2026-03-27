package com.vastpro.services.exammaster;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class UpdateExam {
	
	public static Map<String, Object> updateExam(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId = (String) context.get("examId");
			String examName = (String) context.get("examName");
			String description = (String) context.get("description");
			String noOfQuestions = (String) context.get("noOfQuestions");
			String duration = (String) context.get("duration");
			String passPercentage = (String) context.get("passPercentage");
		    
			if (examId == null || examId.isEmpty()) {                        
			     return ServiceUtil.returnError("Exam ID is required");
			}
			LocalDispatcher dispatcher = dctx.getDispatcher();
			
			GenericValue userLogin = (GenericValue) context.get("userLogin");      
			
			 if (userLogin == null) {
		            return ServiceUtil.returnError("UserLogin is required");
		        }
			 
			Map<String, Object> update = new HashMap<>();
			
			update.put("examId", examId);
			update.put("examName", examName);
	    	update.put("description", description);
	    	update.put("noOfQuestions", noOfQuestions);
	    	update.put("duration", duration);
	    	update.put("passPercentage", passPercentage);
	    	update.put("userLogin", userLogin);
	    	
	    	 Map<String, Object> result = dispatcher.runSync("updateExamAuto", update);

	         if (ServiceUtil.isError(result)) {
	             return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	         }

	         return ServiceUtil.returnSuccess("Exam Updated Successfully");
			
			
		}catch(GenericServiceException e) {
			return ServiceUtil.returnError("Error updating exam: " + e.getMessage());
		}
		
	}

}
