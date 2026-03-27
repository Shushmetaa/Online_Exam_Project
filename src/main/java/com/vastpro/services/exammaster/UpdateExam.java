package com.vastpro.services.exammaster;

import java.util.Map;

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
			if(examName==null || examName.isEmpty()) {
	    		return ServiceUtil.returnError("Exam Name is required");
	    	}
	    	if(noOfQuestions == null || noOfQuestions.isEmpty()) {
	    		return ServiceUtil.returnError("No of Questions is required");
	    	}
	    	if(description==null ||  description.isEmpty()) {
	    		return ServiceUtil.returnError("description is required");
	    	}
	    	if(duration==null || duration.isEmpty()) {
	    		return ServiceUtil.returnError("Duration is required");
	    	}
	    	if(passPercentage==null) {
	    		return ServiceUtil.returnError("Pass Percentage is required");
	    	}
			LocalDispatcher dispatcher = dctx.getDispatcher();
	    	
	    	 Map<String, Object> result = dispatcher.runSync("updateExamAuto", context);

	         if (ServiceUtil.isError(result)) {
	             return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	         }
	         Map<String,Object> response= ServiceUtil.returnSuccess("Exam Updated Successfully");
	    		response.put("examId",result.get("examId"));
	    		return response;
			
			
		}catch(GenericServiceException e) {
			return ServiceUtil.returnError("Error updating exam: " + e.getMessage());
		}
		
	}

}
