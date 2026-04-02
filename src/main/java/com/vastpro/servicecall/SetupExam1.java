package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SetupExam1 {
	
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
	
	public static Map<String, Object> createSetup(HttpServletRequest request, HttpServletResponse response){
		
		try {

			String examId = request.getParameter("examId");

			// ExamSetupDetails 
			String setupType    = request.getParameter("setupType");
			String setupDetails = request.getParameter("setupDetails");

			// PartyExamRelationship 
			String partyId         = request.getParameter("partyId");
			String allowedAttempts = request.getParameter("allowedAttempts");
			String noOfAttempts    = request.getParameter("noOfAttempts");
			String timeoutDays     = request.getParameter("timeoutDays");
			
			LocalDispatcher dispatcher=getDispatcher(request);
    	    
    	    if (dispatcher == null) {
                return ServiceUtil.returnError("Dispatcher is null");
            }

    	    GenericValue userLogin=EntityQuery.use(getDelegator(request))
    	    		.from("UserLogin")
    	    		.where("userLoginId", "admin")
    	    		.queryOne();
    	    
    	    Map<String, Object> examSetupDetailsData = new HashMap<>();
            examSetupDetailsData.put("examId",       examId);     
            examSetupDetailsData.put("setupType",    setupType);
            examSetupDetailsData.put("setupDetails", setupDetails);
            examSetupDetailsData.put("userLogin",    userLogin); 
            dispatcher.runSync("createSetupExams", examSetupDetailsData); 

            Map<String, Object> partyExamData = new HashMap<>();
            partyExamData.put("examId",          examId);          
            partyExamData.put("partyId",         partyId);
            partyExamData.put("allowedAttempts", allowedAttempts);
            partyExamData.put("noOfAttempts",    noOfAttempts);
            partyExamData.put("timeoutDays",     timeoutDays);
            partyExamData.put("userLogin",       userLogin);      
            dispatcher.runSync("createSetupExam", partyExamData); 

            return ServiceUtil.returnSuccess("Exam setup created successfully");
			
		}catch(Exception e) {
			return ServiceUtil.returnError("Error: " + e.getMessage());
		}
		
		
	}
	
	public static Map<String, Object> updateSetup(String examId, String setupType, String setupDetails,
			String partyId, String allowedAttempts, String noOfAttempts, String timeoutDays, 
			HttpServletRequest request, HttpServletResponse response){
				
		try {
			LocalDispatcher dispatcher = getDispatcher(request);
			if (dispatcher == null) {
			    return ServiceUtil.returnError("Dispatcher is null");
			}

			GenericValue userLogin = EntityQuery.use(getDelegator(request))
			        .from("UserLogin")
			        .where("userLoginId", "admin")
			        .queryOne();

			Map<String, Object> examSetupDetailsData = new HashMap<>();
			examSetupDetailsData.put("examId",       examId);       
			examSetupDetailsData.put("setupType",    setupType);    
			examSetupDetailsData.put("setupDetails", setupDetails); 
			examSetupDetailsData.put("userLogin",    userLogin);
			dispatcher.runSync("updateSetupExam", examSetupDetailsData); 

			Map<String, Object> partyExamData = new HashMap<>();
			partyExamData.put("examId",          examId);           
			partyExamData.put("partyId",         partyId);         
			partyExamData.put("allowedAttempts", allowedAttempts);  
			partyExamData.put("noOfAttempts",    noOfAttempts);     
			partyExamData.put("timeoutDays",     timeoutDays);      
			partyExamData.put("userLogin",       userLogin);
			dispatcher.runSync("updateSetupExam", partyExamData); 

			return ServiceUtil.returnSuccess("Exam setup updated successfully");
		}catch(Exception e) {
			return ServiceUtil.returnError("Error: " + e.getMessage());
		}
		
		
	}
	

}
