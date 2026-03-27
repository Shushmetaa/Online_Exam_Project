package com.vastpro.javaservice;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class TopicMaster {
	
	public static Map<String, Object> createTopic(HttpServletRequest request, HttpServletResponse response){
		
		try {
			String examId = request.getParameter("examId");
			String topicId = request.getParameter("topicId");
			String topicName = request.getParameter("topicName");
			String percentage = request.getParameter("percentage");
			String startingQid = request.getParameter("startingQid");
			String endingQid = request.getParameter("endingQid");
			String questionsPerExam = request.getParameter("questionsPerExam");
			String topicPassPercentage = request.getParameter("topicPassPercentage");
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
		    
		    GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
		    		.from("UserLogin")
		    		.where("userLoginId", "admin")
		    		.queryOne();
		    
		    Map<String, Object> topicData = new HashMap<>();
		    
		    topicData.put("examId", examId);
		    topicData.put("topicId", topicId);
		    topicData.put("topicName", topicName);
		    topicData.put("percentage", percentage);
		    topicData.put("startingQid", startingQid);
		    topicData.put("endingQid", endingQid);
		    topicData.put("questionsPerExam", questionsPerExam);
		    topicData.put("topicPassPercentage", topicPassPercentage);
		    topicData.put("UserLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("topicMaster", topicData);
		    
		    if(ServiceUtil.isError(result)) {
		    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		    }
		    else {
		    	return ServiceUtil.returnSuccess("Topics created successfully");
		    }
		    
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	
	public static Map<String, Object> getTopic(HttpServletRequest request, HttpServletResponse response){
		try {
			

			String examId = request.getParameter("examId");
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
		    
		    GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
		    		.from("UserLogin")
		    		.where("userLoginId", "admin")
		    		.queryOne();
		    
		    Map<String, Object> id = new HashMap<>();
		    
		    id.put("examId", examId);
		    id.put("UserLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("getTopicMaster", id);
		    
		    if(ServiceUtil.isError(result)) {
		    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		    }
		    else {
		    	return ServiceUtil.returnSuccess();
		    }
			
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		
	}
	
	public static Map<String, Object> updateTopic(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			String topicName = request.getParameter("topicName");
			String percentage = request.getParameter("percentage");
			String startingQid = request.getParameter("startingQid");
			String endingQid = request.getParameter("endingQid");
			String questionsPerExam = request.getParameter("questionsPerExam");
			String topicPassPercentage = request.getParameter("topicPassPercentage");
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
		    
		    GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
		    		.from("UserLogin")
		    		.where("userLoginId", "admin")
		    		.queryOne();
		    
		    Map<String, Object> updateData = new HashMap<>();
		    
		    updateData.put("topicName", topicName);
		    updateData.put("percentage", percentage);
		    updateData.put("startingQid", startingQid);
		    updateData.put("endingQid", endingQid);
		    updateData.put("questionsPerExam", questionsPerExam);
		    updateData.put("topicPassPercentage", topicPassPercentage);
		    updateData.put("UserLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("updatetopicMaster", updateData);
			
		    if(ServiceUtil.isError(result)) {
		    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		    }
		    else {
		    	return ServiceUtil.returnSuccess("Topics created successfully");
		    }
			
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		
	}
	
	public static Map<String, Object> deleteTopic(HttpServletRequest request, HttpServletResponse response){
		return null;
		
	}

}
