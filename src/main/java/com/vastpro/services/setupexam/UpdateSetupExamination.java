package com.vastpro.services.setupexam;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class UpdateSetupExamination {
	
public static Map<String, Object> updateSetupExam(DispatchContext dctx, Map<String, ? extends Object> context){
		
		 try {
	            String examId          = (String) context.get("examId");
	            String setupType       = (String) context.get("setupType");
	            String setupDetails    = (String) context.get("setupDetails");
	            String partyId         = (String) context.get("partyId");
	            String allowedAttempts = (String) context.get("allowedAttempts");
	            String noOfAttempts    = (String) context.get("noOfAttempts");
	            String timeoutDays     = (String) context.get("timeoutDays");

	            if (examId == null || examId.isEmpty()) {
	                return ServiceUtil.returnError("Exam ID is required");
	            }
	            if (partyId == null || partyId.isEmpty()) {
	                return ServiceUtil.returnError("Party ID is required");
	            }

	            LocalDispatcher dispatcher = dctx.getDispatcher();
	            
	            GenericValue userLogin = (GenericValue) context.get("userLogin");

	            Map<String, Object> examSetupData = new HashMap<>();
	            examSetupData.put("examId",       examId);       
	            examSetupData.put("setupType",    setupType);    
	            examSetupData.put("setupDetails", setupDetails); 
	            examSetupData.put("userLogin",    userLogin);
	            dispatcher.runSync("updateExamSetupDetailsAuto", examSetupData); 

	            Map<String, Object> partyExamData = new HashMap<>();
	            partyExamData.put("examId",          examId);           
	            partyExamData.put("partyId",         partyId);          
	            partyExamData.put("allowedAttempts", allowedAttempts); 
	            partyExamData.put("noOfAttempts",    noOfAttempts);    
	            partyExamData.put("timeoutDays",     timeoutDays);      
	            partyExamData.put("userLogin",       userLogin);
	            dispatcher.runSync("updatePartyExamRelationshipAuto", partyExamData);

	            return ServiceUtil.returnSuccess("Setup exam updated successfully");
		
		
		 }catch(Exception e) {
			 return ServiceUtil.returnError("Error in setting up the exam" + e.getMessage());
		 }
		
		
	}
}
