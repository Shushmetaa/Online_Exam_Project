package com.vastpro.services.questionmaster;

import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class CreateQuestionMaster {
	
	public Map<String, Object> createQuestions(DispatchContext dctx, Map<String, ? extends Object> context){
		
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
			String questionType = (String) context.get("questionType");
			String difficultyLevel = (String) context.get("difficultyLevel");
			Double answerValue = (Double) context.get("answerValue");
			Double negativeMarkValue = (Double) context.get("negativeMarkValue");
			
			if(examId == null || examId.isEmpty()) {
				return ServiceUtil.returnError("Exam Id is required");
			}
			
			if(qId == null || qId.isEmpty()) {
				return ServiceUtil.returnError("QId is required");
			}
			
			if(topicId == null || topicId.isEmpty()) {
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
			
			if(questionType == null || questionType.isEmpty() ) {
				return ServiceUtil.returnError("Question typ is required");
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
			
//			GenericValue existing = EntityQuery.use(delegator)
//					                       .from("QuestionBankMasterB")
//					                       .where("examId", examId, "topicId", topicId)
//					                       .queryOne();
//			if(existing != null){
//			    return ServiceUtil.returnError("Question already exists");
//			}
			
			Map<String, Object> result = dispatcher.runSync("createQuestionMasterAuto", context);
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
			}
			else {
				return ServiceUtil.returnSuccess("Questions created successfully");
			}
			
		}catch( GenericServiceException e) {
			return ServiceUtil.returnError("Error in creating questions" + e.getMessage());
		}
	}

}
