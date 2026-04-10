package com.vastpro.services.questionmaster;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class UpdateQuestionMaster {
	
	public static Map<String, Object> updateQuestions(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId = (String) context.get("examId");
			String qId = (String) context.get("qId");
			String topicId = (String) context.get("topicId");
			String questionDetail = (String) context.get("questionDetail");
			String optiona = (String) context.get("optiona");
			String optionb = (String) context.get("optionb");
			String optionc = (String) context.get("optionc");
			String optiond = (String) context.get("optiond");
			String optione = (String) context.get("optione");
			String answer = (String) context.get("answer");
			Long numAnswers = (Long) context.get("numAnswers");
			String questiontype = (String) context.get("questiontype");
			String difficultyLevel = (String) context.get("difficultyLevel");
			Double answerValue = (Double) context.get("answerValue");
			Double negativeMarkValue = (Double) context.get("negativeMarkValue");
			
			
			if(examId == null || examId.isEmpty()) {
				return ServiceUtil.returnError("Exam Id is required");
			}
			
			if(qId == null ) {
				return ServiceUtil.returnError("QId is required");
			}
			
			if(topicId == null) {
				return ServiceUtil.returnError("Topic Id is required");
			}
			
			if(questionDetail == null || questionDetail.isEmpty()) {
				return ServiceUtil.returnError("Question Details is required");
			}
			
			if(optiona == null || optiona.isEmpty()) {
				return ServiceUtil.returnError("option A is required");
			}
			
			if(optionb == null || optionb.isEmpty()) {
				return ServiceUtil.returnError("option B is required");
			}
			
			if(optionc == null || optionc.isEmpty()) {
				return ServiceUtil.returnError("option C is required");
			}
			
			if(optiond == null || optiond.isEmpty()) {
				return ServiceUtil.returnError("option D is required");
			}
			
			if(optione == null || optione.isEmpty()) {
				return ServiceUtil.returnError("option E is required");
			}
			
			if(answer == null || answer.isEmpty()) {
				return ServiceUtil.returnError("answer is required");
			}
			
			if(numAnswers == null) {
				return ServiceUtil.returnError("Number of answers is required");
			}
			
			if(questiontype == null) {
				return ServiceUtil.returnError("Question type is required");
			}
			
			if(difficultyLevel == null) {
				return ServiceUtil.returnError("Difficuilty type is required");
			}
			
			if(answerValue == null) {
				return ServiceUtil.returnError("Answer is required");
			}
			
			if(negativeMarkValue == null) {
				return ServiceUtil.returnError("Negative marks value is required");
			}
			
			Delegator delegator = dctx.getDelegator();
			
			LocalDispatcher dispatcher = dctx.getDispatcher();
			
			GenericValue existing_data = EntityQuery.use(delegator)
					                           .from("QuestionBankMasterB")
					                           .where("examId", examId, "qId", qId)
					                           .queryOne();
			
			if(existing_data == null) {
				return ServiceUtil.returnError("Questions not found");
			}
			
			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("examId", examId);
			updateMap.put("qId", qId);
			updateMap.put("topicId", topicId);
			updateMap.put("questionDetail", questionDetail);
			updateMap.put("optiona", optiona);
			updateMap.put("optionb", optionb);
			updateMap.put("optionc", optionc);
			updateMap.put("optiond", optiond);
			updateMap.put("optione", optione);
			updateMap.put("answer", answer);
			updateMap.put("numAnswers", numAnswers);
			updateMap.put("questiontype", questiontype);
			updateMap.put("difficultyLevel", difficultyLevel);
			updateMap.put("answerValue", answerValue);
			updateMap.put("negativeMarkValue", negativeMarkValue);
			updateMap.put("userLogin", context.get("userLogin")); 
			
			Map<String, Object> result = dispatcher.runSync("updateQuestionMasterAuto", updateMap);
			
			if(ServiceUtil.isError(result)) {
			    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
			}
			
			return ServiceUtil.returnSuccess("Question updated successfully");
			
			
			
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError("Questions failed to update: " + e.getMessage());
		}
	}

}
