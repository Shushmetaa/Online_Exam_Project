package com.vastpro.services.topicmaster;

import java.util.HashMap;
import java.util.Map;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class DeleteTopicMaster {

	public static Map<String, Object> updateExam(DispatchContext dctx, Map<String, ? extends Object> context){
		try {
				String examId = (String) context.get("examId");
				String topicId = (String) context.get("topicId");
				
				 if (examId == null || examId.isEmpty()) {
		             return ServiceUtil.returnError("Exam ID is required");
		         }
		
		         if (topicId == null || topicId.isEmpty()) {
		             return ServiceUtil.returnError("Topic ID is required");
		         }
		         
		         Delegator delegator = dctx.getDelegator();
		         LocalDispatcher dispatcher = dctx.getDispatcher();
		         
		         GenericValue topic = EntityQuery.use(delegator)
		                 .from("ExamTopicDetails")
		                 .where("examId", examId, "topicId", topicId)
		                 .queryOne();
		         
		         if (topic == null) {
		                return ServiceUtil.returnError("Topic not found");
		            }
		         Map<String, Object> input = new HashMap<>();
		            input.put("examId", examId);
		            input.put("topicId", topicId);

		            Map<String, Object> result = dispatcher.runSync("deleteTopicMasterAuto", input);

		            if (ServiceUtil.isError(result)) {
		                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		            }

		            return ServiceUtil.returnSuccess("Topic deleted successfully");
		            
		}catch (GenericEntityException | GenericServiceException e) {
            return ServiceUtil.returnError("Error deleting topic: " + e.getMessage());
        }
		
	}

}
