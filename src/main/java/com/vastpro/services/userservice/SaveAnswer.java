package com.vastpro.services.userservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SaveAnswer {

    public static Map<String, Object> saveAnswer(
            DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            String examId          = (String) context.get("examId");
            String partyId         = (String) context.get("partyId");
            Long   questionId      = Long.valueOf(context.get("questionId").toString());
            String submittedAnswer = (String) context.get("submittedAnswer");
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            Delegator delegator        = dctx.getDelegator();
            LocalDispatcher dispatcher = dctx.getDispatcher();

            // Upsert — check if answer exists
            GenericValue existing = EntityQuery.use(delegator)
                    .from("AnswerMaster")
                    .where("questionId", questionId, "examId", examId, "partyId", partyId)
                    .queryOne();

            Map<String, Object> ansData = new HashMap<>();
            ansData.put("questionId",      questionId);
            ansData.put("examId",          examId);
            ansData.put("partyId",         partyId);
            ansData.put("submittedAnswer", submittedAnswer);
            ansData.put("userLogin",       userLogin);

            if (existing == null) {
                dispatcher.runSync("createAnswerMasterAuto", ansData);
            } else {
                dispatcher.runSync("updateAnswerMasterAuto", ansData);
            }

            // Update InProgressParty counts
            long answered = EntityQuery.use(delegator)
                    .from("AnswerMaster")
                    .where("examId", examId, "partyId", partyId)
                    .queryCount();

            GenericValue exam = EntityQuery.use(delegator)
                    .from("ExamMaster").where("examId", examId).queryOne();

            long total     = exam != null ? exam.getLong("noOfQuestions") : 0L;
            long remaining = total - answered;
            Long remainingTime = (Long) context.get("remainingTime");

            Map<String, Object> inpData = new HashMap<>();
            inpData.put("examId",        examId);
            inpData.put("partyId",       partyId);
            inpData.put("totalAnswered",  answered);
            inpData.put("totalRemaining", remaining);
            inpData.put("userLogin",      userLogin);
            inpData.put("remainingTime",  remainingTime);
            dispatcher.runSync("updateInProgressPartyAuto", inpData);

            return ServiceUtil.returnSuccess("Answer saved");

        } catch (Exception e) {
            return ServiceUtil.returnError("Error saving answer: " + e.getMessage());
        }
    }
}