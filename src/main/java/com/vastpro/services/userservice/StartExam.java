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

            Long totalQuestions  = Long.parseLong(exam.getString("noOfQuestions"));
            Long durationSeconds = Long.parseLong(exam.getString("duration")) * 60L;

            // 3. Check if InProgressParty exists
            GenericValue inProgress = EntityQuery.use(delegator)
                    .from("InProgressParty")
                    .where("examId", examId, "partyId", partyId)
                    .queryOne();

            if (inProgress == null) {
                // ── Fresh start — CREATE ──────────────────────────────
                Map<String, Object> inpData = new HashMap<>();
                inpData.put("examId",         examId);
                inpData.put("partyId",        partyId);
                inpData.put("isExamActive",   1L);
                inpData.put("totalAnswered",  0L);
                inpData.put("totalRemaining", totalQuestions);
                inpData.put("remainingTime",  durationSeconds); // full time
                inpData.put("userLogin",      userLogin);
                Map<String, Object> createResult = dispatcher.runSync("createInProgressPartyAuto", inpData);
                if (ServiceUtil.isError(createResult))
                    return ServiceUtil.returnError("Failed to create exam session: "
                            + ServiceUtil.getErrorMessage(createResult));

            } else {
                // ── Resume — UPDATE, keep existing progress ───────────
                Map<String, Object> inpData = new HashMap<>();
                inpData.put("examId",         examId);
                inpData.put("partyId",        partyId);
                inpData.put("isExamActive",   1L);
                inpData.put("totalAnswered",  inProgress.getLong("totalAnswered"));
                inpData.put("totalRemaining", inProgress.getLong("totalRemaining"));
                inpData.put("remainingTime",  inProgress.getLong("remainingTime")); // restore saved time
                inpData.put("userLogin",      userLogin);
                Map<String, Object> updateResult = dispatcher.runSync("updateInProgressPartyAuto", inpData);
                if (ServiceUtil.isError(updateResult))
                    return ServiceUtil.returnError("Failed to update exam session: "
                            + ServiceUtil.getErrorMessage(updateResult));
            }

            // 4. Fetch questions (no answers sent to frontend)
            List<GenericValue> questions = EntityQuery.use(delegator)
                    .from("QuestionBankMaster")
                    .where("examId", examId)
                    .orderBy("qId")
                    .queryList();

            List<Map<String, Object>> questionList = new ArrayList<>();
            for (GenericValue q : questions) {
                Map<String, Object> qMap = new HashMap<>();
                qMap.put("qId",            q.getString("qId"));
                qMap.put("topicId",        q.getString("topicId"));
                qMap.put("questionDetail", q.getString("questionDetail"));
                qMap.put("optiona",        q.getString("optiona"));
                qMap.put("optionb",        q.getString("optionb"));
                qMap.put("optionc",        q.getString("optionc"));
                qMap.put("optiond",        q.getString("optiond"));
                qMap.put("optione",        q.getString("optione"));
                qMap.put("numAnswers",     q.getLong("numAnswers"));
                questionList.add(qMap);
            }

            // 5. Fetch already answered questions from AnswerMaster
            List<GenericValue> answered = EntityQuery.use(delegator)
                    .from("AnswerMaster")
                    .where("examId", examId, "partyId", partyId)
                    .queryList();

            List<Map<String, Object>> answeredList = new ArrayList<>();
            for (GenericValue a : answered) {
                Map<String, Object> aMap = new HashMap<>();
                aMap.put("qId",             a.getString("questionId")); // ← check your field name
                aMap.put("submittedAnswer", a.getString("submittedAnswer"));
                answeredList.add(aMap);
            }

            // 6. Get latest InProgressParty for remaining time
            GenericValue latestProgress = EntityQuery.use(delegator)
                    .from("InProgressParty")
                    .where("examId", examId, "partyId", partyId)
                    .queryOne();

            // 7. Build result
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("questionList",  questionList);
            result.put("answeredList",  answeredList);
            result.put("examName",      exam.getString("examName"));
            result.put("duration",      Long.parseLong(exam.getString("duration")));
            result.put("totalQ",        Long.parseLong(exam.getString("noOfQuestions")));
            result.put("remainingTime", latestProgress.getLong("remainingTime"));
            result.put("totalAnswered", latestProgress.getLong("totalAnswered"));
            result.put("isResuming",    inProgress != null ? "Y" : "N");
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error starting exam: " + e.getMessage());
        }
    }
}