package com.vastpro.services.exammaster;

import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetExam {

	public Map<String, Object> getTopic(DispatchContext dctx, Map<String, ? extends Object> context){
		try {
			String examId = (String) context.get("examId");
			
			if(examId == null || examId.isEmpty()) {
				return ServiceUtil.returnError("Exam Id is required");
			}
			
			Delegator delegator = dctx.getDelegator();
			List<GenericValue> exams = EntityQuery.use(delegator)
                    .from("ExamMaster")
                    .where("examId", examId)
                    .queryList();
			if(exams == null || exams.isEmpty()) {
				return ServiceUtil.returnError("Exam not found");
			}
			Map<String, Object> result = ServiceUtil.returnSuccess("Exams fetched successfully");
	         result.put("examList", exams);
	         return result;
	         
     
		}catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error fetching exams: " + e.getMessage());
			
		}
	}
}
