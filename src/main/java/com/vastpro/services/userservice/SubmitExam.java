package com.vastpro.services.userservice;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SubmitExam {

    public static Map<String, Object> submitExam(
            DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            String examId  = (String) context.get("examId");
            String partyId = (String) context.get("partyId");
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            Delegator delegator        = dctx.getDelegator();
            LocalDispatcher dispatcher = dctx.getDispatcher();

            // 1. Get exam
            GenericValue exam = EntityQuery.use(delegator)
                    .from("ExamMaster").where("examId", examId).queryOne();
            if (exam == null)
                return ServiceUtil.returnError("Exam not found");

            double passPercentage = exam.getDouble("passPercentage");
            long   totalQ         = exam.getLong("noOfQuestions");

            // 2. Get all questions with correct answers
            List<GenericValue> questions = EntityQuery.use(delegator)
                    .from("QuestionBankMaster")
                    .where("examId", examId)
                    .queryList();

            // 3. Get all user answers
            List<GenericValue> answers = EntityQuery.use(delegator)
                    .from("AnswerMaster")
                    .where("examId", examId, "partyId", partyId)
                    .queryList();

            // 4. Build answer map
            Map<Long, String> answerMap = new HashMap<>();
            for (GenericValue ans : answers)
                answerMap.put(ans.getLong("questionId"), ans.getString("submittedAnswer"));

            // 5. Calculate score + topic stats
            long totalCorrect = 0;
            long totalWrong   = 0;
            Map<Long, long[]> topicStats = new HashMap<>();

            for (GenericValue q : questions) {
                Long   qId        = q.getLong("qId");
                Long   topicId    = q.getLong("topicId");
                String correct    = q.getString("answer");
                String userAnswer = answerMap.get(qId);

                topicStats.putIfAbsent(topicId, new long[]{0, 0});
                topicStats.get(topicId)[1]++; // total++

                if (userAnswer != null && userAnswer.equalsIgnoreCase(correct)) {
                    totalCorrect++;
                    topicStats.get(topicId)[0]++; // correct++
                } else if (userAnswer != null) {
                    totalWrong++;
                }
            }

            double  score  = ((double) totalCorrect / totalQ) * 100;
            boolean passed = score >= passPercentage;

            // 6. Get attempt info
            GenericValue per = EntityQuery.use(delegator)
                    .from("PartyExamRelationship")
                    .where("examId", examId, "partyId", partyId)
                    .queryOne();

            long allowedAttempts = per.getLong("allowedAttempts");
            long noOfAttempts    = per.getLong("noOfAttempts");
            noOfAttempts++;

            // 7. Save PartyPerformance
            long performanceId = System.currentTimeMillis();

            Map<String, Object> perfData = new HashMap<>();
            perfData.put("performanceId",  performanceId);
            perfData.put("partyId",        partyId);
            perfData.put("examId",         examId);
            perfData.put("score",          score);
            perfData.put("date",           new Timestamp(System.currentTimeMillis()));
            perfData.put("noOfQuestions",  totalQ);
            perfData.put("totalCorrect",   totalCorrect);
            perfData.put("totalWrong",     totalWrong);
            perfData.put("userPassed",     passed ? 1L : 0L);
            perfData.put("attemptNo",      noOfAttempts);
            perfData.put("userLogin",      userLogin);
            dispatcher.runSync("createPartyPerformanceAuto", perfData);

            // 8. Save DetailedPartyPerformance per topic
            long detailedId = System.currentTimeMillis() + 1;
            for (Map.Entry<Long, long[]> entry : topicStats.entrySet()) {
                Long   topicId      = entry.getKey();
                long   topicCorrect = entry.getValue()[0];
                long   topicTotal   = entry.getValue()[1];
                double topicPct     = topicTotal > 0
                        ? ((double) topicCorrect / topicTotal) * 100 : 0;

                GenericValue topicDetails = EntityQuery.use(delegator)
                        .from("ExamTopicDetails")
                        .where("examId", examId, "topicId", String.valueOf(topicId))
                        .queryOne();
                double topicPassPct = topicDetails != null
                        ? topicDetails.getDouble("topicPassPercentage") : 50.0;

                Map<String, Object> detData = new HashMap<>();
                detData.put("detailedPerformanceId",       detailedId++);
                detData.put("partyId",                     partyId);
                detData.put("examId",                      examId);
                detData.put("topicId",                     String.valueOf(topicId));
                detData.put("topicPassPercentage",         topicPassPct);
                detData.put("userTopicPercentage",         topicPct);
                detData.put("correctQuestionsInthisTopic", topicCorrect);
                detData.put("totalQuestionsInThisTopic",   topicTotal);
                detData.put("userPassedThisTopic",         topicPct >= topicPassPct ? 1L : 0L);
                detData.put("performanceId",               performanceId);
                detData.put("userLogin",                   userLogin);
                dispatcher.runSync("createDetailedPartyPerformanceAuto", detData);
            }

            // 9. Handle pass/fail + attempts
            boolean examExpired = false;

            if (passed) {
                // Passed → expire exam immediately
                per.set("thruDate",            new Timestamp(System.currentTimeMillis()));
                per.set("passwordChangesAuto", null);
                per.set("noOfAttempts",        noOfAttempts);
                per.set("lastPerformanceDate", new Timestamp(System.currentTimeMillis()));
                per.store();
                examExpired = true;

            } else {
                per.set("noOfAttempts",        noOfAttempts);
                per.set("lastPerformanceDate", new Timestamp(System.currentTimeMillis()));

                if (noOfAttempts >= allowedAttempts) {
                    // No attempts left → expire
                    per.set("thruDate",            new Timestamp(System.currentTimeMillis()));
                    per.set("passwordChangesAuto", null);
                    per.store();
                    examExpired = true;

                } else {
                    // Attempts remaining → new password + email
                    per.store();

                    String rawPassword    = generatePassword();
                    String hashedPassword = HashCrypt.cryptUTF8("SHA", null, rawPassword);
                    per.set("passwordChangesAuto", hashedPassword);
                    per.store();

                    GenericValue assignedUser = EntityQuery.use(delegator)
                            .from("UserLogin").where("partyId", partyId).queryFirst();

                    if (assignedUser != null) {
                        String email    = assignedUser.getString("userLoginId");
                        String examName = exam.getString("examName");
                        long   left     = allowedAttempts - noOfAttempts;

                        Map<String, Object> emailCtx = new HashMap<>();
                        emailCtx.put("sendTo",  email);
                        emailCtx.put("subject", "Exam Result — " + examName);
                        emailCtx.put("body",
                            "Hello,\n\n" +
                            "You did not pass: "    + examName + "\n" +
                            "Your score: "          + String.format("%.1f", score) + "%\n" +
                            "Pass mark: "           + passPercentage + "%\n\n" +
                            "Attempts remaining: "  + left + "\n" +
                            "New Exam Password: "   + rawPassword + "\n\n" +
                            "Use this for your next attempt.\n\n" +
                            "Regards,\nAdmin");
                        emailCtx.put("contentType", "text/plain");
                        dispatcher.runSync("sendMail", emailCtx);
                    }
                    examExpired = false;
                }
            }

            // 10. Delete InProgressParty
            Map<String, Object> delData = new HashMap<>();
            delData.put("examId",    examId);
            delData.put("partyId",   partyId);
            delData.put("userLogin", userLogin);
            dispatcher.runSync("deleteInProgressPartyAuto", delData);

            // 11. Return result
            long attemptsLeft = Math.max(0, allowedAttempts - noOfAttempts);

            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("score",         score);
            result.put("passed",        passed ? "Y" : "N");
            result.put("totalCorrect",  totalCorrect);
            result.put("totalWrong",    totalWrong);
            result.put("attemptsLeft",  attemptsLeft);
            result.put("examExpired",   examExpired ? "Y" : "N");
            result.put("performanceId", performanceId);
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error submitting exam: " + e.getMessage());
        }
    }

    private static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }
}