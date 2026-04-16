package com.vastpro.services.userservice;

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
	 public static Map<String,Object>verifyExamPassword(DispatchContext dctx, Map<String, ? extends Object> context) {
	        try {
	        	Delegator delegator=dctx.getDelegator();
	   	        String password = (String) context.get("password");
                String examId=(String) context.get("examId");
	        	 String partyId      = (String) context.get("partyId");

		            GenericValue assignment = EntityQuery.use(delegator)
		                    .from("PartyExamRelationship")  
		                    .where("partyId", partyId,
		                    		"examId",examId)
		                    .queryOne();
		            
		            if (assignment == null) {
		                return ServiceUtil.returnError("No exam assigned to this user.");
		            }
		            
		            String storedHashedPassword = assignment.getString("passwordChangesAuto"); 
		            
		            boolean isMatch = HashCrypt.comparePassword(storedHashedPassword, "SHA" , password);
			           
		            if (!isMatch)
		                return ServiceUtil.returnError("Invalid password");
		            
		            Map<String, Object> result = ServiceUtil.returnSuccess("Login successful now you allowed to attend the exam");
		            result.put("partyId",  partyId);
		            System.out.println(partyId);
		            result.put("examId",  examId);
		            return result;
		            
	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error Yout password is incorrect: " + e.getMessage());
	        }
	        }
}
