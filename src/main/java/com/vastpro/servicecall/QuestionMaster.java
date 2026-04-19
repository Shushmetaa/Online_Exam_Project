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

public class QuestionMaster {
	
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
	
	public static Map<String, Object> createQuestion(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			String examId = request.getParameter("examId");
			String topicId = request.getParameter("topicId");
			String questionDetail = request.getParameter("questionDetail");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String answer = request.getParameter("answer");
			Long numAnswers = Long.parseLong(request.getParameter("numAnswers").trim());
			String questionType = request.getParameter("questionType");
			String difficultyLevel = request.getParameter("difficultyLevel").trim();
			Double answerValue = Double.parseDouble(request.getParameter("answerValue").trim());
			Double negativeMarkValue = Double.parseDouble(request.getParameter("negativeMarkValue").trim());
			
			LocalDispatcher dispatcher= getDispatcher(request);
			
			GenericValue userLogin=EntityQuery.use(getDelegator(request))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> createData = new HashMap<>();
			
			createData.put("examId", examId);
			createData.put("topicId", topicId);
			createData.put("questionDetail", questionDetail);
			createData.put("optiona", optiona);
			createData.put("optionb", optionb);
			createData.put("optionc", optionc);
			createData.put("optiond", optiond);
			createData.put("optione", optione);
			createData.put("answer", answer);
			createData.put("numAnswers", numAnswers);
			createData.put("questionType", questionType);
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
			
			
		} catch (GenericServiceException | GenericEntityException e) {
			
			return ServiceUtil.returnError("Questions created failed" + e.getMessage());
		}
		
	}
	
	public static Map<String, Object> getQuestion(HttpServletRequest request, HttpServletResponse response) {
	    try {
	        String examId  = request.getParameter("examId");
	        String topicId = request.getParameter("topicId");

	        if (examId == null || examId.trim().isEmpty())
	            return ServiceUtil.returnError("examId is required");
	        if (topicId == null || topicId.trim().isEmpty())
	            return ServiceUtil.returnError("topicId is required");

	        LocalDispatcher dispatcher = getDispatcher(request);
	        GenericValue userLogin = EntityQuery.use(getDelegator(request))
	                .from("UserLogin").where("userLoginId", "admin").queryOne();

	        Map<String, Object> ids = new HashMap<>();
	        ids.put("examId",  examId);
	        ids.put("topicId", topicId);

	        // NEW — pagination
	        String viewSize  = request.getParameter("viewSize");
	        String viewIndex = request.getParameter("viewIndex");
	        ids.put("viewSize",  viewSize  != null ? viewSize  : "10");
	        ids.put("viewIndex", viewIndex != null ? viewIndex : "0");

	        // NEW — filters
	        ids.put("search",          request.getParameter("search"));
	        ids.put("questionType",    request.getParameter("questionType"));
	        ids.put("difficultyLevel", request.getParameter("difficultyLevel"));

	        ids.put("userLogin", userLogin);

	        Map<String, Object> result = dispatcher.runSync("getQuestionMaster", ids);
	        if (ServiceUtil.isError(result))
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	        return result;

	    } catch (GenericEntityException | GenericServiceException e) {
	        return ServiceUtil.returnError("Questions cannot be fetched: " + e.getMessage());
	    }
	}
	
	public static Map<String, Object> updateQuestion(HttpServletRequest request, HttpServletResponse response){

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
			String answer = request.getParameter("answer");
			String questionType = request.getParameter("questionType");
			String difficultyLevel = request.getParameter("difficultyLevel");
			
			if(difficultyLevel != null) difficultyLevel = difficultyLevel.trim();

			String answerValueStr = request.getParameter("answerValue");
			Double answerValue = (answerValueStr != null && !answerValueStr.trim().isEmpty()) 
					? Double.parseDouble(answerValueStr.trim()) : null;

			String negativeMarkStr = request.getParameter("negativeMarkValue");
			Double negativeMarkValue = (negativeMarkStr != null && !negativeMarkStr.trim().isEmpty()) 
			                           ? Double.parseDouble(negativeMarkStr.trim()) : null;

			String numAnswersStr = request.getParameter("numAnswers");
			Long numAnswers = (numAnswersStr != null && !numAnswersStr.trim().isEmpty()) 
			                  ? Long.parseLong(numAnswersStr.trim()) : null;
			
			LocalDispatcher dispatcher= getDispatcher(request);
	
			
			GenericValue userLogin=EntityQuery.use(getDelegator(request))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> updateData = new HashMap<>();
			updateData.put("examId", examId);
			updateData.put("qId", qId);
			updateData.put("topicId", topicId);
			updateData.put("questionDetail", questionDetail);
			updateData.put("optiona", optiona);
			updateData.put("optionb", optionb);
			updateData.put("optionc", optionc);
			updateData.put("optiond", optiond);
			updateData.put("optione", optione);
			updateData.put("answer", answer);
			updateData.put("numAnswers", numAnswers);
			updateData.put("questionType", questionType);
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
			
			LocalDispatcher dispatcher= getDispatcher(request);
			
			
			GenericValue userLogin=EntityQuery.use(getDelegator(request))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> delete = new HashMap<>();
		    
			delete.put("examId", examId);
			delete.put("qId", qId);
		    delete.put("userLogin", userLogin);
		    
		    Map<String, Object> result = dispatcher.runSync("deleteQuestionMaster", delete);
		    
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
	        
		    