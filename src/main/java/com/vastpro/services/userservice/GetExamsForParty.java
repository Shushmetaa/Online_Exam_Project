package com.vastpro.services.userservice;

import java.sql.Timestamp;
import java.util.*;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.*;

public class GetExamsForParty {

    public static Map<String, Object> getExamsForParty(DispatchContext dctx,
            Map<String, ?> context) {

        try {
            Delegator delegator = dctx.getDelegator();
            String partyId = (String) context.get("partyId");

            List<GenericValue> rows = EntityQuery.use(delegator)
                    .from("PartyPerformance")
                    .where("partyId", partyId, "userPassed", 1L)
                    .orderBy("-date")
                    .queryList();

            List<Map<String, Object>> exams = new ArrayList<>();

            for (GenericValue row : rows) {

                Map<String, Object> exam = new HashMap<>();

                String examId = row.getString("examId");

                GenericValue examMaster = EntityQuery.use(delegator)
                        .from("ExamMaster")
                        .where("examId", examId)
                        .queryOne();

                exam.put("examId", examId);
                exam.put("examName", examMaster != null ? examMaster.getString("examName") : "Unknown");

                Double score = row.getDouble("score");
                exam.put("score", score != null ? score.intValue() : 0);  
                exam.put("grade", calculateGrade(score));

                Timestamp ts = row.getTimestamp("date");
                String formattedDate = ts != null 
                    ? new java.text.SimpleDateFormat("dd MMMM yyyy").format(ts) 
                    : "N/A";
                exam.put("date", formattedDate);  // ← formatted string, not raw timestamp

                exam.put("performanceId", row.getLong("performanceId"));
                exams.add(exam);
            }

            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("exams", exams);
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    private static String calculateGrade(Double score) {
        if (score == null) return "N/A";
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        return "Pass";
    }
}