package com.vastpro.services.userservice;

import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetAttempts {

    public static Map<String, Object> getAttempts(
            DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            String examId  = (String) context.get("examId");
            String partyId = (String) context.get("partyId");

            Delegator delegator = dctx.getDelegator();

            GenericValue per = EntityQuery.use(delegator)
                    .from("PartyExamRelationship")
                    .where("examId", examId, "partyId", partyId)
                    .queryOne();

            if (per == null)
                return ServiceUtil.returnError("Not assigned to this exam");

            long    allowed = per.getLong("allowedAttempts");
            long    used    = per.getLong("noOfAttempts");
            long    left    = Math.max(0, allowed - used);
            boolean expired = per.getTimestamp("thruDate") != null;

            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("allowedAttempts", allowed);
            result.put("usedAttempts",    used);
            result.put("attemptsLeft",    left);
            result.put("expired",         expired ? "Y" : "N");
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }
}