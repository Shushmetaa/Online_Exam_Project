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

    public static Map<String, Object> deleteExam(DispatchContext dctx, Map<String, ? extends Object> context) {

        String examId = (String) context.get("examId");

        if (examId == null || examId.isEmpty())
            return ServiceUtil.returnError("Exam ID is required");

        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        try {

            // Step 1: Remove exam_setup_details this is foreifn key
            List<GenericValue> setupDetails = EntityQuery.use(delegator)
                    .from("ExamSetupDetails")
                    .where("examId", examId)
                    .queryList();
            if (setupDetails != null && !setupDetails.isEmpty())
                delegator.removeAll(setupDetails);

            // Step 2: Remove assigned users (PartyExamRelationship)
            List<GenericValue> assignedUsers = EntityQuery.use(delegator)
                    .from("PartyExamRelationship")
                    .where("examId", examId)
                    .queryList();
            if (assignedUsers != null && !assignedUsers.isEmpty())
                delegator.removeAll(assignedUsers);

            // Step 3: Remove admin-exam relationships (AdminPartyExamRel)
            List<GenericValue> adminRels = EntityQuery.use(delegator)
                    .from("AdminPartyExamRel")
                    .where("examId", examId)
                    .queryList();
            if (adminRels != null && !adminRels.isEmpty())
                delegator.removeAll(adminRels);

            // Step 4: Now safe to delete the exam itself
            Map<String, Object> input = new HashMap<>();
            input.put("examId",    examId);
            input.put("userLogin", context.get("userLogin"));

            Map<String, Object> result = dispatcher.runSync("deleteExamAuto", input);

            if (ServiceUtil.isError(result))
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

            Map<String, Object> response = ServiceUtil.returnSuccess("Exam deleted successfully");
            response.put("examId", examId);
            return response;

        } catch (GenericEntityException | GenericServiceException e) {
            return ServiceUtil.returnError("Error deleting exam: " + e.getMessage());
        }
    }
}