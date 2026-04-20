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
            System.out.println("=== NEW STARTEXAM RUNNING ===");
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

            Long totalQuestions  = exam.getLong("noOfQuestions");
            Long durationSeconds = exam.getLong("duration") * 60L;

            // 3. Check if InProgressParty exists
            GenericValue inProgress = EntityQuery.use(delegator)
                    .from("InProgressParty")
                    .where("examId", examId, "partyId", partyId)
                    .queryOne();

            boolean isResuming = (inProgress != null);

            if (!isResuming) {
                // Clean old answers from any previous attempt
                List<GenericValue> oldAnswers = EntityQuery.use(delegator)
                        .from("AnswerMaster")
                        .where("examId", examId, "partyId", partyId)
                        .queryList();
                for (GenericValue old : oldAnswers) old.remove();

                // Double-check to guard against race condition
                GenericValue doubleCheck = EntityQuery.use(delegator)
                        .from("InProgressParty")
                        .where("examId", examId, "partyId", partyId)
                        .queryOne();

                Map<String, Object> inpData = new HashMap<>();
                inpData.put("examId",         examId);
                inpData.put("partyId",        partyId);
                inpData.put("isExamActive",   1L);
                inpData.put("totalAnswered",  0L);
                inpData.put("totalRemaining", totalQuestions);
                inpData.put("remainingTime",  String.valueOf(durationSeconds));
                inpData.put("userLogin",      userLogin);

                if (doubleCheck != null) {
                    // Parallel request already created it — just update
                    dispatcher.runSync("updateInProgressPartyAuto", inpData);
                } else {
                    // Safe to insert
                    Map<String, Object> createResult = dispatcher.runSync(
                            "createInProgressPartyAuto", inpData);
                    if (ServiceUtil.isError(createResult))
                        return ServiceUtil.returnError("Failed to create exam session: "
                                + ServiceUtil.getErrorMessage(createResult));
                }
            }

            // 4. Fetch questions
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

            // 5. Fetch saved answers (empty for fresh start, populated for resume)
            List<GenericValue> answered = EntityQuery.use(delegator)
                    .from("AnswerMaster")
                    .where("examId", examId, "partyId", partyId)
                    .queryList();

            List<Map<String, Object>> answeredList = new ArrayList<>();
            for (GenericValue a : answered) {
                Map<String, Object> aMap = new HashMap<>();
                aMap.put("qId",             a.getString("questionId"));
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
            result.put("duration",      exam.getLong("duration"));
            result.put("totalQ",        exam.getLong("noOfQuestions"));
            String remainingTimeStr = latestProgress.getString("remainingTime");
            result.put("remainingTime", remainingTimeStr != null ? Long.parseLong(remainingTimeStr) : durationSeconds);
            result.put("totalAnswered", latestProgress.getLong("totalAnswered"));
            result.put("isResuming",    isResuming ? "Y" : "N");
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error starting exam: " + e.getMessage());
        }
    }
}