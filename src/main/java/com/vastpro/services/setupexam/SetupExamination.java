package com.vastpro.services.setupexam;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SetupExamination {
	
	public static Map<String, Object> createSetupExam(DispatchContext dctx, Map<String, ? extends Object> context){
		
		try {
			
			String examId = (String) context.get("examId");
			String setupType = (String) context.get("setupType");
			String setupDetails = (String) context.get("setupDetails");
			String partyId = (String) context.get("partyId");
			String allowedAttempts = (String) context.get("allowedAttempts");
			String noOfAttempts = (String) context.get("noOfAttempts");
			String timeoutDays = (String) context.get("timeoutDays");
			
			if(setupType == null || setupType.isEmpty()) {
				return ServiceUtil.returnError("Setup type is required");
			}
			
			if(setupDetails == null || setupDetails.isEmpty()) {
				return ServiceUtil.returnError("Setup details is required");
			}
			
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
		
			Map<String, Object> examSetupData = new HashMap<>();
            examSetupData.put("examId", examId);
            examSetupData.put("setupType", setupType);
            examSetupData.put("setupDetails", setupDetails);
            examSetupData.put("userLogin", userLogin);
            dispatcher.runSync("createExamSetupDetailsAuto", examSetupData);
            
            Map<String, Object> partyExamData = new HashMap<>();
            partyExamData.put("examId", examId);
            partyExamData.put("partyId", partyId);
            partyExamData.put("allowedAttempts", Long.parseLong(allowedAttempts)); 
            partyExamData.put("noOfAttempts", Long.parseLong(noOfAttempts));       
            partyExamData.put("timeoutDays", Long.parseLong(timeoutDays));  
            partyExamData.put("userLogin", userLogin);
            dispatcher.runSync("createPartyExamRelationshipAuto", partyExamData);
			
			return ServiceUtil.returnSuccess("Setup exam is successfull");
			
		}catch(Exception e) {
			return ServiceUtil.returnError("Error in setting up the exam" + e.getMessage());
		}
	}

}
