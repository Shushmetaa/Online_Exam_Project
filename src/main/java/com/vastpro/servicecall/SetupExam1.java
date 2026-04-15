package com.vastpro.servicecall;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
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
	
	public static Map<String, Object> assignUser(String examId, String partyId, String allowedAttempts, String noOfAttempts, String timeoutDays,
	        HttpServletRequest request, HttpServletResponse response) {
		
	    try {
	    	
	        LocalDispatcher dispatcher = getDispatcher(request);

	        GenericValue userLogin = EntityQuery.use(getDelegator(request))
	                .from("UserLogin")
	                .where("userLoginId", "admin")
	                .queryOne();

	        Long noOfAttemptsLong;
	        Long timeoutDaysLong;
	        Long allowedAttemptsLong;

	        try {
	            noOfAttemptsLong = Long.valueOf(noOfAttempts);
	            timeoutDaysLong = Long.valueOf(timeoutDays);
	            allowedAttemptsLong = allowedAttempts.equalsIgnoreCase("yes") ? 1L : 0L;
	        } catch (NumberFormatException e) {
	            return ServiceUtil.returnError("allowedAttempts, noOfAttempts, timeoutDays must be numbers only");
	        }

	        Map<String, Object> assignData = new HashMap<>();
	        
	        assignData.put("examId", examId);
	        assignData.put("partyId", partyId);
	        assignData.put("allowedAttempts", allowedAttemptsLong);
	        assignData.put("noOfAttempts", noOfAttemptsLong);
	        assignData.put("timeoutDays", timeoutDaysLong);
	        assignData.put("userLogin", userLogin);

	        Map<String, Object> result = dispatcher.runSync("createPartyExamRelationship", assignData);

	        if (ServiceUtil.isError(result)) {
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	        }
	        return ServiceUtil.returnSuccess("User assigned successfully");

	    } catch (Exception e) {
	        return ServiceUtil.returnError("Error: " + e.getMessage());
	    }
	}
	
	public static Map<String, Object> getAssignedUsers(String examId, HttpServletRequest request, HttpServletResponse response) {
	    
		try {
			
	        Delegator delegator = getDelegator(request);

	        //searching for the users with the examid
	        List<GenericValue> assigned = EntityQuery.use(delegator)
	                .from("PartyExamRelationship")
	                .where("examId", examId)
	                .queryList();

	        List<Map<String, Object>> userList = new ArrayList<>();

	        //putting all the partyid in the list
	        for (GenericValue per : assigned) {
	            String partyId = per.getString("partyId");

	            //fetch person details using partyId
	            GenericValue person = EntityQuery.use(delegator)
	                    .from("Person")
	                    .where("partyId", partyId)
	                    .queryOne();

	            Map<String, Object> user = new HashMap<>();
	            user.put("partyId",   partyId);

	            if (person != null) {
	                user.put("firstName", person.getString("firstName"));
	                user.put("lastName",  person.getString("lastName"));
	            }

	            userList.add(user);
	        }

	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        result.put("assignedUsers", userList);
	        return result;

	    } catch (Exception e) {
	        return ServiceUtil.returnError("Error: " + e.getMessage());
	    }
	}
	public static Map<String, Object> sendExamAssignmentEmail(
	        String examId, String partyId, HttpServletRequest request, HttpServletResponse response) {

	    try {
	        LocalDispatcher dispatcher = getDispatcher(request);
	        Delegator delegator = getDelegator(request);

	        GenericValue userLogin = EntityQuery.use(delegator)
	                .from("UserLogin").where("userLoginId", "admin").queryOne();

	        // get user email
	        GenericValue assignedUser = EntityQuery.use(delegator)
	                .from("UserLogin").where("partyId", partyId).queryFirst();

	        // get exam name
	        GenericValue exam = EntityQuery.use(delegator)
	                .from("ExamMaster").where("examId", examId).queryOne();
	       
	        if (assignedUser == null) {
	            return ServiceUtil.returnError("User not found");
	        }
	        if (exam == null) {
	            return ServiceUtil.returnError("Exam not found");
	        }

	        String email    = assignedUser.getString("userLoginId");
	        String examName = exam.getString("examName");
            
	        // generate password
	        String rawPassword = generatePassword();
            String hashedPassword=HashCrypt.cryptUTF8("SHA", null, rawPassword);
            
            //save password to db
            Map<String,Object> updateData=new HashMap<>();
            updateData.put("examId", examId);
            updateData.put("partyId",partyId);
            updateData.put("passwordChangesAuto", hashedPassword);
            updateData.put("userLogin",           userLogin);

            Map<String, Object> updateResult = dispatcher.runSync(
                "updatePartyExamPassword", updateData);

            if (ServiceUtil.isError(updateResult)) {
                return ServiceUtil.returnError("Failed to save password: "+ ServiceUtil.getErrorMessage(updateResult));
            }
            
	        // send rawpassword 
	        Map<String, Object> emailCtx = new HashMap<>();
	        emailCtx.put("sendTo", email);
	        emailCtx.put("subject",  "You have been assigned to Exam: " + examName);
	        emailCtx.put("body",
	                "Hello,\n\n" +
	                "You have been assigned to: " + examName + "\n\n" +
	                "Username: " + email + "\n" +
	                "Exam Password: " + rawPassword + "\n\n" +
	                "Use these to start your exam.\n\n" +
	                "Regards,\nAdmin");
	        emailCtx.put("contentType", "text/plain");

	        Map<String, Object> mailResult = dispatcher.runSync("sendMail", emailCtx);
	        if (ServiceUtil.isError(mailResult)) {
	            return ServiceUtil.returnError("Email failed: "
	                + ServiceUtil.getErrorMessage(mailResult));
	        }

	        return ServiceUtil.returnSuccess("Email sent successfully");

	    } catch (Exception e) {
	        return ServiceUtil.returnError("Error: " + e.getMessage());
	    }
	}
	
	private static String generatePassword() {
	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
	   SecureRandom random = new SecureRandom();
	    StringBuilder password = new StringBuilder();
	    for (int i = 0; i < 8; i++) {
	    	//it will select the random index
	        password.append(chars.charAt(random.nextInt(chars.length())));
	    }
	    return password.toString();
	}
	
	public static Map<String, Object> saveAndPublish(
	        String examId, String status, String partyIds,
	        String allowedAttempts, String noOfAttempts, String timeoutDays,
	        String openDate, String closeDate, String whenExpires, String gradingMethod,
	        String shuffleQ, String shuffleA, String sequential, String showResults,
	        HttpServletRequest request, HttpServletResponse response) {
	    try {
	        LocalDispatcher dispatcher = getDispatcher(request);
	        GenericValue userLogin = EntityQuery.use(getDelegator(request))
	                .from("UserLogin").where("userLoginId", "admin").queryOne();

	        Map<String, Object> data = new HashMap<>();
	        data.put("examId",          examId);
	        data.put("status", status);
	        data.put("partyIds",        partyIds);
	        data.put("allowedAttempts", allowedAttempts);
	        data.put("noOfAttempts",    noOfAttempts);
	        data.put("timeoutDays",     timeoutDays);
	        data.put("openDate",        openDate);
	        data.put("closeDate",       closeDate);
	        data.put("whenExpires",     whenExpires);
	        data.put("gradingMethod",   gradingMethod);
	        data.put("shuffleQ",        shuffleQ);
	        data.put("shuffleA",        shuffleA);
	        data.put("sequential",      sequential);
	        data.put("showResults",     showResults);
	        data.put("userLogin",       userLogin);

	        Map<String, Object> result = dispatcher.runSync("saveAndPublishExam", data);
	        if (ServiceUtil.isError(result))
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	        return ServiceUtil.returnSuccess("Exam saved and published successfully!");
	    } catch (Exception e) {
	        return ServiceUtil.returnError("Error: " + e.getMessage());
	    }
	}
	
	public static Map<String, Object> getUnsetupExams(
	        HttpServletRequest request, HttpServletResponse response) {
	    try {
	        Delegator delegator = getDelegator(request);

	        // Get ALL exams
	        List<GenericValue> allExams = EntityQuery.use(delegator)
	                .from("ExamMaster")
	                .orderBy("examId")
	                .queryList();

	        List<Map<String, Object>> examList = new ArrayList<>();

	        for (GenericValue exam : allExams) {
	            String examId = exam.getString("examId");

	            // Check if already setup
	            GenericValue setupRecord = EntityQuery.use(delegator)
	                    .from("ExamSetupDetails")
	                    .where("examId", examId)
	                    .queryOne();

	            // Only add if NOT yet setup
	            if (setupRecord == null) {
	                Map<String, Object> examMap = new HashMap<>();
	                examMap.put("examId",        examId);
	                examMap.put("examName",       exam.getString("examName"));
	                examMap.put("noOfQuestions",  exam.getLong("noOfQuestions"));
	                examMap.put("duration",       exam.getLong("duration"));
	                examMap.put("passPercentage", exam.getLong("passPercentage"));
	                examMap.put("description",    exam.getString("description"));
	                examList.add(examMap);
	            }
	        }

	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        result.put("examList", examList);
	        return result;

	    } catch (GenericEntityException e) {
	        return ServiceUtil.returnError("Failed: " + e.getMessage());
	    }
	}
	
	public static Map<String, Object> getAllAssignedUsers(HttpServletRequest request, HttpServletResponse response) {
	    try {
	        Delegator delegator = getDelegator(request);

	        // 1. Get all setup exams
	        List<GenericValue> setupExams = EntityQuery.use(delegator)
	                .from("ExamSetupDetails")
	                .queryList();

	        List<Map<String, Object>> examCards = new ArrayList<>();

	        for (GenericValue setup : setupExams) {
	            String examId = setup.getString("examId");

	            // 2. Get exam details from ExamMaster
	            GenericValue exam = EntityQuery.use(delegator)
	                    .from("ExamMaster")
	                    .where("examId", examId)
	                    .queryOne();

	            if (exam == null) continue;

	            // 3. Get all assigned users for this exam
	            List<GenericValue> relationships = EntityQuery.use(delegator)
	                    .from("PartyExamRelationship")
	                    .where("examId", examId)
	                    .queryList();

	            List<Map<String, Object>> userList = new ArrayList<>();

	            for (GenericValue rel : relationships) {
	                String partyId = rel.getString("partyId");

	                // 4. Get name from Person table
	                GenericValue person = EntityQuery.use(delegator)
	                        .from("Person")
	                        .where("partyId", partyId)
	                        .queryOne();

	                Map<String, Object> userMap = new HashMap<>();
	                userMap.put("partyId", partyId);
	                userMap.put("noOfAttempts", rel.getLong("noOfAttempts"));
	                userMap.put("timeoutDays",  rel.getLong("timeoutDays"));

	                if (person != null) {
	                    userMap.put("firstName", person.getString("firstName"));
	                    userMap.put("lastName",  person.getString("lastName"));
	                }

	                userList.add(userMap);
	            }

	            Map<String, Object> examCard = new HashMap<>();
	            examCard.put("examId",        examId);
	            examCard.put("examName",      exam.getString("examName"));
	            examCard.put("noOfQuestions", exam.getLong("noOfQuestions"));
	            examCard.put("duration",      exam.getLong("duration"));
	            examCard.put("passPercentage",exam.getLong("passPercentage"));
	            examCard.put("assignedUsers", userList);

	            examCards.add(examCard);
	        }

	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        result.put("examCards", examCards);
	        return result;

	    } catch (GenericEntityException e) {
	        return ServiceUtil.returnError("Failed: " + e.getMessage());
	    }
	}
}
