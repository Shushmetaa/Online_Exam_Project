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
            // Find max examId from DB and generate next ID
            List<GenericValue> allExams = EntityQuery.use(delegator)
                    .from("ExamMaster")
                    .queryList();

            long maxId = 0;
            for (GenericValue exam : allExams) {
                try {
                    long id = Long.parseLong(exam.getString("examId"));
                    if (id > maxId) maxId = id;
                } catch (NumberFormatException e) {
                    // skip non-numeric ids like EXAM001
                }
            }

            // Next examId = max + 1
            String examId = String.valueOf(maxId + 1);

            // Put generated examId into a new map along with all other fields
            Map<String, Object> examData = new HashMap<>(context);
            examData.put("examId", examId);

            Map<String, Object> result = dispatcher.runSync("createExamAuto", examData);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            Map<String, Object> response = ServiceUtil.returnSuccess("Exam Created Successfully");
            response.put("examId", examId);
            return response;

        } catch (GenericServiceException | GenericEntityException e) {
            return ServiceUtil.returnError("Exam Creation Failed: " + e.getMessage());
        }
    }
}