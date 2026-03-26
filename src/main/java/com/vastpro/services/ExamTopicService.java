package com.vastpro.services;

import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class ExamTopicService {

		//Create
		public static Map<String, Object> createExamTopic(
	            DispatchContext dctx, Map<String, ? extends Object> context) {

	        String examId              = (String) context.get("examId");
	        String topicId             = (String) context.get("topicId");
	        String topicName           = (String) context.get("topicName");
	        Long   percentage          = (Long)   context.get("percentage");
	        Long   startingQid         = (Long)   context.get("startingQid");
	        Long   endingQid           = (Long)   context.get("endingQid");
	        Long   questionsPerExam    = (Long)   context.get("questionsPerExam");
	        Double topicPassPercentage = (Double) context.get("topicPassPercentage");

	        // Validation
	        if (examId == null || examId.isEmpty())
	            return ServiceUtil.returnError("Exam ID is required");
	        if (topicId == null || topicId.isEmpty())
	            return ServiceUtil.returnError("Topic ID is required");
	        if (topicName == null || topicName.isEmpty())
	            return ServiceUtil.returnError("Topic name is required");
	        if (percentage == null || percentage <= 0)
	            return ServiceUtil.returnError("Percentage must be greater than 0");
	        if (startingQid == null || endingQid == null)
	            return ServiceUtil.returnError("Question range is required");
	        if (endingQid <= startingQid)
	            return ServiceUtil.returnError("Ending QID must be greater than starting QID");
	        if (questionsPerExam == null || questionsPerExam <= 0)
	            return ServiceUtil.returnError("Questions per exam must be greater than 0");
	        if (questionsPerExam > (endingQid - startingQid + 1))
	            return ServiceUtil.returnError("Questions per exam exceeds available question range");
	        if (topicPassPercentage == null || topicPassPercentage < 0 || topicPassPercentage > 100)
	            return ServiceUtil.returnError("Topic pass percentage must be between 0 and 100");

	        Delegator delegator = dctx.getDelegator();

	        try {
	            // Check exam exists
	            GenericValue exam = EntityQuery.use(delegator)
	                    .from("ExamMaster")
	                    .where("examId", examId)
	                    .queryOne();
	            if (exam == null)
	                return ServiceUtil.returnError("Exam not found: " + examId);

	            // Check duplicate topic for this exam
	            GenericValue existing = EntityQuery.use(delegator)
	                    .from("ExamTopicDetails")
	                    .where("examId", examId, "topicId", topicId)
	                    .queryOne();
	            if (existing != null)
	                return ServiceUtil.returnError("Topic ID already exists for this exam");

	            // Create record
	            GenericValue topic = delegator.makeValue("ExamTopicDetails");
	            topic.set("examId",              examId);
	            topic.set("topicId",             topicId);
	            topic.set("topicName",           topicName);
	            topic.set("percentage",          percentage);
	            topic.set("startingQid",         startingQid);
	            topic.set("endingQid",           endingQid);
	            topic.set("questionsPerExam",    questionsPerExam);
	            topic.set("topicPassPercentage", topicPassPercentage);
	            delegator.create(topic);

	            Map<String, Object> result = ServiceUtil.returnSuccess("Topic created successfully");
	            result.put("examId",  examId);
	            result.put("topicId", topicId);
	            return result;

	        } catch (GenericEntityException e) {
	            return ServiceUtil.returnError("Error creating topic: " + e.getMessage());
	        }
	    }

	}


