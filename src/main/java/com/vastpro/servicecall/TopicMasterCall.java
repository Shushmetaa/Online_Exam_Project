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

    // ✅ Always get the actual logged-in user from session
    private static GenericValue getLoggedInUser(HttpServletRequest request) {
        return (GenericValue) request.getSession().getAttribute("userLogin");
    }

    public static Map<String, Object> createTopic(String topicName, 
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);

            // ✅ Get logged-in admin's userLogin and partyId from session
            GenericValue userLogin = getLoggedInUser(request);
            if (userLogin == null) {
                return ServiceUtil.returnError("User not logged in");
            }
            String partyId = userLogin.getString("partyId");

            Map<String, Object> topicData = new HashMap<>();
            topicData.put("topicName", topicName);
            topicData.put("partyId",   partyId);   // ✅ scoped to this admin
            topicData.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync("createTopicMaster", topicData);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            Map<String, Object> success = ServiceUtil.returnSuccess("Topic created successfully");
            success.put("topicId",   result.get("topicId"));
            success.put("topicName", topicName);
            return success;

        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    public static Map<String, Object> getAllTopics(
            HttpServletRequest request, HttpServletResponse response) {
        try {
            Delegator delegator = getDelegator(request);

            // ✅ Get logged-in admin's partyId from session
            GenericValue userLogin = getLoggedInUser(request);
            if (userLogin == null) {
                return ServiceUtil.returnError("User not logged in");
            }
            String partyId = userLogin.getString("partyId");

            // ✅ Only return topics belonging to this admin
            List<GenericValue> topicList = EntityQuery.use(delegator)
                    .from("TopicMaster")
                    .where("partyId", partyId)
                    .orderBy("topicName")
                    .queryList();

            Map<String, Object> res = ServiceUtil.returnSuccess();
            res.put("responseMessage", "success");
            res.put("topicList", topicList);
            return res;

        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
    }
}