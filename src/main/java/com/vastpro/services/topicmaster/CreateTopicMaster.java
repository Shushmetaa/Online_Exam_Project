package com.vastpro.services.topicmaster;

import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class CreateTopicMaster {
	
	public Map<String, Object> createTopic(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId = (String) context.get("examId");
			String topicId = (String) context.get("topicId");
			String topicName = (String) context.get("topicName");
			String percentage = (String) context.get("percentage");
			String startingQid = (String) context.get("startingQid");
			String endingQid = (String) context.get("endingQid");
			String topicPassPercentage = (String) context.get("topicPassPercentage");
			
			if(topicName == null || topicName.isEmpty()) {
				return ServiceUtil.returnError("Topic name is required");
			}
			if(percentage == null || percentage.isEmpty()) {
				return ServiceUtil.returnError("Percentage is required");
			}
			if(startingQid == null || startingQid.isEmpty()) {
				return ServiceUtil.returnError("startingqid is required");
			}
			if(endingQid == null || endingQid.isEmpty()) {
				return ServiceUtil.returnError("EndingQid is required");
			}
			if(topicPassPercentage == null || topicPassPercentage.isEmpty()) {
				return ServiceUtil.returnError("topic pass percentage for the exam is required");
			}
			
			Delegator delegator = dctx.getDelegator();
			
			GenericValue exam = EntityQuery.use(delegator)
				                           .from("ExamMaster")
				                           .where("examId", examId)
				                           .queryOne();
			String noOfQuestions = exam.getString("noOfQuestions");
			
			double pct = Double.parseDouble(percentage);
			double questions = Double.parseDouble(noOfQuestions);
			
			long questionsPerExam = Math.round((pct/100) * questions);
			
			List<GenericValue> existingTopics = EntityQuery.use(delegator)
			        .from("ExamTopicDetails")
			        .where("examId", examId)
			        .queryList();

			double existingTotal = 0;
			for(GenericValue topic : existingTopics) {
			    existingTotal += Double.parseDouble(topic.getString("percentage"));
			}

			if(existingTotal + pct > 100) {
			    return ServiceUtil.returnError("Total % exceeds 100. Already used: " + existingTotal + "%");
			}else {
				ServiceUtil.returnSuccess("% is correct proceed");
			}
			
			LocalDispatcher dispatcher = dctx.getDispatcher();
			
			Map<String, Object> result = dispatcher.runSync("createTopicMaster", context);
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
			}
			else {
				return ServiceUtil.returnSuccess("Topics created successfully");
			}
			
		}catch(GenericServiceException | GenericEntityException e) {
			return ServiceUtil.returnError("Topic Creation Failed:" +e.getMessage());
		}
	}

}
