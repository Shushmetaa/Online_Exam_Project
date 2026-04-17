package com.vastpro.services.userservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class StartExam {

    public static Map<String, Object> startExam(
            DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            String examId  = (String) context.get("examId");
            String partyId = (String) context.get("partyId");
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            Delegator delegator        = dctx.getDelegator();
            LocalDispatcher dispatcher = dctx.getDispatcher();

            // 1. Check PartyExamRelationship
            GenericValue per = EntityQuery.use(delegator)
                    .from("PartyExamRelationship")
                    .where("examId", examId, "partyId", partyId)
                    .queryOne();

            if (per == null)
                return ServiceUtil.returnError("You are not assigned to this exam.");

            if (per.getTimestamp("thruDate") != null)
                return ServiceUtil.returnError("This exam has expired for you.");

            // 2. Get exam details
            GenericValue exam = EntityQuery.use(delegator)
                    .from("ExamMaster")
                    .where("examId", examId)
                    .queryOne();

            if (exam == null)
                return ServiceUtil.returnError("Exam not found.");

            // noOfQuestions is type="numeric" → getLong()
            Long totalQuestions = exam.getLong("noOfQuestions");

            // 3. Create OR update InProgressParty
            GenericValue inProgress = EntityQuery.use(delegator)
                    .from("InProgressParty")
                    .where("examId", examId, "partyId", partyId)
                    .queryOne();

            if (inProgress == null) {
                // No existing record — CREATE
                Map<String, Object> inpData = new HashMap<>();
                inpData.put("examId",         examId);
                inpData.put("partyId",        partyId);
                inpData.put("isExamActive",   1L);
                inpData.put("totalAnswered",  0L);
                inpData.put("totalRemaining", totalQuestions);
                inpData.put("userLogin",      userLogin);
                Map<String, Object> createResult = dispatcher.runSync("createInProgressPartyAuto", inpData);
                if (ServiceUtil.isError(createResult)) {
                    return ServiceUtil.returnError("Failed to create exam session: "
                            + ServiceUtil.getErrorMessage(createResult));
                }
            } else {
                // Existing record found — UPDATE, just set isExamActive = 1
                Map<String, Object> inpData = new HashMap<>();
                inpData.put("examId",         examId);
                inpData.put("partyId",        partyId);
                inpData.put("isExamActive",   1L);
                // totalAnswered and totalRemaining are type="numeric" → getLong()
                inpData.put("totalAnswered",  inProgress.getLong("totalAnswered"));
                inpData.put("totalRemaining", inProgress.getLong("totalRemaining"));
                inpData.put("userLogin",      userLogin);
                Map<String, Object> updateResult = dispatcher.runSync("updateInProgressPartyAuto", inpData);
                if (ServiceUtil.isError(updateResult)) {
                    return ServiceUtil.returnError("Failed to update exam session: "
                            + ServiceUtil.getErrorMessage(updateResult));
                }
            }

            // 4. Fetch questions (do NOT send answer to frontend)
            List<GenericValue> questions = EntityQuery.use(delegator)
                    .from("QuestionBankMaster")
                    .where("examId", examId)
                    .orderBy("qId")
                    .queryList();

            List<Map<String, Object>> questionList = new ArrayList<>();
            for (GenericValue q : questions) {
                Map<String, Object> qMap = new HashMap<>();
                // qId       is type="id"           → getString()
                // topicId   is type="short-varchar" → getString()
                // numAnswers is type="numeric"      → getLong()
                qMap.put("qId",            q.getString("qId"));
                qMap.put("topicId",        q.getString("topicId"));
                qMap.put("questionDetail", q.getString("questionDetail"));
                qMap.put("optiona",        q.getString("optiona"));
                qMap.put("optionb",        q.getString("optionb"));
                qMap.put("optionc",        q.getString("optionc"));
                qMap.put("optiond",        q.getString("optiond"));
                qMap.put("optione",        q.getString("optione"));
                qMap.put("numAnswers",     q.getLong("numAnswers"));
                // answer intentionally excluded
                questionList.add(qMap);
            }

            // 5. Build and return result
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("questionList", questionList);
            // examName is type="name"    → getString()
            // duration is type="numeric" → getLong()
            result.put("examName", exam.getString("examName"));
            result.put("duration", exam.getLong("duration"));
            result.put("totalQ",   exam.getLong("noOfQuestions"));
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error starting exam: " + e.getMessage());
        }
    }
}