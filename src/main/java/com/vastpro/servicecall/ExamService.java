package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class ExamService {

    private static LocalDispatcher getDispatcher(HttpServletRequest request) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null)
            dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
        return dispatcher;
    }

    private static Delegator getDelegator(HttpServletRequest request) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (delegator == null)
            delegator = (Delegator) request.getSession().getServletContext().getAttribute("delegator");
        return delegator;
    }

    // ── 1. Start Exam ──────────────────────────────────────────────────────
    public static Map<String, Object> startExam(
            String examId, String partyId,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator        = getDelegator(request);

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();

            Map<String, Object> data = new HashMap<>();
            data.put("examId",    examId);
            data.put("partyId",   partyId);
            data.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync("startExam", data);
            if (ServiceUtil.isError(result))
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

            Map<String, Object> resp = ServiceUtil.returnSuccess();
            resp.put("questionList",   result.get("questionList"));
            resp.put("examName",       result.get("examName"));
            resp.put("duration",       result.get("duration"));
            resp.put("totalQ",         result.get("totalQ"));
            resp.put("remainingTime",  result.get("remainingTime"));   
            resp.put("totalAnswered",  result.get("totalAnswered"));  
            resp.put("savedAnswers",   result.get("answeredList"));    
            resp.put("isResuming",     result.get("isResuming"));      
            resp.put("currentQuestion", result.get("currentQuestion")); 
            return resp;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    // ── 2. Save Answer ─────────────────────────────────────────────────────
    public static Map<String, Object> saveAnswer(
            String examId, String partyId, String questionId,
            String submittedAnswer,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator        = getDelegator(request);

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();

            Map<String, Object> data = new HashMap<>();
            data.put("examId",          examId);
            data.put("partyId",         partyId);
            data.put("questionId",      Long.parseLong(questionId));
            data.put("submittedAnswer", submittedAnswer);
            data.put("userLogin",       userLogin);

            Map<String, Object> result = dispatcher.runSync("saveAnswer", data);
            if (ServiceUtil.isError(result))
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            return ServiceUtil.returnSuccess("Answer saved");

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    // ── 3. Submit Exam ─────────────────────────────────────────────────────
    public static Map<String, Object> submitExam(
            String examId, String partyId,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator        = getDelegator(request);

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();

            Map<String, Object> data = new HashMap<>();
            data.put("examId",    examId);
            data.put("partyId",   partyId);
            data.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync("submitExam", data);
            if (ServiceUtil.isError(result))
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

            Map<String, Object> resp = ServiceUtil.returnSuccess();
            resp.put("score",         result.get("score"));
            resp.put("passed",        result.get("passed"));
            resp.put("totalCorrect",  result.get("totalCorrect"));
            resp.put("totalWrong",    result.get("totalWrong"));
            resp.put("attemptsLeft",  result.get("attemptsLeft"));
            resp.put("examExpired",   result.get("examExpired"));
            resp.put("performanceId", result.get("performanceId"));
            return resp;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    // ── 4. Get Results ─────────────────────────────────────────────────────
    public static Map<String, Object> getResults(
            String examId, String partyId, String performanceId,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator        = getDelegator(request);

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();

            Map<String, Object> data = new HashMap<>();
            data.put("examId",        examId);
            data.put("partyId",       partyId);
            data.put("performanceId", Long.parseLong(performanceId));
            data.put("userLogin",     userLogin);

            Map<String, Object> result = dispatcher.runSync("getExamResults", data);
            if (ServiceUtil.isError(result))
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

            Map<String, Object> resp = ServiceUtil.returnSuccess();
            resp.put("score",         result.get("score"));
            resp.put("totalCorrect",  result.get("totalCorrect"));
            resp.put("totalWrong",    result.get("totalWrong"));
            resp.put("noOfQuestions", result.get("noOfQuestions"));
            resp.put("userPassed",    result.get("userPassed"));
            resp.put("attemptNo",     result.get("attemptNo"));
            resp.put("topicList",     result.get("topicList"));
            resp.put("reviewList",    result.get("reviewList"));
            return resp;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    // ── 5. Get Attempts ────────────────────────────────────────────────────
    public static Map<String, Object> getAttempts(
            String examId, String partyId,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator        = getDelegator(request);

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();

            Map<String, Object> data = new HashMap<>();
            data.put("examId",    examId);
            data.put("partyId",   partyId);
            data.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync("getAttempts", data);
            if (ServiceUtil.isError(result))
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

            Map<String, Object> resp = ServiceUtil.returnSuccess();
            resp.put("allowedAttempts", result.get("allowedAttempts"));
            resp.put("usedAttempts",    result.get("usedAttempts"));
            resp.put("attemptsLeft",    result.get("attemptsLeft"));
            resp.put("expired",         result.get("expired"));
            return resp;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }
}