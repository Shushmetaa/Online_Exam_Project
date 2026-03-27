package com.vastpro.services.exammaster;

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

public class DeleteExam {

	public static Map<String, Object> updateExam(DispatchContext dctx, Map<String, ? extends Object> context){
		String examId = (String) context.get("examId");

        if (examId == null || examId.isEmpty())
            return ServiceUtil.returnError("Exam ID is required");

        Delegator delegator = dctx.getDelegator();

        try {

            List<GenericValue> topics = EntityQuery.use(delegator)
                    .from("ExamTopicDetails")
                    .where("examId", examId)
                    .queryList();

            if (!topics.isEmpty())
                return ServiceUtil.returnError("Cannot delete exam — topics exist");

            List<GenericValue> questions = EntityQuery.use(delegator)
                    .from("QuestionBankMaster")
                    .where("examId", examId)
                    .queryList();

            if (!questions.isEmpty())
                return ServiceUtil.returnError("Cannot delete exam — questions exist");

            List<GenericValue> assignedUsers = EntityQuery.use(delegator)
                    .from("PartyExamRelationship")
                    .where("examId", examId)
                    .queryList();

            if (!assignedUsers.isEmpty())
                return ServiceUtil.returnError("Cannot delete exam — users assigned");

            List<GenericValue> performances = EntityQuery.use(delegator)
                    .from("PartyPerformance")
                    .where("examId", examId)
                    .queryList();

            if (!performances.isEmpty())
                return ServiceUtil.returnError("Cannot delete exam — performance exists");

   
            LocalDispatcher dispatcher = dctx.getDispatcher();

            Map<String, Object> input = new HashMap<>();
            input.put("examId", examId);

            Map<String, Object> result = dispatcher.runSync("deleteExamAuto", input);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            Map<String, Object> response = ServiceUtil.returnSuccess("Exam deleted successfully");
            response.put("examId", examId);
            return response;
            

        } catch (GenericEntityException | GenericServiceException e) {
            return ServiceUtil.returnError(
                "Error deleting exam: " + e.getMessage());
        }
		
	}

}
