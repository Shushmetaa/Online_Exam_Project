package com.vastpro.services.exammaster;

import java.util.Map;

import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class CreateExam {
    public static Map<String,Object> createExam(DispatchContext dctx,Map<String, ? extends Object> context){
    	
    	String examName  =   (String) context.get("examName");
        String description = (String) context.get("description");
        String noOfQuestions = (String) context.get("noOfQuestions");
		String duration = (String) context.get("duration");
		String passPercentage = (String) context.get("passPercentage");
        
        LocalDispatcher dispatcher=dctx.getDispatcher();
    	
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
      	try {
    		Map<String,Object> result= dispatcher.runSync("createExamAuto", context);
    		
    		if(ServiceUtil.isError(result)) {
    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
    		}
    		
    		Map<String,Object> response= ServiceUtil.returnSuccess("Exam Created Succesfully");
    		response.put("examId",result.get("examId"));
    		return response;
    		
      	}
      	catch(GenericServiceException e) {
      		return ServiceUtil.returnError("Exam Creation Failed:" +e.getMessage());
      	}
    }
}
