package com.vastpro.services.topicmaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetTopicMaster {
	
	public static Map<String, Object> getTopic(
	        DispatchContext dctx, Map<String, ? extends Object> context) {
	    try {
	        String examId = (String) context.get("examId");

	        if (examId == null || examId.trim().isEmpty()) {
	            return ServiceUtil.returnError("Exam Id is required");
	        }

	        Delegator delegator = dctx.getDelegator();

	        List<GenericValue> topics = EntityQuery.use(delegator)
	                .from("ExamTopicDetails")
	                .where("examId", examId)
	                .queryList();

	        Map<String, Object> res = ServiceUtil.returnSuccess();
	        res.put("topicList", topics);
	        return res;

	    } catch (GenericEntityException e) {
	        return ServiceUtil.returnError("Failed: " + e.getMessage());
	    }
	}
}
