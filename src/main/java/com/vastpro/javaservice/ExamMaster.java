package com.vastpro.javaservice;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class ExamMaster {
	
    public static Map<String,Object> createExam(@Context HttpServletRequest request, @Context HttpServletResponse response){
    	try {
    		Map<String, Object> result=new HashMap<>();
    		String examId = request.getParameter("examId");
	    	String examName = request.getParameter("examName");
	    	String description = request.getParameter("description");
	    	String noOfQuestions = request.getParameter("noOfQuestions");
	    	String duration = request.getParameter("duration");
	    	String passPercentage = request.getParameter("passPercentage");
	    	
    	    LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
    	    
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
    	    else {
    	    	return ServiceUtil.returnSuccess("Exam Created Successfully");
    	    }
    	}catch(Exception e) {
      		return ServiceUtil.returnError("Exam Creation Failed:" +e.getMessage());
    	}	    	
    }

}
