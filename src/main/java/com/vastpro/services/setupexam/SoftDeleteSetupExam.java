package com.vastpro.services.setupexam;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SoftDeleteSetupExam {
	
	public static Map<String, Object> softDeletePartyExam(DispatchContext dctx, Map<String, ? extends Object> context) {
        
		try {
			
            String examId  = (String) context.get("examId");
            
            String partyId = (String) context.get("partyId");

            if (examId == null || examId.isEmpty()) {
                return ServiceUtil.returnError("Exam ID is required");
            }
            if (partyId == null || partyId.isEmpty()) {
                return ServiceUtil.returnError("Party ID is required");
            }

            LocalDispatcher dispatcher = dctx.getDispatcher();
            
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            Map<String, Object> deleteData = new HashMap<>();
            deleteData.put("examId",   examId);
            deleteData.put("partyId",  partyId);
            deleteData.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync(
                "removePartyExamRelationshipAuto", deleteData);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            return ServiceUtil.returnSuccess("Party exam relationship removed");

        } catch (Exception e) {
            return ServiceUtil.returnError("Error in soft delete: " + e.getMessage());
        }
    }

}
