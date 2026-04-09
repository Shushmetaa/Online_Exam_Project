package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class TopicMasterCall {
	
	private static LocalDispatcher getDispatcher(HttpServletRequest request) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
        }
        return dispatcher;
    }

    private static Delegator getDelegator(HttpServletRequest request) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (delegator == null) {
            delegator = (Delegator) request.getSession().getServletContext().getAttribute("delegator");
        }
        return delegator;
    }

    public static Map<String, Object> createTopic(String topicName, HttpServletRequest request, HttpServletResponse response) {
    	
        try {

            LocalDispatcher dispatcher = getDispatcher(request);

            GenericValue userLogin = EntityQuery.use(getDelegator(request))
                    .from("UserLogin")
                    .where("userLoginId", "admin")
                    .queryOne();

            Map<String, Object> topicData = new HashMap<>();
            topicData.put("topicName", topicName);
            topicData.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync("createTopicMaster", topicData);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            // Pass topicId and topicName back so React can auto-select
            Map<String, Object> success = ServiceUtil.returnSuccess("Topic created successfully");
            success.put("topicId",   result.get("topicId"));
            success.put("topicName", result.get("topicName"));
            return success;

        } catch (GenericEntityException | GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    public static Map<String, Object> getAllTopics(HttpServletRequest request, HttpServletResponse response) {

        try {
            Delegator delegator = getDelegator(request);

            List<GenericValue> topicList = EntityQuery.use(delegator)
                    .from("TopicMaster")
                    .orderBy("topicName")
                    .queryList();

            Map<String, Object> res = ServiceUtil.returnSuccess();
            res.put("topicList", topicList);
            return res;

        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
    }
    
}