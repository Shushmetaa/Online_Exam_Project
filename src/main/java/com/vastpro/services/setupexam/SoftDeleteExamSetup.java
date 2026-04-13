package com.vastpro.services.setupexam;

import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class SoftDeleteExamSetup {

    public static Map<String, Object> softDeleteExamSetup(
            DispatchContext dctx, Map<String, ? extends Object> context) {

        try {
            String examId = (String) context.get("examId");
            if (examId == null || examId.isEmpty())
                return ServiceUtil.returnError("Exam ID is required");

            Delegator delegator = dctx.getDelegator();

            // 1. Delete ExamSetupDetails if exists
            GenericValue setupDetails = EntityQuery.use(delegator)
                    .from("ExamSetupDetails")
                    .where("examId", examId)
                    .queryOne();
            if (setupDetails != null) {
                setupDetails.remove();
            }

            // 2. Delete all PartyExamRelationship for this exam
            List<GenericValue> relationships = EntityQuery.use(delegator)
                    .from("PartyExamRelationship")
                    .where("examId", examId)
                    .queryList();
            for (GenericValue rel : relationships) {
                rel.remove();
            }

            // 3. Delete all QuestionBankMaster for this exam
            List<GenericValue> questions = EntityQuery.use(delegator)
                    .from("QuestionBankMaster")
                    .where("examId", examId)
                    .queryList();
            for (GenericValue q : questions) {
                q.remove();
            }
            
         // 4. Delete from ExamMaster
            GenericValue examMaster = EntityQuery.use(delegator)
                    .from("ExamMaster")
                    .where("examId", examId)
                    .queryOne();
            if (examMaster != null) {
                examMaster.remove();
            }

            return ServiceUtil.returnSuccess("Exam setup deleted successfully");

        } catch (Exception e) {
            return ServiceUtil.returnError("Error in delete: " + e.getMessage());
        }
    }
}