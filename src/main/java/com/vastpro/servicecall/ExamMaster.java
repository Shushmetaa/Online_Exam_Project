package com.vastpro.servicecall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class ExamMaster {
	
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
	
    public static Map<String,Object> createExam(HttpServletRequest request, HttpServletResponse response){
    	try {
	    	String examName = request.getParameter("examName");
	    	String description = request.getParameter("description");
	    	String noOfQuestions = request.getParameter("noOfQuestions");
	    	String duration = request.getParameter("duration");
	    	String passPercentage = request.getParameter("passPercentage");
	    	
    	    LocalDispatcher dispatcher=getDispatcher(request);
    	    
    	    if (dispatcher == null) {
                return ServiceUtil.returnError("Dispatcher is null");
            }

    	    GenericValue userLogin=EntityQuery.use(getDelegator(request))
    	    		.from("UserLogin")
    	    		.where("userLoginId", "admin")
    	    		.queryOne();
    	    
    	    Map<String, Object> createData =new HashMap<>();
    	    createData.put("examName", examName);
    	    createData.put("description", description);
    	    createData.put("noOfQuestions",noOfQuestions);
    	    createData.put("duration",  duration);
    	    createData.put("passPercentage", passPercentage);
    	    createData.put("userLogin", userLogin);
    	    
    	    Map<String,Object> serviceResult=dispatcher.runSync("createExam", createData);
    	    
    	    if(ServiceUtil.isError(serviceResult)) {
    	    	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
    	    }
    	    
    	    Map<String,Object> responseMap = ServiceUtil.returnSuccess("Exam Created Successfully");
            responseMap.put("examId", serviceResult.get("examId"));

            return responseMap;
    	}catch(Exception e) {
      		return ServiceUtil.returnError("Exam Creation Failed:" +e.getMessage());
    	}	 
    }
    	public static Map<String,Object> updateExam( HttpServletRequest request, HttpServletResponse response){
			
    		try {
	    		
	    		String examId = request.getParameter("examId");
	    		String examName = request.getParameter("examName");
	    		String description = request.getParameter("description");
	    		String noOfQuestions = request.getParameter("noOfQuestions");
	    		String duration = request.getParameter("duration");
	    		String passPercentage = request.getParameter("passPercentage");
		    	
	    		if (examId == null || examId.isEmpty()) {
	    	            return ServiceUtil.returnError("Exam ID is required");
	    	        }
	    		LocalDispatcher dispatcher=getDispatcher(request);

	    		GenericValue userLogin=EntityQuery.use(getDelegator(request))
	    	    		.from("UserLogin")
	    	    		.where("userLoginId", "admin")
	    	    		.queryOne();
		    	
		    	Map<String, Object> updateData = new HashMap<>();
		    
		    	updateData.put("examId", examId);
		    	updateData.put("examName", examName);
		    	updateData.put("description", description);
		    	updateData.put("noOfQuestions", noOfQuestions);
		    	updateData.put("duration", duration);
		    	updateData.put("passPercentage", passPercentage);
		    	updateData.put("userLogin", userLogin);
		    	
		    	Map<String, Object> result = dispatcher.runSync("updateExam", updateData);
		    	
		    	return result;
		    	
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    		return null;
	    	}
    }
    	public static Map<String,Object> retireExam(HttpServletRequest request, HttpServletResponse response){
    		try {
    			String examId = request.getParameter("examId");
    			String lastModifiedByUserLogin = request.getParameter("lastModifiedByUserLogin");
    			
    			if (examId == null || examId.isEmpty())
    	            return ServiceUtil.returnError("Exam ID is required");
    			
    			LocalDispatcher dispatcher=getDispatcher(request);

	    		GenericValue userLogin=EntityQuery.use(getDelegator(request))
	    	    		.from("UserLogin")
	    	    		.where("userLoginId", "admin")
	    	    		.queryOne();

    			Map<String, Object> retireData = new HashMap<>();
                retireData.put("examId",examId);
                retireData.put("lastModifiedByUserLogin","admin");
                retireData.put("userLogin", userLogin);
    			
                Map<String, Object> result = dispatcher.runSync("retireExam", retireData);
        
    	    	
                return result;
    				
    			}catch (GenericEntityException | GenericServiceException e) {
    	            return ServiceUtil.returnError("Error retiring exam: " + e.getMessage());
    				
    			}
    		}
			public static Map<String, Object> deleteExam(HttpServletRequest request, HttpServletResponse response,String examId) {
				try {
	
			        if (examId == null || examId.isEmpty())
			            return ServiceUtil.returnError("Exam ID is required");
	
			        LocalDispatcher dispatcher =getDispatcher(request);
	
			        GenericValue userLogin = EntityQuery
			                .use(getDelegator(request))
			                .from("UserLogin")
			                .where("userLoginId", "admin")
			                .queryOne();
	
			        Map<String, Object> deleteData = new HashMap<>();
			        deleteData.put("examId",    examId);
			        deleteData.put("userLogin", userLogin);
	
			        Map<String, Object> result =
			            dispatcher.runSync("deleteExam", deleteData);
	
			        return result;
	
			    } catch (GenericEntityException | GenericServiceException e) {
			        return ServiceUtil.returnError(
			            "Error deleting exam: " + e.getMessage());
			    }
			}
		
		public static Map<String, Object> getExam( HttpServletRequest request,  HttpServletResponse response) {
			try {

				String examId = request.getParameter("examId");
				
				LocalDispatcher dispatcher=getDispatcher(request);
			    
			    GenericValue userLogin=EntityQuery.use(getDelegator(request))
			    		.from("UserLogin")
			    		.where("userLoginId", "admin")
			    		.queryOne();
			    
			    Map<String, Object> input = new HashMap<>();

		        input.put("examId", examId);
		        input.put("userLogin", userLogin); 

		        Map<String, Object> result = dispatcher.runSync("getExam", input);

		        if (ServiceUtil.isError(result)) {
		            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
		        } else {
		            return result;
		        }

		    } catch (GenericEntityException | GenericServiceException e) {
		        return ServiceUtil.returnError(e.getMessage());
		    }
		}
		public static Map<String, Object> getExams(HttpServletRequest request, HttpServletResponse response) {
             try {
                  Delegator delegator = getDelegator(request);

                       List<GenericValue> exams = EntityQuery.use(delegator)
                                    .from("ExamMaster")
                                    .orderBy("examId")
                                    .queryList();

                       Map<String, Object> result = ServiceUtil.returnSuccess("Exams fetched");
                       result.put("examList", exams);
                       return result;

              } catch (GenericEntityException e) {
                   return ServiceUtil.returnError("Failed: " + e.getMessage());
              }
         }
		
		public static Map<String, Object> getAssignedUsers(HttpServletRequest request, HttpServletResponse response) {
		    
			try {
				
		        Delegator delegator = getDelegator(request);

		        List<GenericValue> userLogins = EntityQuery.use(delegator)
		                .from("UserLogin")
		                .where("enabled", "Y")
		                .queryList();

		        List<Map<String, Object>> userList = new ArrayList<>();

		        for (GenericValue ul : userLogins) {

		            String partyId = ul.getString("partyId");
		            String userLoginId = ul.getString("userLoginId");

		            if (partyId == null) continue;

		            GenericValue person = EntityQuery.use(delegator)
		                    .from("Person")
		                    .where("partyId", partyId)
		                    .queryOne();

		            if (person != null) {
		                Map<String, Object> user = new HashMap<>();

		                user.put("partyId", partyId);
		                user.put("userLoginId", userLoginId);
		                user.put("firstName", person.getString("firstName"));
		                user.put("lastName", person.getString("lastName"));

		                userList.add(user);
		            }
		        }

		        Map<String, Object> result = ServiceUtil.returnSuccess();
		        result.put("userList", userList);
		        return result;

		    } catch (Exception e) {
		        return ServiceUtil.returnError("Error fetching users: " + e.getMessage());
		    }
		}
		public static Map<String, Object> getNums(HttpServletRequest request, HttpServletResponse response) {
		    try {
		        Delegator delegator = getDelegator(request);

		        long totalExams = EntityQuery.use(delegator)
		            .from("ExamMaster")
		            .queryCount();

		        long totalUsers = EntityQuery.use(delegator)
		            .from("PartyRole")
		            .where("roleTypeId", "SPHINX_USER")
		            .queryCount();

		        long assignedUsers = EntityQuery.use(delegator)
		            .from("PartyExamRelationship")
		            .queryCount();

		        long totalQuestions = EntityQuery.use(delegator)
		            .from("QuestionBankMaster")
		            .queryCount();

		        Map<String, Object> result = ServiceUtil.returnSuccess();
		        result.put("totalExams", totalExams);
		        result.put("totalUsers", totalUsers);
		        result.put("assignedUsers", assignedUsers);
		        result.put("totalQuestions", totalQuestions);
		        return result;

		    } catch (GenericEntityException e) {
		        return ServiceUtil.returnError("Failed: " + e.getMessage());
		    }
		}

		public static Map<String, Object> searchAllExams(HttpServletRequest request, HttpServletResponse response) {
		    try {
		        Delegator delegator = getDelegator(request);

		        String keyword = request.getParameter("keyword") != null 
		        	    ? request.getParameter("keyword") 
		        	    : (String) request.getAttribute("keyword");

		        	if (keyword == null) keyword = "";

		        List<GenericValue> allExams = EntityQuery.use(delegator)
		                .from("ExamMaster")
		                .queryList();

		        List<Map<String, Object>> filteredExams = new ArrayList<>();

		        for (GenericValue exam : allExams) {
		            String examName    = exam.getString("examName") != null ? exam.getString("examName").toLowerCase() : "";
		            String description = exam.getString("description") != null ? exam.getString("description").toLowerCase() : "";
		            String search      = keyword != null ? keyword.toLowerCase().trim() : "";

		            if (search.isEmpty() || examName.contains(search) || description.contains(search)) {
		                Map<String, Object> examMap = new HashMap<>();
		                examMap.put("examId",          exam.getString("examId"));
		                examMap.put("examName",        exam.getString("examName"));
		                examMap.put("description",     exam.getString("description"));
		                examMap.put("noOfQuestions",   exam.getString("noOfQuestions"));
		                examMap.put("duration",        exam.getString("duration"));
		                examMap.put("passPercentage",  exam.getString("passPercentage"));
		                filteredExams.add(examMap);
		            }
		        }

		        Map<String, Object> result = ServiceUtil.returnSuccess("success");
		        result.put("examList", filteredExams);
		        return result;

		    } catch (Exception e) {
		        e.printStackTrace();
		        return ServiceUtil.returnError("Search failed: " + e.getMessage());
		    }
		}
		public static Map<String, Object> searchAssignedExams(HttpServletRequest request, HttpServletResponse response) {
		    try {
		        Delegator delegator = getDelegator(request);

		        // 1️⃣ Get logged-in user
		        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		        if (userLogin == null) {
		            return ServiceUtil.returnError("User not logged in");
		        }

		        String partyId = userLogin.getString("partyId");

		        // 2️⃣ Get assigned exams
		        List<GenericValue> assignedList = EntityQuery.use(delegator)
		                .from("PartyExamRelationship")
		                .where("partyId", partyId)
		                .queryList();

		        String keyword = request.getParameter("keyword") != null
		                ? request.getParameter("keyword").toLowerCase()
		                : "";

		        List<Map<String, Object>> filteredExams = new ArrayList<>();

		        // 3️⃣ Loop assigned exams
		        for (GenericValue rel : assignedList) {

		            String examId = rel.getString("examId");

		            // Get exam details
		            GenericValue exam = EntityQuery.use(delegator)
		                    .from("ExamMaster")
		                    .where("examId", examId)
		                    .queryOne();

		            if (exam == null) continue;

		            String examName = exam.getString("examName") != null
		                    ? exam.getString("examName").toLowerCase() : "";

		            String description = exam.getString("description") != null
		                    ? exam.getString("description").toLowerCase() : "";

		            // 4️⃣ Apply search
		            if (keyword.isEmpty() || examName.contains(keyword) || description.contains(keyword)) {

		                Map<String, Object> examMap = new HashMap<>();
		                examMap.put("examId", exam.getString("examId"));
		                examMap.put("examName", exam.getString("examName"));
		                examMap.put("description", exam.getString("description"));
		                examMap.put("noOfQuestions", exam.getString("noOfQuestions"));
		                examMap.put("duration", exam.getString("duration"));
		                examMap.put("passPercentage", exam.getString("passPercentage"));

		                filteredExams.add(examMap);
		            }
		        }

		        Map<String, Object> result = ServiceUtil.returnSuccess("success");
		        result.put("examList", filteredExams);
		        return result;

		    } catch (Exception e) {
		        e.printStackTrace();
		        return ServiceUtil.returnError("Search failed: " + e.getMessage());
		    }
		}
		public static Map<String, Object> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
		    try {
		        Delegator delegator = getDelegator(request);

		        List<GenericValue> roles = EntityQuery.use(delegator)
		            .from("PartyRole")
		            .where("roleTypeId", "SPHINX_USER")
		            .queryList();

		        List<Map<String, Object>> userList = new ArrayList<>();

		        for (GenericValue role : roles) {
		            String partyId = role.getString("partyId");
		            if (partyId == null) continue;

		            GenericValue person = EntityQuery.use(delegator)
		                .from("Person")
		                .where("partyId", partyId)
		                .queryOne();

		            GenericValue userLogin = EntityQuery.use(delegator)
		                .from("UserLogin")
		                .where("partyId", partyId)
		                .queryFirst();

		            Map<String, Object> user = new HashMap<>();
		            user.put("partyId", partyId);
		            user.put("userLoginId", userLogin != null ? userLogin.getString("userLoginId") : "");
		            user.put("firstName",   person   != null ? person.getString("firstName")   : "");
		            user.put("lastName",    person   != null ? person.getString("lastName")    : "");
		            user.put("roleTypeId",  "SPHINX_USER");
		            userList.add(user);
		        }

		        Map<String, Object> result = ServiceUtil.returnSuccess();
		        result.put("totalUsers", userList.size());
		        result.put("userList", userList);
		        return result;

		    } catch (Exception e) {
		        return ServiceUtil.returnError("Failed: " + e.getMessage());
		    }
		}
}
