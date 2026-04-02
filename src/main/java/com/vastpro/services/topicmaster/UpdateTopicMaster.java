package com.vastpro.services.topicmaster;

import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class UpdateTopicMaster {
	
	public static Map<String, Object> updateTopic(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		try {
            String examId = (String) context.get("examId");
            String topicId = (String) context.get("topicId");
            String topicName = (String) context.get("topicName");
            String percentage = (String) context.get("percentage");
            String startingQid = (String) context.get("startingQid");
            String endingQid = (String) context.get("endingQid");
            String topicPassPercentage = (String) context.get("topicPassPercentage");

            // validate
            if (examId == null || examId.isEmpty()) {
                return ServiceUtil.returnError("Exam Id is required");
            }
            if (topicId == null || topicId.isEmpty()) {
                return ServiceUtil.returnError("Topic Id is required");
            }

            Delegator delegator = dctx.getDelegator();
            LocalDispatcher dispatcher = dctx.getDispatcher();

            GenericValue existingTopic = EntityQuery.use(delegator)
                    .from("ExamTopicDetails")
                    .where("examId", examId, "topicId", topicId)
                    .queryOne();

            if (existingTopic == null) {
                return ServiceUtil.returnError("Topic not found");
            }

            GenericValue exam = EntityQuery.use(delegator)
                    .from("ExamMaster")
                    .where("examId", examId)
                    .queryOne();

            if (exam == null) {
                return ServiceUtil.returnError("Exam not found");
            }

            double noOfQuestions  = exam.getLong("noOfQuestions").doubleValue();
            double pct = Double.parseDouble(percentage);
            long questionsPerExam = Math.round((pct / 100) * noOfQuestions);

            ((Map<String, Object>) context).put("questionsPerExam", String.valueOf(questionsPerExam));

            Map<String, Object> result = dispatcher.runSync("updateTopicMasterAuto", context);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            return ServiceUtil.returnSuccess("Topic updated successfully");

        } catch (GenericEntityException | GenericServiceException e) {
            return ServiceUtil.returnError("Topic update failed: " + e.getMessage());
        }

	}

}
