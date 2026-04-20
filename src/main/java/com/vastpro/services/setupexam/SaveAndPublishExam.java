package com.vastpro.services.setupexam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SaveAndPublishExam {

    public static Map<String, Object> saveAndPublish(
            DispatchContext dctx, Map<String, ? extends Object> context) {

        try {
            String examId          = (String) context.get("examId");
            String partyIdsStr     = (String) context.get("partyIds");
            String allowedAttempts = (String) context.get("allowedAttempts");
            String timeoutDays     = (String) context.get("timeoutDays");
            String openDate        = (String) context.get("openDate");
            String closeDate       = (String) context.get("closeDate");
            String whenExpires     = (String) context.get("whenExpires");
            String gradingMethod   = (String) context.get("gradingMethod");
            String shuffleQ        = (String) context.get("shuffleQ");
            String shuffleA        = (String) context.get("shuffleA");
            String sequential      = (String) context.get("sequential");
            String showResults     = (String) context.get("showResults");
            String status          = (String) context.get("status");
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            boolean isActive = "ACTIVE".equals(status);

            if (examId == null || examId.isEmpty())
                return ServiceUtil.returnError("Exam ID is required");
            if (partyIdsStr == null || partyIdsStr.isEmpty())
                return ServiceUtil.returnError("At least one user must be assigned");

            Delegator delegator        = dctx.getDelegator();
            LocalDispatcher dispatcher = dctx.getDispatcher();

            // ── STEP 1: Save exam config ──────────────────────────────
            String setupDetails =
                "{" +
                "\"openDate\":\""      + (openDate      != null ? openDate      : "") + "\"," +
                "\"closeDate\":\""     + (closeDate     != null ? closeDate     : "") + "\"," +
                "\"whenExpires\":\""   + (whenExpires   != null ? whenExpires   : "") + "\"," +
                "\"gradingMethod\":\"" + (gradingMethod != null ? gradingMethod : "") + "\"," +
                "\"shuffleQ\":"        + (shuffleQ      != null ? shuffleQ      : "true")  + "," +
                "\"shuffleA\":"        + (shuffleA      != null ? shuffleA      : "true")  + "," +
                "\"sequential\":"      + (sequential    != null ? sequential    : "false") + "," +
                "\"showResults\":"     + (showResults   != null ? showResults   : "true")  + "," +
                "\"status\":\""        + (status        != null ? status        : "DRAFT") + "\"" +
                "}";

            Map<String, Object> setupData = new HashMap<>();
            setupData.put("examId",       examId);
            setupData.put("setupType",    "EXAM_CONFIG");
            setupData.put("setupDetails", setupDetails);
            setupData.put("userLogin",    userLogin);

            GenericValue existingSetup = EntityQuery.use(delegator)
                    .from("ExamSetupDetails")
                    .where("examId", examId)
                    .queryOne();

            if (existingSetup != null) {
                dispatcher.runSync("updateExamSetupDetailsAuto", setupData);
            } else {
                dispatcher.runSync("createExamSetupDetailsAuto", setupData);
            }

            // ── STEP 2: Copy questions ONLY if ACTIVE ─────────────────
            if (isActive) {
                long globalQId = 1;

                List<GenericValue> oldQuestions = EntityQuery.use(delegator)
                        .from("QuestionBankMaster")
                        .where("examId", examId)
                        .queryList();
                for (GenericValue old : oldQuestions) old.remove();

                List<GenericValue> topics = EntityQuery.use(delegator)
                        .from("ExamTopicDetails")
                        .where("examId", examId)
                        .queryList();

                if (topics == null || topics.isEmpty())
                    return ServiceUtil.returnError(
                        "No topics found for examId: " + examId +
                        ". Please add topics before publishing.");

                for (GenericValue topic : topics) {
                    String topicId       = topic.getString("topicId");
                    String topicName     = topic.getString("topicName");
                    Long questionsNeeded = topic.getLong("questionsPerExam");

                    if (questionsNeeded == null || questionsNeeded == 0)
                        return ServiceUtil.returnError(
                            "questionsPerExam is 0 for topic: " + topicName);

                    List<GenericValue> pool = EntityQuery.use(delegator)
                            .from("QuestionBankMasterB")
                            .where("examId", examId, "topicId", topicId)
                            .queryList();

                    if (pool == null || pool.size() < questionsNeeded)
                        return ServiceUtil.returnError(
                            "Not enough questions for topic: " + topicName +
                            ". Need: " + questionsNeeded +
                            " | Available: " + (pool != null ? pool.size() : 0));

                    Collections.shuffle(pool);
                    List<GenericValue> selected = pool.subList(0, questionsNeeded.intValue());

                    for (GenericValue q : selected) {
                        Map<String, Object> liveQ = new HashMap<>();
                        liveQ.put("examId",            examId);
                        liveQ.put("qId",               String.valueOf(globalQId++));
                        liveQ.put("topicId",           topicId);
                        liveQ.put("questionDetail",    q.getString("questionDetail"));
                        liveQ.put("optiona",           q.getString("optiona"));
                        liveQ.put("optionb",           q.getString("optionb"));
                        liveQ.put("optionc",           q.getString("optionc"));
                        liveQ.put("optiond",           q.getString("optiond"));
                        liveQ.put("optione",           q.getString("optione"));
                        liveQ.put("answer",            q.getString("answer"));
                        liveQ.put("numAnswers",        q.getLong("numAnswers"));
                        liveQ.put("questionType",      q.getString("questionType"));
                        liveQ.put("difficultyLevel",   q.getString("difficultyLevel"));
                        liveQ.put("answerValue",       q.getBigDecimal("answerValue"));
                        liveQ.put("negativeMarkValue", q.getBigDecimal("negativeMarkValue"));
                        liveQ.put("userLogin",         userLogin);

                        Map<String, Object> createResult = dispatcher.runSync(
                                "createQuestionBankMasterAuto", liveQ);
                        if (ServiceUtil.isError(createResult))
                            return ServiceUtil.returnError("Failed to copy question: "
                                    + ServiceUtil.getErrorMessage(createResult));
                    }
                }
            }

            // ── STEP 3: Assign users — runs for BOTH DRAFT and ACTIVE ─
            // Parse allowedAttempts safely ONCE before the loop
            long parsedAllowedAttempts;
            if (allowedAttempts == null || allowedAttempts.isEmpty()
                    || "unlimited".equalsIgnoreCase(allowedAttempts)) {
                parsedAllowedAttempts = 999L;
            } else {
                parsedAllowedAttempts = Long.parseLong(allowedAttempts);
            }
            long parsedTimeoutDays = Long.parseLong(timeoutDays != null ? timeoutDays : "30");

            String mailFrom = isActive
                    ? EntityUtilProperties.getPropertyValue(
                        "general", "mail.smtp.auth.user",
                        "sphinxofbizz@gmail.com", delegator)
                    : null;

            String[] partyIds = partyIdsStr.split(",");

            for (String partyId : partyIds) {
                partyId = partyId.trim();
                if (partyId.isEmpty()) continue;

                // Check first assignment BEFORE creating the record
                List<GenericValue> previousExams = EntityQuery.use(delegator)
                        .from("PartyExamRelationship")
                        .where("partyId", partyId)
                        .queryList();
                boolean isFirstAssignment = previousExams.isEmpty();

                GenericValue existing = EntityQuery.use(delegator)
                        .from("PartyExamRelationship")
                        .where("examId", examId, "partyId", partyId)
                        .queryOne();

                if (existing == null) {
                    // ✅ CREATE with correct allowedAttempts
                    Map<String, Object> assignData = new HashMap<>();
                    assignData.put("examId",          examId);
                    assignData.put("partyId",         partyId);
                    assignData.put("noOfAttempts",    0L);
                    assignData.put("allowedAttempts", parsedAllowedAttempts);
                    assignData.put("timeoutDays",     parsedTimeoutDays);
                    assignData.put("userLogin",       userLogin);

                    Map<String, Object> assignResult = dispatcher.runSync(
                            "createPartyExamRelationshipAuto", assignData);
                    if (ServiceUtil.isError(assignResult))
                        return ServiceUtil.returnError("Failed to assign user "
                                + partyId + ": "
                                + ServiceUtil.getErrorMessage(assignResult));
                } else {
                    // ✅ ALWAYS UPDATE allowedAttempts — permanent fix
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("examId",          examId);
                    updateData.put("partyId",         partyId);
                    updateData.put("allowedAttempts", parsedAllowedAttempts);
                    updateData.put("userLogin",       userLogin);
                    dispatcher.runSync("updatePartyExamRelationshipAuto", updateData);
                }

                // ── Send email ONLY if ACTIVE ─────────────────────────
                if (!isActive) continue;

                GenericValue assignedUser = EntityQuery.use(delegator)
                        .from("UserLogin").where("partyId", partyId).queryFirst();
                GenericValue exam = EntityQuery.use(delegator)
                        .from("ExamMaster").where("examId", examId).queryOne();

                if (assignedUser == null || exam == null) continue;

                String email    = assignedUser.getString("userLoginId");
                String examName = exam.getString("examName");

                String rawPassword    = generatePassword();
                String hashedPassword = org.apache.ofbiz.base.crypto.HashCrypt
                        .cryptUTF8("SHA", null, rawPassword);

                Map<String, Object> pwdData = new HashMap<>();
                pwdData.put("examId",              examId);
                pwdData.put("partyId",             partyId);
                pwdData.put("passwordChangesAuto", hashedPassword);
                pwdData.put("userLogin",           userLogin);
                dispatcher.runSync("updatePartyExamPassword", pwdData);

                String safeExamName = (examName != null)
                        ? examName.replaceAll("[^a-zA-Z0-9 ]", "").trim() : "Exam";

                GenericValue person = EntityQuery.use(delegator)
                        .from("Person").where("partyId", partyId).queryOne();
                String firstName = (person != null)
                        ? person.getString("firstName") : "Candidate";

                Map<String, Object> emailCtx = new HashMap<>();
                emailCtx.put("sendTo",      email);
                emailCtx.put("sendFrom",    mailFrom);
                emailCtx.put("subject",     "You have been assigned: " + safeExamName);
                emailCtx.put("contentType", "text/plain");
                emailCtx.put("body",
                        "Dear " + firstName + ",\n\n" +
                        "You have been assigned to the following exam:\n" +
                        "Exam Name : " + safeExamName + "\n\n" +
                        (isFirstAssignment ?
                            "================================================\n" +
                            "STEP 1 — LOGIN TO THE PORTAL\n" +
                            "================================================\n" +
                            "Use these credentials to login:\n\n" +
                            "Username       : " + email + "\n" +
                            "Login Password : Sphinx@123\n\n" +
                            "================================================\n" +
                            "STEP 2 — START YOUR EXAM\n" +
                            "================================================\n" +
                            "Once logged in, use this password to start your exam:\n\n"
                            :
                            "================================================\n" +
                            "EXAM ACCESS\n" +
                            "================================================\n" +
                            "Use this password to start your exam:\n\n"
                        ) +
                        "Exam Name     : " + safeExamName + "\n" +
                        "Exam Password : " + rawPassword + "\n\n" +
                        "================================================\n" +
                        "IMPORTANT NOTES\n" +
                        "================================================\n" +
                        "* Please complete the exam before the deadline.\n" +
                        "* Do not share your exam password with anyone.\n" +
                        "* Once the exam starts, the timer cannot be paused.\n" +
                        "* Results and certificate will be emailed after completion.\n\n" +
                        "If you face any issues, please contact your administrator.\n\n" +
                        "Best Regards,\n" +
                        "Sphinx Exam Portal Team");

                dispatcher.runAsync("sendMail", emailCtx);
            }

            return ServiceUtil.returnSuccess(
                isActive
                    ? "Exam published! Questions locked. Emails sent."
                    : "Draft saved! No emails sent."
            );

        } catch (GenericEntityException | GenericServiceException e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    private static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++)
            password.append(chars.charAt(random.nextInt(chars.length())));
        return password.toString();
    }
}