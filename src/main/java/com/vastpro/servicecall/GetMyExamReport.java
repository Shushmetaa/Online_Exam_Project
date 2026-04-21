package com.vastpro.servicecall;

import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.ServiceUtil;

public class GetMyExamReport {

	    private static Delegator getDelegator(HttpServletRequest request) {
	        Delegator d = (Delegator) request.getAttribute("delegator");
	        if (d == null)
	            d = (Delegator) request.getSession()
	                    .getServletContext().getAttribute("delegator");
	        return d;
	    }

	    public static Map<String, Object> getReport(
	            HttpServletRequest request,
	            HttpServletResponse response) {
	        try {
	            HttpSession session = request.getSession();
	            String partyId       = (String) session.getAttribute("partyId");
	            String examId        = (String) session.getAttribute("examId");
	            String performanceId = (String) session.getAttribute("performanceId");

	            if (partyId == null || examId == null || performanceId == null) {
	                return ServiceUtil.returnError(
	                    "Session data missing. Please attempt an exam first.");
	            }

	            Delegator delegator = getDelegator(request);

	            // ── 1. Fetch performance ──────────────────────────────
	            GenericValue perf = EntityQuery.use(delegator)
	                .from("PartyPerformance")
	                .where("performanceId", Long.parseLong(performanceId))
	                .queryOne();

	            if (perf == null)
	                return ServiceUtil.returnError("Performance record not found.");

	            // ── 2. Fetch exam ─────────────────────────────────────
	            GenericValue exam = EntityQuery.use(delegator)
	                .from("ExamMaster")
	                .where("examId", examId)
	                .queryOne();

	            if (exam == null)
	                return ServiceUtil.returnError("Exam not found.");

	            // ── 3. Fetch topic breakdown ──────────────────────────
	            List<GenericValue> topicPerf = EntityQuery.use(delegator)
	                .from("DetailedPartyPerformance")
	                .where("partyId",       partyId,
	                       "examId",        examId,
	                       "performanceId", Long.parseLong(performanceId))
	                .queryList();

	            // ── 4. Build report map ───────────────────────────────
	            Map<String, Object> report = new HashMap<>();
	            report.put("score",       perf.getBigDecimal("score"));
	            report.put("passed",      perf.getInteger("userPassed") != null
	                                      && perf.getInteger("userPassed") == 1);
	            report.put("correct",     perf.getLong("totalCorrect"));
	            report.put("wrong",       perf.getLong("totalWrong"));
	            report.put("totalQ",      perf.getLong("noOfQuestions"));
	            report.put("attemptedOn", String.valueOf(perf.getTimestamp("date")));
	            report.put("cutOff",      exam.getLong("passPercentage"));

	            // skipped = total - correct - wrong
	            long total   = perf.getLong("noOfQuestions") != null
	                           ? perf.getLong("noOfQuestions") : 0;
	            long correct = perf.getLong("totalCorrect")  != null
	                           ? perf.getLong("totalCorrect") : 0;
	            long wrong   = perf.getLong("totalWrong")    != null
	                           ? perf.getLong("totalWrong")   : 0;
	            report.put("skipped", total - correct - wrong);

	            // Topic breakdown list
	            List<Map<String, Object>> breakdown = new ArrayList<>();
	            for (GenericValue tp : topicPerf) {
	                Map<String, Object> t = new HashMap<>();
	                t.put("topicName", tp.getString("topicId")); // replace with topicName if available
	                BigDecimal pct = tp.getBigDecimal("userTopicPercentage");
	                t.put("pct", pct != null ? pct.intValue() : 0);
	                breakdown.add(t);
	            }
	            report.put("topicBreakdown", breakdown);

	            // ── 5. Build exam map ─────────────────────────────────
	            Map<String, Object> examMap = new HashMap<>();
	            examMap.put("examId",         exam.getString("examId"));
	            examMap.put("examName",       exam.getString("examName"));
	            examMap.put("noOfQuestions",  exam.getLong("noOfQuestions"));
	            examMap.put("duration",       exam.getLong("duration"));
	            examMap.put("passPercentage", exam.getLong("passPercentage"));

	            Map<String, Object> result = ServiceUtil.returnSuccess();
	            result.put("report", report);
	            result.put("exam",   examMap);
	            return result;

	        } catch (Exception e) {
	            return ServiceUtil.returnError(
	                "Failed to fetch report: " + e.getMessage());
	        }
	    }
	}

