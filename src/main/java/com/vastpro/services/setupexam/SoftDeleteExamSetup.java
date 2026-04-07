package com.vastpro.services.setupexam;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SoftDeleteExamSetup {
	
	public static Map<String, Object> softDeleteExamSetup(DispatchContext dctx, Map<String, ? extends Object> context) {
        
		try {
            String examId = (String) context.get("examId");

            if (examId == null || examId.isEmpty()) {
                return ServiceUtil.returnError("Exam ID is required");
            }

            LocalDispatcher dispatcher = dctx.getDispatcher();
            
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("examId",   examId);
            updateData.put("statusId", "INACTIVE");
            updateData.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync("updateExamSetupDetailsAuto", updateData);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            return ServiceUtil.returnSuccess("Exam setup deactivated successfully");

        } catch (Exception e) {
            return ServiceUtil.returnError("Error in soft delete: " + e.getMessage());
        }
    }

}
