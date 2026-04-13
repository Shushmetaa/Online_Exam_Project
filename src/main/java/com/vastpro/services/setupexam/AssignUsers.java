package com.vastpro.services.setupexam;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class AssignUsers {
	
	public static Map<String, Object> assignUsers(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId = (String) context.get("examId");
			String partyId = (String) context.get("partyId");
			String allowedAttempts = (String) context.get("allowedAttempts");
			String noOfAttempts = (String) context.get("noOfAttempts");
			String timeoutDays = (String) context.get("timeoutDays");
			
			
			if(allowedAttempts == null || allowedAttempts.isEmpty()) {
				return ServiceUtil.returnError("Attempts for the exam is required");
			}
			
			if(noOfAttempts == null || noOfAttempts.isEmpty()) {
				return ServiceUtil.returnError("How many times user can attempt this exam is required");
			}
			
			if(timeoutDays == null || timeoutDays.isEmpty()) {
				return ServiceUtil.returnError("Set the time out for the exam is required");
			}

			LocalDispatcher dispatcher = dctx.getDispatcher();
			
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			Map<String, Object> users = new HashMap<>();
			
			users.put("examId", examId);
			users.put("partyId", partyId);
			users.put("allowedAttempts", Long.parseLong(allowedAttempts));
			users.put("noOfAttempts", Long.parseLong(noOfAttempts));
			users.put("timeoutDays", Long.parseLong(timeoutDays));
			users.put("userLogin", userLogin);
			dispatcher.runSync("createPartyExamRelationshipAuto", users);
			
			return ServiceUtil.returnSuccess("User is assigned successfull");
			
		}catch(Exception e) {
			return ServiceUtil.returnError("Error in assigning user" + e.getMessage());
		}
	}

}
