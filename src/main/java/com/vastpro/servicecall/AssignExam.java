//package com.vastpro.servicecall;
//
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.ofbiz.entity.Delegator;
//import org.apache.ofbiz.entity.GenericValue;
//import org.apache.ofbiz.entity.util.EntityQuery;
//import org.apache.ofbiz.service.LocalDispatcher;
//import org.apache.ofbiz.service.ServiceUtil;
//
//public class AssignExam {
//
//	private static LocalDispatcher getDispatcher(HttpServletRequest request) {
//        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
//        if (dispatcher == null) {
//            dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
//        }
//        return dispatcher;
//    }
//
//    private static Delegator getDelegator(HttpServletRequest request) {
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        if (delegator == null) {
//            delegator = (Delegator) request.getSession().getServletContext().getAttribute("delegator");
//        }
//        return delegator;
//    }
//    public static Map<String, Object> assignExam(HttpServletRequest request, HttpServletResponse response){
//    	try {
//
//			LocalDispatcher dispatcher=getDispatcher(request);
//    	    
//    	    if (dispatcher == null) {
//                return ServiceUtil.returnError("Dispatcher is null");
//            }
//
//    	    GenericValue userLogin=EntityQuery.use(getDelegator(request))
//    	    		.from("UserLogin")
//    	    		.where("userLoginId", "admin")
//    	    		.queryOne();
//    	    
//    	}
//    }
//}
