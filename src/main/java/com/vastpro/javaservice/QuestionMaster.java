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

public class QuestionMaster {
	
	public static Map<String, Object> createQuestion(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			String examId = request.getParameter("examId");
			String qId = request.getParameter("qId");
			String topicId = request.getParameter("topicId");
			String questionDetail = request.getParameter("questionDetail");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String numAnswers = request.getParameter("numAnswers");
			String questiontype = request.getParameter("questiontype");
			String difficultyLevel = request.getParameter("difficultyLevel");
			String answerValue = request.getParameter("answerValue");
			String negativeMarkValue = request.getParameter("negativeMarkValue");
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
	
			
			GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> createData = new HashMap<>();
			
			createData.put("examId", examId);
			createData.put("qId", qId);
			createData.put("topicId", topicId);
			createData.put("questionDetail", questionDetail);
			createData.put("optiona", optiona);
			createData.put("optiona", optiona);
			createData.put("optiona", optiona);
			createData.put("optiona", optiona);
			createData.put("optiona", optiona);
			createData.put("numAnswers", numAnswers);
			createData.put("questiontype", questiontype);
			createData.put("difficultyLevel", difficultyLevel);
			createData.put("answerValue", answerValue);
			createData.put("negativeMarkValue", negativeMarkValue);
			createData.put("userLogin", userLogin);
			
			Map<String, Object> result = dispatcher.runSync("createQuestionMaster", createData);
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
			}
			else {
				return ServiceUtil.returnSuccess("Questions created successfully");
			}
			
			
		} catch (GenericEntityException | GenericServiceException e) {
			
			return ServiceUtil.returnError("Questions created failed" + e.getMessage());
		}
		
	}
	
	public static Map<String, Object> getQuestion(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			String examId = request.getParameter("examId");
			String topicId = request.getParameter("topicId");
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
	
			
			GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> ids = new HashMap<>();
		    
		    ids.put("examId", examId);
		    ids.put("topicId", topicId);
		    ids.put("UserLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("getTopicMaster", ids);
		    
		    if(ServiceUtil.isError(result)) {
		    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		    }
		    else {
		    	return ServiceUtil.returnSuccess();
		    }
			
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError("Questions cannot be fetched" + e.getMessage());
		}
		
	}
	
	public static Map<String, Object> updateQuestion(HttpServletRequest request, HttpServletResponse response){

		try {
			
			String topicId = request.getParameter("topicId");
			String questionDetail = request.getParameter("questionDetail");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String numAnswers = request.getParameter("numAnswers");
			String questiontype = request.getParameter("questiontype");
			String difficultyLevel = request.getParameter("difficultyLevel");
			String answerValue = request.getParameter("answerValue");
			String negativeMarkValue = request.getParameter("negativeMarkValue");
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
	
			
			GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> updateData = new HashMap<>();
			
			updateData.put("topicId", topicId);
			updateData.put("questionDetail", questionDetail);
			updateData.put("optiona", optiona);
			updateData.put("optiona", optiona);
			updateData.put("optiona", optiona);
			updateData.put("optiona", optiona);
			updateData.put("optiona", optiona);
			updateData.put("numAnswers", numAnswers);
			updateData.put("questiontype", questiontype);
			updateData.put("difficultyLevel", difficultyLevel);
			updateData.put("answerValue", answerValue);
			updateData.put("negativeMarkValue", negativeMarkValue);
			updateData.put("userLogin", userLogin);
			
			Map<String, Object> result = dispatcher.runSync("updateQuestionMaster", updateData);
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
			}
			else {
				return ServiceUtil.returnSuccess("Questions updated successfully");
			}
			
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError("Questions failed to update" + e.getMessage());
		}
		
	}

	public static Map<String, Object> deleteQuestion(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			String examId = request.getParameter("examId");
			String qId = request.getParameter("qId");
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
			
			
			GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> delete = new HashMap<>();
		    
			delete.put("examId", examId);
		    delete.put("UserLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("getTopicMaster", delete);
		    
		    if(ServiceUtil.isError(result)) {
		    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		    }
		    else {
		    	return ServiceUtil.returnSuccess();
		    }
			
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError("Questions cannot be deleted" + e.getMessage());
		}
	
	}

}
