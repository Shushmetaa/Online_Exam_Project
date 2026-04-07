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
	
	public static Map<String, Object> createSetup(String examId, String setupType, String setupDetails, 
			String partyId, String allowedAttempts, String noOfAttempts, String timeoutDays, HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			LocalDispatcher dispatcher=getDispatcher(request);
    	    
    	    if (dispatcher == null) {
                return ServiceUtil.returnError("Dispatcher is null");
            }

    	    GenericValue userLogin=EntityQuery.use(getDelegator(request))
    	    		.from("UserLogin")
    	    		.where("userLoginId", "admin")
    	    		.queryOne();
    	    
    	    Map<String, Object> setupData = new HashMap<>();
            setupData.put("examId",          examId);
            setupData.put("setupType",       setupType);
            setupData.put("setupDetails",    setupDetails);
            setupData.put("partyId",         partyId);
            setupData.put("allowedAttempts", allowedAttempts);
            setupData.put("noOfAttempts",    noOfAttempts);
            setupData.put("timeoutDays",     timeoutDays);
            setupData.put("userLogin",       userLogin);

            Map<String, Object> result = dispatcher.runSync("createSetupExam", setupData);

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

			Map<String, Object> partyExamData = new HashMap<>();
			partyExamData.put("examId",          examId);           
			partyExamData.put("partyId",         partyId);   
			partyExamData.put("setupType",    setupType);    
			partyExamData.put("setupDetails", setupDetails); 
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

	public static Map<String, Object> softUserDeleteSetup(String examId, String partyId, HttpServletRequest request, HttpServletResponse response) {
		
	    try {
	    			
	    	LocalDispatcher dispatcher = getDispatcher(request);

	        GenericValue userLogin = EntityQuery.use(getDelegator(request))
	                .from("UserLogin")
	                .where("userLoginId", "admin")
	                .queryOne();

	        Map<String, Object> deleteData = new HashMap<>();
	        deleteData.put("examId",   examId);
	        deleteData.put("partyId",  partyId);
	        deleteData.put("userLogin", userLogin);

	        Map<String, Object> result = dispatcher.runSync("softDeletePartyExamRelationship", deleteData);

	        if (ServiceUtil.isError(result)) {
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	        }
	        return ServiceUtil.returnSuccess("User removed from exam successfully");

	    } catch (Exception e) {
	        return ServiceUtil.returnError("Error: " + e.getMessage());
	    }
	    
	}
	
	public static Map<String, Object> softDeleteExamSetup(String examId, HttpServletRequest request, HttpServletResponse response) {
	   
		try {
			
			
	        LocalDispatcher dispatcher = getDispatcher(request);

	        GenericValue userLogin = EntityQuery.use(getDelegator(request))
	                .from("UserLogin")
	                .where("userLoginId", "admin")
	                .queryOne();

	        Map<String, Object> softDeleteData = new HashMap<>();
	        softDeleteData.put("examId",   examId);
	        softDeleteData.put("userLogin", userLogin);

	        Map<String, Object> result = dispatcher.runSync("softDeleteExamSetupDetails", softDeleteData);

	        if (ServiceUtil.isError(result)) {
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	        }
	        
	        return ServiceUtil.returnSuccess("Exam setup deactivated successfully");

	    } catch (Exception e) {
	        return ServiceUtil.returnError("Error: " + e.getMessage());
	    }
	}
	

}
