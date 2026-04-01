package com.vastpro.servicecall;

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
	
	private static LocalDispatcher getDispatcher(HttpServletRequest request) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
        }
        return dispatcher;
    }

    private static Delegator getDelegator(HttpServletRequest request) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (delegator == null) {
            delegator = (Delegator) request.getSession().getServletContext().getAttribute("delegator");
        }
        return delegator;
    }
	
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
			
			LocalDispatcher dispatcher=getDispatcher(request);
		    
		    GenericValue userLogin=EntityQuery.use(getDelegator(request))
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
		    topicData.put("userLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("createTopicMaster", topicData);
		    
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
	
	public static Map<String, Object> getTopic(String examId, HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			
			LocalDispatcher dispatcher=getDispatcher(request);
		    
		    GenericValue userLogin=EntityQuery.use(getDelegator(request))
		    		.from("UserLogin")
		    		.where("userLoginId", "admin")
		    		.queryOne();
		    
		    Map<String, Object> id = new HashMap<>();
		    
		    id.put("examId", examId);
		    id.put("userLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("getTopicMaster", id);
		    
		    if(ServiceUtil.isError(result)) {
		    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		    }
		    
		    Map<String, Object> response2 = ServiceUtil.returnSuccess();
            response2.put("topicList", result.get("topicList"));
            return response2;
			
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		
	}
	
	public static Map<String, Object> updateTopic(String examId, String topicId, String topicName, String percentage,
			String startingQid, String endingQid, String questionsPerExam, String topicPassPercentage, HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			LocalDispatcher dispatcher=getDispatcher(request);
		    
		    GenericValue userLogin=EntityQuery.use(getDelegator(request))
		    		.from("UserLogin")
		    		.where("userLoginId", "admin")
		    		.queryOne();
		    
		    Map<String, Object> updateData = new HashMap<>();
		    
		    updateData.put("examId", examId);  
		    updateData.put("topicId", topicId);  
		    updateData.put("topicName", topicName);
		    updateData.put("percentage", percentage);
		    updateData.put("startingQid", startingQid);
		    updateData.put("endingQid", endingQid);
		    updateData.put("questionsPerExam", questionsPerExam);
		    updateData.put("topicPassPercentage", topicPassPercentage);
		    updateData.put("userLogin", userLogin);
		    
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
	
	public static Map<String, Object> deleteTopic(String examId, String topicId,HttpServletRequest request, HttpServletResponse response){
		try {

		        if (examId == null || examId.isEmpty()) {
		            return ServiceUtil.returnError("Exam ID is required");
		        }
		        if (topicId == null || topicId.isEmpty()) {
		            return ServiceUtil.returnError("Topic ID is required");
		        }    
		            LocalDispatcher dispatcher = getDispatcher(request);

		            GenericValue userLogin = EntityQuery.use(getDelegator(request))
		                    .from("UserLogin")
		                    .where("userLoginId", "admin")
		                    .queryOne();

		            Map<String, Object> input = new HashMap<>();
		            input.put("examId", examId);
		            input.put("topicId", topicId);
		            input.put("userLogin", userLogin); 

		            Map<String, Object> result = dispatcher.runSync("deleteTopicMaster", input);

		            if (ServiceUtil.isError(result)) {
		                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		            }

		            return ServiceUtil.returnSuccess("Topic deleted successfully");   
			
		}catch (GenericEntityException | GenericServiceException e) {
	        return ServiceUtil.returnError("Error deleting topic: " + e.getMessage());
	    }
		
	}

}
