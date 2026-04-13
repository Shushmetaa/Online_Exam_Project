package com.vastpro.services.userservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetResults {

    public static Map<String, Object> getResults(
            DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            String examId        = (String) context.get("examId");
            String partyId       = (String) context.get("partyId");
            Long   performanceId = (Long)   context.get("performanceId");

            Delegator delegator = dctx.getDelegator();

            // 1. Get performance summary
            GenericValue perf = EntityQuery.use(delegator)
                    .from("PartyPerformance")
                    .where("performanceId", performanceId)
                    .queryOne();

            if (perf == null)
                return ServiceUtil.returnError("Performance record not found");

            // 2. Get topic-wise performance
            List<GenericValue> topicPerfs = EntityQuery.use(delegator)
                    .from("DetailedPartyPerformance")
                    .where("performanceId", performanceId)
                    .queryList();

            List<Map<String, Object>> topicList = new ArrayList<>();
            for (GenericValue tp : topicPerfs) {
                Map<String, Object> t = new HashMap<>();
                t.put("topicId",                     tp.getString("topicId"));
                t.put("userTopicPercentage",          tp.getDouble("userTopicPercentage"));
                t.put("topicPassPercentage",          tp.getDouble("topicPassPercentage"));
                t.put("correctQuestionsInthisTopic",  tp.getLong("correctQuestionsInthisTopic"));
                t.put("totalQuestionsInThisTopic",    tp.getLong("totalQuestionsInThisTopic"));
                t.put("userPassedThisTopic",          tp.getLong("userPassedThisTopic"));
                topicList.add(t);
            }

            // 3. Get questions + user answers for review
            List<GenericValue> questions = EntityQuery.use(delegator)
                    .from("QuestionBankMaster")
                    .where("examId", examId)
                    .orderBy("qId")
                    .queryList();

            List<GenericValue> answers = EntityQuery.use(delegator)
                    .from("AnswerMaster")
                    .where("examId", examId, "partyId", partyId)
                    .queryList();

            Map<Long, String> answerMap = new HashMap<>();
            for (GenericValue ans : answers)
                answerMap.put(ans.getLong("questionId"), ans.getString("submittedAnswer"));

            List<Map<String, Object>> reviewList = new ArrayList<>();
            for (GenericValue q : questions) {
                Long   qId         = q.getLong("qId");
                String correctAns  = q.getString("answer");
                String userAnswer  = answerMap.getOrDefault(qId, "");
                boolean isCorrect  = correctAns.equalsIgnoreCase(userAnswer);

                Map<String, Object> qMap = new HashMap<>();
                qMap.put("qId",            qId);
                qMap.put("topicId",        q.getLong("topicId"));
                qMap.put("questionDetail", q.getString("questionDetail"));
                qMap.put("optiona",        q.getString("optiona"));
                qMap.put("optionb",        q.getString("optionb"));
                qMap.put("optionc",        q.getString("optionc"));
                qMap.put("optiond",        q.getString("optiond"));
                qMap.put("optione",        q.getString("optione"));
                qMap.put("correctAnswer",  correctAns);
                qMap.put("userAnswer",     userAnswer);
                qMap.put("isCorrect",      isCorrect ? "Y" : "N");
                reviewList.add(qMap);
            }

            // 4. Build response
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("score",         perf.getDouble("score"));
            result.put("totalCorrect",  perf.getLong("totalCorrect"));
            result.put("totalWrong",    perf.getLong("totalWrong"));
            result.put("noOfQuestions", perf.getLong("noOfQuestions"));
            result.put("userPassed",    perf.getLong("userPassed"));
            result.put("attemptNo",     perf.getLong("attemptNo"));
            result.put("topicList",     topicList);
            result.put("reviewList",    reviewList);
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error getting results: " + e.getMessage());
        }
    }
}