package com.vastpro.services.questionmaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetQuestionMaster {
	
	public Map<String, Object> getQuestions(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId = (String) context.get("examId");
			String topicId = (String) context.get("topicId");
//			String questionType = (String) context.get("questionType"); 
//		    String difficultyLevel = (String) context.get("difficultyLevel");

			if(examId == null || examId.isEmpty()) {
				return ServiceUtil.returnError("Exam Id is required");
			}
			
			if(topicId == null) {
				return ServiceUtil.returnError("Topic Id is required");
		   }
			
			Delegator delegator = dctx.getDelegator();
			
			 Map<String, Object> conditions = new HashMap<>();
	        conditions.put("examId", examId);

	        if (topicId != null && !topicId.isEmpty()) {
	            conditions.put("topicId", topicId);
	        }

//	        if (questionType != null && !questionType.isEmpty()) {
//	            conditions.put("questionType", questionType);
//	        }
//
//	        if (difficultyLevel != null && !difficultyLevel.isEmpty()) {
//	            conditions.put("difficultyLevel", difficultyLevel);
//	        }

	        List<GenericValue> getData = EntityQuery.use(delegator)
	                .from("QuestionBankMasterB")
	                .where(conditions)
	                .queryList();

	        Map<String, Object> result = ServiceUtil.returnSuccess("Questions fetched successfully");
	        result.put("questionList", getData != null ? getData : new ArrayList<>());
	        return result;

	         
		}catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error fetching topics: " + e.getMessage());
			
		}
	}

}
