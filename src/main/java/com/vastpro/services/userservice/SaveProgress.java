package com.vastpro.services.userservice;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SaveProgress {
	
	public static Map<String, Object> saveProgress(DispatchContext dctx, Map<String, ? extends Object> context) {
        
		try {
            String examId         = (String) context.get("examId");
            String partyId        = (String) context.get("partyId");
            Long   timeRemaining  = (Long)   context.get("timeRemaining");
            Long   currentQuestion = (Long)  context.get("currentQuestion");
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            Delegator delegator        = dctx.getDelegator();
            LocalDispatcher dispatcher = dctx.getDispatcher();

            Map<String, Object> data = new HashMap<>();
            data.put("examId",          examId);
            data.put("partyId",         partyId);
            data.put("timeRemaining",   timeRemaining);
            data.put("currentQuestion", currentQuestion);
            data.put("lastActiveTime", new Timestamp(System.currentTimeMillis()));
            data.put("userLogin",       userLogin);

            dispatcher.runSync("updateInProgressPartyAuto", data);
            return ServiceUtil.returnSuccess();

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

}
