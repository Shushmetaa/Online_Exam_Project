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
	
	public Map<String, Object> getTopic(DispatchContext dctx, Map<String, ? extends Object> context){
		
		
		try {
			
			String examId = (String) context.get("examId");
			
			if(examId == null || examId.isEmpty()) {
				return ServiceUtil.returnError("Exam Id is required");
			}
			
			Delegator delegator = dctx.getDelegator();
		
		
			List<GenericValue> topics = EntityQuery.use(delegator)
					                               .from("ExamTopicDetails")
					                               .where("examId", examId)
					                               .queryList();
			if(topics == null || topics.isEmpty()) {
				return ServiceUtil.returnError("Topics not found");
			}
			
			 Map<String, Object> result = ServiceUtil.returnSuccess("Topics fetched successfully");
	         result.put("topicList", topics);
	         return result;
			
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error fetching topics: " + e.getMessage());
			
		}

	}

}
