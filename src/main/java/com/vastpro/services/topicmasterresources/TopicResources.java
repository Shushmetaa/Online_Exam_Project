package com.vastpro.services.topicmasterresources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class TopicResources {
	

	    public static Map<String, Object> createTopic(DispatchContext dctx, Map<String, ? extends Object> context) {

	        String topicName = (String) context.get("topicName");

	        Delegator delegator       = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();

	        // Validation
	        if (topicName == null || topicName.isEmpty()) {
	            return ServiceUtil.returnError("Topic Name is required");
	        }

	        try {
	        	
	        	GenericValue existing = EntityQuery.use(delegator)
	                    .from("TopicMaster")
	                    .where("topicName", topicName)
	                    .queryOne();

	            if (existing != null) {
	                return ServiceUtil.returnError("Topic already exists");
	            }

	            String topicId = delegator.getNextSeqId("TopicMaster");

	            Map<String, Object> topicData = new HashMap<>(context);
	            topicData.put("topicId",   topicId);
	            topicData.put("topicName", topicName);

	            Map<String, Object> result = dispatcher.runSync("createTopicMasterAuto", topicData);

	            if (ServiceUtil.isError(result)) {
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	            }

	            Map<String, Object> response = ServiceUtil.returnSuccess("Topic Created Successfully");
	            response.put("topicId",   topicId);   
	            response.put("topicName", topicName);
	            return response;

	        } catch (GenericServiceException | GenericEntityException e) {
	            return ServiceUtil.returnError("Topic Creation Failed: " + e.getMessage());
	        }
	    }

}
