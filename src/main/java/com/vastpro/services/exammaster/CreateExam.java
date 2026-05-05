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

public class CreateExam {

    public static Map<String, Object> createExam(DispatchContext dctx, Map<String, ? extends Object> context) {

        String examName = (String) context.get("examName");
        String description = (String) context.get("description");
        String noOfQuestions = (String) context.get("noOfQuestions");
        String duration = (String) context.get("duration");
        String passPercentage = (String) context.get("passPercentage");

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        // Validations
        if (examName == null || examName.isEmpty()) {
            return ServiceUtil.returnError("Exam Name is required");
        }
        if (noOfQuestions == null || noOfQuestions.isEmpty()) {
            return ServiceUtil.returnError("No of Questions is required");
        }
        if (description == null || description.isEmpty()) {
            return ServiceUtil.returnError("Description is required");
        }
        if (duration == null || duration.isEmpty()) {
            return ServiceUtil.returnError("Duration is required");
        }
        if (passPercentage == null) {
            return ServiceUtil.returnError("Pass Percentage is required");
        }

        try {
            
        	String examId = delegator.getNextSeqId("ExamMaster");

            // Put generated examId into a new map along with all other fields
            Map<String, Object> examData = new HashMap<>(context);
            examData.put("examId", examId);

            Map<String, Object> result = dispatcher.runSync("createExamAuto", examData);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }
            
            // insert into admin_exam_relationship table
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            if (userLogin != null) {
                String partyId = userLogin.getString("partyId");

                Map<String, Object> relData = new HashMap<>();
                relData.put("partyId",   partyId);
                relData.put("examId",    examId);
                relData.put("fromDate",  org.apache.ofbiz.base.util.UtilDateTime.nowTimestamp());
                relData.put("userLogin", userLogin);

                Map<String, Object> relResult = dispatcher.runSync("createAdminPartyExamRelAuto", relData);
                if (ServiceUtil.isError(relResult))
                    return ServiceUtil.returnError("Exam created but admin assign failed: "
                            + ServiceUtil.getErrorMessage(relResult));
            }

            Map<String, Object> response = ServiceUtil.returnSuccess("Exam Created Successfully");
            response.put("examId", examId);
            return response;

        } catch (GenericServiceException e) {
            return ServiceUtil.returnError("Exam Creation Failed: " + e.getMessage());
        }
    }
}