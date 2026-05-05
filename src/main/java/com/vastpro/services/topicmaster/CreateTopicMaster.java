package com.vastpro.services.topicmaster;

import java.util.HashMap;
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
	
	public static Map<String, Object> createTopic(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId             = (String) context.get("examId");
			String topicId            = (String) context.get("topicId"); 
			String topicName          = (String) context.get("topicName");
			String percentage         = (String) context.get("percentage");
			String startingQid        = (String) context.get("startingQid");
			String endingQid          = (String) context.get("endingQid");
			String topicPassPercentage = (String) context.get("topicPassPercentage");
			GenericValue userLogin    = (GenericValue) context.get("userLogin");
			
			// ✅ Extract partyId from logged-in admin
			String partyId = (userLogin != null) ? userLogin.getString("partyId") : null;
			
			if (partyId == null || partyId.isEmpty()) {
				return ServiceUtil.returnError("Admin partyId is required. Please log in again.");
			}
			
			// Validations
			if (topicName == null || topicName.isEmpty()) {
				return ServiceUtil.returnError("Topic name is required");
			}
			if (percentage == null || percentage.isEmpty()) {
				return ServiceUtil.returnError("Percentage is required");
			}
			if (startingQid == null || startingQid.isEmpty()) {
				return ServiceUtil.returnError("Starting QID is required");
			}
			if (endingQid == null || endingQid.isEmpty()) {
				return ServiceUtil.returnError("Ending QID is required");
			}
			if (topicPassPercentage == null || topicPassPercentage.isEmpty()) {
				return ServiceUtil.returnError("Topic pass percentage is required");
			}
			
			Delegator delegator       = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();

			// Get exam to calculate questionsPerExam
			GenericValue exam = EntityQuery.use(delegator)
				                           .from("ExamMaster")
				                           .where("examId", examId)
				                           .queryOne();

			if (exam == null) {
                return ServiceUtil.returnError("Exam not found for examId: " + examId);
            }
			
			String noOfQuestions = exam.getString("noOfQuestions");

			double pct                       = Double.parseDouble(percentage);
			double questions                 = Double.parseDouble(noOfQuestions);
			long calculatedQuestionsPerExam  = Math.round((pct / 100) * questions);
			
			// Check total percentage not exceeding 100
			List<GenericValue> existingTopics = EntityQuery.use(delegator)
			        .from("ExamTopicDetails")
			        .where("examId", examId)
			        .queryList();
			
			double existingTotal = 0;
			for (GenericValue topic : existingTopics) {
			    existingTotal += Double.parseDouble(topic.getString("percentage"));
			}
			if (existingTotal + pct > 100) {
			    return ServiceUtil.returnError("Total % exceeds 100. Already used: " + existingTotal + "%");
			}

			// CASE 1 — topicId is empty = NEW topic typed by admin, auto generate ID
			if (topicId == null || topicId.isEmpty()) {

				// ✅ Only look at THIS admin's topics for ID generation
			    List<GenericValue> allTopics = EntityQuery.use(delegator)
			            .from("TopicMaster")
			            .where("partyId", partyId)
			            .queryList();

			    long maxTopicId = 0;
			    for (GenericValue t : allTopics) {
			        try {
			            long id = Long.parseLong(t.getString("topicId"));
			            if (id > maxTopicId) maxTopicId = id;
			        } catch (NumberFormatException e) {
			            // skip non-numeric topicIds
			        }
			    }

			    // Generate next topicId = max + 1
			    topicId = String.valueOf(maxTopicId + 1);
			    
	            // ✅ Save new topic to TopicMaster with partyId scoped to this admin
	            Map<String, Object> topicMasterData = new HashMap<>();
	            topicMasterData.put("topicId",   topicId);
	            topicMasterData.put("topicName", topicName);
	            topicMasterData.put("partyId",   partyId);   // ✅ admin scoping
	            topicMasterData.put("userLogin", userLogin);

	            Map<String, Object> topicResult = dispatcher.runSync("createTopicMasterAuto", topicMasterData);
	            if (ServiceUtil.isError(topicResult)) {
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(topicResult));
	            }
	        }

	        // CASE 2 — topicId came from dropdown (existing topic), skip TopicMaster insert

	        // Save to ExamTopicDetails — same for both cases
			Map<String, Object> autoData = new HashMap<>();
	        autoData.put("examId",              examId);
	        autoData.put("topicId",             topicId);
	        autoData.put("topicName",           topicName);
	        autoData.put("percentage",          percentage);
	        autoData.put("startingQid",         startingQid);
	        autoData.put("endingQid",           endingQid);
	        autoData.put("questionsPerExam",    String.valueOf(calculatedQuestionsPerExam));
	        autoData.put("topicPassPercentage", topicPassPercentage);
	        autoData.put("userLogin",           userLogin);
	        
			Map<String, Object> result = dispatcher.runSync("createAutoTopicMaster", autoData);
			
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
			}

			return ServiceUtil.returnSuccess("Topic created successfully");
			
		} catch (GenericServiceException | GenericEntityException e) {
			return ServiceUtil.returnError("Topic creation failed: " + e.getMessage());
		}
	}
}