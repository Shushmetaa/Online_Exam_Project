package com.vastpro.services.questionmaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetQuestionMaster {

    public static Map<String, Object> getQuestions(DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            String examId  = (String) context.get("examId");
            String topicId = (String) context.get("topicId");
            String search  = (String) context.get("search");
            String qType   = (String) context.get("questionType");
            String diff    = (String) context.get("difficultyLevel");

            if (examId == null || examId.isEmpty())
                return ServiceUtil.returnError("Exam Id is required");
            if (topicId == null || topicId.isEmpty())
                return ServiceUtil.returnError("Topic Id is required");

            int viewSize  = parseIntSafe(context.get("viewSize"),  10);
            int viewIndex = parseIntSafe(context.get("viewIndex"), 0);

            Delegator delegator = dctx.getDelegator();

            List<GenericValue> allRows = EntityQuery.use(delegator)
                    .from("QuestionBankMasterB")
                    .where("examId", examId, "topicId", topicId)
                    .orderBy("qId ASC")
                    .queryList();

            // filter in memory
            List<GenericValue> filtered = new ArrayList<>();
            for (GenericValue q : allRows) {
                if (qType != null && !qType.isEmpty()
                        && !qType.equals(q.getString("questionType"))) continue;
                if (diff != null && !diff.isEmpty()
                        && !diff.equals(q.getString("difficultyLevel"))) continue;
                if (search != null && !search.trim().isEmpty()) {
                    String detail = q.getString("questionDetail");
                    if (detail == null || !detail.toLowerCase()
                            .contains(search.trim().toLowerCase())) continue;
                }
                filtered.add(q);
            }

            int totalSize  = filtered.size();
            int totalPages = (totalSize == 0) ? 1 : (int) Math.ceil((double) totalSize / viewSize);
            int fromIndex  = Math.min(viewIndex * viewSize, totalSize);
            int toIndex    = Math.min(fromIndex + viewSize, totalSize);
            List<GenericValue> pageRows = filtered.subList(fromIndex, toIndex);

            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("questionList", pageRows);
            result.put("totalSize",    totalSize);
            result.put("totalPages",   totalPages);
            result.put("viewSize",     viewSize);
            result.put("viewIndex",    viewIndex);
            return result;

        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Error fetching questions: " + e.getMessage());
        }
    }

    private static int parseIntSafe(Object val, int def) {
        try { return (val != null) ? Integer.parseInt(val.toString().trim()) : def; }
        catch (NumberFormatException e) { return def; }
    }
}