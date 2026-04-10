package com.vastpro.services.questionmaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetQuestionMaster {
	
	public static Map<String, Object> getQuestions(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId = (String) context.get("examId");
			String topicId = (String) context.get("topicId");
			
			if(examId == null || examId.isEmpty()) {
				return ServiceUtil.returnError("Exam Id is required");
			}
			
			if(topicId == null) {
				return ServiceUtil.returnError("Topic Id is required");
			}
			
			Delegator delegator = dctx.getDelegator();
			
			List<GenericValue> getData = EntityQuery.use(delegator)
					                                .from("QuestionBankMasterB")
					                                .where("examId", examId, "topicId", topicId)
					                                .queryList();
			
			Map<String, Object> result = ServiceUtil.returnSuccess();
			result.put("questionList", getData != null ? getData : new ArrayList<>());
			return result;
	         
		}catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error fetching topics: " + e.getMessage());
			
		}
	}

}
