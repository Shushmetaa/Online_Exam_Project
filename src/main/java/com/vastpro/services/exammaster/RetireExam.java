package com.vastpro.services.exammaster;

import java.sql.Timestamp;
import java.util.Map;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class RetireExam {
	
	public static Map<String, Object> retireExam(DispatchContext dctx, Map<String, ? extends Object> context) {
		String examId = (String) context.get("examId");
		String lastModifiedByUserLogin = (String) context.get("lastModifiedByUserLogin");
			
			if (examId == null || examId.isEmpty())
	            return ServiceUtil.returnError("Exam ID is required");
			
			
			 Delegator delegator = dctx.getDelegator();

		        try {
		            // ── Fetch exam 
		            GenericValue exam = EntityQuery.use(delegator)
		                    .from("ExamMaster")
		                    .where("examId", examId)
		                    .queryOne();

		            if (exam == null)
		                return ServiceUtil.returnError("Exam not found: " + examId);

		            // ── Check already retired
		            Timestamp existingThruDate = (Timestamp) exam.get("thruDate");
		            Timestamp now = new Timestamp(System.currentTimeMillis());

		            if (existingThruDate != null && existingThruDate.getTime() < now.getTime())
		                return ServiceUtil.returnError("Exam is already retired on: " + existingThruDate);

		            exam.set("thruDate", now);
		            exam.set("lastModifiedByUserLogin", lastModifiedByUserLogin);
		            delegator.store(exam);

		            // ── Return 
		            Map<String, Object> result = ServiceUtil.returnSuccess("Exam retired successfully");
		            result.put("retiredAt", now.toString());
		            return result;

		        } catch (GenericEntityException e) {
		            return ServiceUtil.returnError("Error retiring exam: " + e.getMessage());
		        }
		}
	
	}
