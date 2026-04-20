package com.vastpro.services.userservice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetExams {
	 public static Map<String, Object> getAssignedExams(
	            DispatchContext dctx, Map<String, ? extends Object> context) {
	        try {
	            Delegator delegator = dctx.getDelegator();
	            String partyId      = (String) context.get("partyId");

	            List<GenericValue> assignments = EntityQuery.use(delegator)
	                    .from("PartyExamRelationship")
	                    .where("partyId", partyId)
	                    .queryList();

	            List<Map<String, Object>> examList = new ArrayList<>();

	            for (GenericValue per : assignments) {
	                String examId = per.getString("examId");
	                
	                Timestamp thruDate = per.getTimestamp("thruDate");
	                
	                if (thruDate != null && thruDate.before(new java.sql.Timestamp(System.currentTimeMillis()))) {
	                    continue; // expired — don't show on home page
	                }


	                GenericValue exam = EntityQuery.use(delegator)
	                        .from("ExamMaster")
	                        .where("examId", examId)
	                        .queryOne();

	                if (exam == null) continue;

	                Map<String, Object> examMap = new HashMap<>();
	                examMap.put("examId",          exam.getString("examId"));
	                examMap.put("examName",         exam.getString("examName"));
	                examMap.put("description",      exam.getString("description"));
	                examMap.put("noOfQuestions",    exam.getLong("noOfQuestions"));
	                examMap.put("duration",         exam.getLong("duration"));
	                examMap.put("passPercentage",   exam.getLong("passPercentage"));
	                examList.add(examMap);
	            }

	            Map<String, Object> result = ServiceUtil.returnSuccess();
	            result.put("examList", examList);
	            return result;

	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error fetching exams: " + e.getMessage());
	        }
	    }
	 public static Map<String, Object> getUserExamStats(
	            DispatchContext dctx, Map<String, ? extends Object> context) {
	        try {
	            Delegator delegator = dctx.getDelegator();
	            String partyId      = (String) context.get("partyId");

	            List<GenericValue> performances = EntityQuery.use(delegator)
	                    .from("PartyPerformance")  
	                    .where("partyId", partyId)
	                    .queryList();
	            
	            long completed  = performances.size();
	            long bestScore  = 0;

	            for (GenericValue perf : performances) {
	                Long score = perf.getLong("score");
	                if (score != null && score > bestScore)
	                    bestScore = score;
	            }

	            Map<String, Object> result = ServiceUtil.returnSuccess();
	            result.put("completed", completed);
	            result.put("bestScore", bestScore > 0 ? bestScore : null);
	            return result;

	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error fetching stats: " + e.getMessage());
	        }
	    }
	 public static Map<String, Object> verifyExamPassword(DispatchContext dctx, Map<String, ? extends Object> context) {
		    try {
		        Delegator delegator = dctx.getDelegator();
		        String password = (String) context.get("password");
		        String examId   = (String) context.get("examId");
		        String partyId  = (String) context.get("partyId");

		        GenericValue assignment = EntityQuery.use(delegator)
		                .from("PartyExamRelationship")
		                .where("partyId", partyId, "examId", examId)
		                .queryOne();

		        if (assignment == null)
		            return ServiceUtil.returnError("No exam assigned to this user.");

		        // ✅ Check 1: Is exam expired?
		        java.sql.Timestamp thruDate = assignment.getTimestamp("thruDate");
		        if (thruDate != null && thruDate.before(new java.sql.Timestamp(System.currentTimeMillis()))) {
		            return ServiceUtil.returnError("This exam has expired. You are not allowed to take it.");
		        }

		        // ✅ Check 2: Are there attempts left?
		        long allowedAttempts = Long.parseLong(assignment.get("allowedAttempts").toString());
		        long noOfAttempts    = Long.parseLong(assignment.get("noOfAttempts").toString());
		        if (allowedAttempts > 0 && noOfAttempts >= allowedAttempts) {
		            return ServiceUtil.returnError("No attempts remaining for this exam.");
		        }

		        // ✅ Check 3: Is password set? (null means exam not properly assigned)
		        String storedHashedPassword = assignment.getString("passwordChangesAuto");
		        if (storedHashedPassword == null) {
		            return ServiceUtil.returnError("No password set for this exam. Contact your administrator.");
		        }

		        // ✅ Check 4: Verify password
		        boolean isMatch = HashCrypt.comparePassword(storedHashedPassword, "SHA", password);
		        if (!isMatch)
		            return ServiceUtil.returnError("Invalid password.");

		        Map<String, Object> result = ServiceUtil.returnSuccess("Login successful. You are now allowed to attend the exam.");
		        result.put("partyId", partyId);
		        result.put("examId",  examId);
		        return result;

		    } catch (Exception e) {
		        return ServiceUtil.returnError("Error verifying password: " + e.getMessage());
		    }
		}
}
