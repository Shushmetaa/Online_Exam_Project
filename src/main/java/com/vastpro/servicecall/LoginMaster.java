package com.vastpro.servicecall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.webapp.control.LoginWorker;

public class LoginMaster {

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
    public static Map<String, Object> loginUser(HttpServletRequest request, HttpServletResponse response) {
    	
    	request.getSession().setAttribute("_WEBAPP_NAME_", "exam");
    	
    	if (request.getAttribute("security") == null)
            request.setAttribute("security", request.getSession().getServletContext().getAttribute("security"));
        if (request.getAttribute("delegator") == null)
            request.setAttribute("delegator", request.getSession().getServletContext().getAttribute("delegator"));
        
    	String userName = request.getParameter("userName");
    	String password = request.getParameter("password");

        //validation
        if (UtilValidate.isEmpty(userName))
            return ServiceUtil.returnError("Username is required");
        if (UtilValidate.isEmpty(password))
            return ServiceUtil.returnError("Password is required");

        try {
        	
             
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator = getDelegator(request);

            if (dispatcher == null)
                return ServiceUtil.returnError("Unexpected Error Occurred! Try again after Sometime!");

            request.setAttribute("USERNAME", userName);
            request.setAttribute("PASSWORD", password);
            
            String loginResult = LoginWorker.login(request, response);
            
            System.out.println("=== LOGIN DEBUG ===");
            System.out.println("Login result: " + loginResult);
            System.out.println("ERROR_MESSAGE: " + request.getAttribute("_ERROR_MESSAGE_"));
            System.out.println("ERROR_MESSAGE_LIST: " + request.getAttribute("_ERROR_MESSAGE_LIST_"));
            System.out.println("security: " + request.getAttribute("security"));
            System.out.println("=== END DEBUG ===");
            
            if ("success".equalsIgnoreCase(LoginWorker.login(request, response))) {

                Map<String, Object> result = ServiceUtil.returnSuccess("Signed In Successfully!");

                HttpSession session = request.getSession(false);
                if (UtilValidate.isNotEmpty(session)) {

                    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

                    if (UtilValidate.isNotEmpty(userLogin)) {

                        // ── Get role from UserLoginSecurityGroup ───────
                        GenericValue userSecGroup = EntityQuery.use(delegator)
                                .from("UserLoginSecurityGroup")
                                .where("userLoginId", userLogin.getString("userLoginId"))
                                .queryFirst();

                        String groupId = null;
                        if (UtilValidate.isNotEmpty(userSecGroup)) {
                            groupId = userSecGroup.getString("groupId");
                        }

                        // ── Get partyRole ──────────────────────────────
                        GenericValue partyRole = EntityQuery.use(delegator)
                                .from("PartyRole")
                                .where("partyId", userLogin.getString("partyId"))
                                .queryFirst();

                        String roleTypeId = null;
                        if (UtilValidate.isNotEmpty(partyRole)) {
                            roleTypeId = partyRole.getString("roleTypeId");
                        }

                        // ── Set in session ─────────────────────────────
                        session.setAttribute("partyId",    userLogin.getString("partyId"));
                        session.setAttribute("groupId",    groupId);
                        session.setAttribute("roleTypeId", roleTypeId);
                        session.setAttribute("userLoginId", userLogin.getString("userLoginId"));

                        // ── Put in result ──────────────────────────────
                        result.put("partyId",     userLogin.getString("partyId"));
                        result.put("groupId",     groupId);
                        result.put("roleTypeId",  roleTypeId);
                        result.put("userLoginId", userLogin.getString("userLoginId"));
                    }
                }
                return result;
            }

            // Login failed
            String errorMsg = (String) request.getAttribute("_ERROR_MESSAGE_");
            return ServiceUtil.returnError(
                    UtilValidate.isNotEmpty(errorMsg) ? errorMsg : "Invalid username or password");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Unexpected error: " + e.getMessage());
        }
    }
 
    public static Map<String, Object> checkSession(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
     
        try {
            HttpSession session = request.getSession(false);
     
            if (session == null) {
                result.put("responseMessage", "error");
                result.put("message", "No active session");
                return result;
            }
     
            String partyId    = (String) session.getAttribute("partyId");
            String groupId    = (String) session.getAttribute("groupId"); // ← read groupId
            String userLoginId = (String) session.getAttribute("userLoginId");
     
            if (partyId == null || groupId == null) {
                result.put("responseMessage", "error");
                result.put("message", "Not logged in");
                return result;
            }
     
            result.put("responseMessage", "success");
            result.put("partyId",     partyId);
            result.put("role",        groupId); 
            result.put("userLoginId", userLoginId);
            return result;
     
        } catch (Exception e) {
            e.printStackTrace();
            result.put("responseMessage", "error");
            result.put("message", "Session check failed: " + e.getMessage());
            return result;
        }
    }
    
    public static Map<String, Object> forgotPassword(
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator = getDelegator(request);

            String email = request.getParameter("email");

            if (email == null || email.isEmpty())
                return ServiceUtil.returnError("Email is required");

            // 1. Check if user exists
            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin")
                    .where("userLoginId", email)
                    .queryOne();

            if (userLogin == null)
                return ServiceUtil.returnError("No account found with this email");

            // 2. Generate new random password
            String newPassword = generateRandomPassword();

            // 3. Update password in DB
            GenericValue systemUserLogin = delegator.findOne("UserLogin",
                    org.apache.ofbiz.base.util.UtilMisc.toMap(
                    "userLoginId", "system"), false);

            Map<String, Object> updateCtx = new HashMap<>();
            updateCtx.put("userLoginId",           email);
            updateCtx.put("newPassword",           newPassword);
            updateCtx.put("newPasswordVerify",     newPassword);
            updateCtx.put("requirePasswordChange", "N");
            updateCtx.put("userLogin",             systemUserLogin);

            Map<String, Object> updateResult = dispatcher.runSync(
                    "updatePassword", updateCtx);

            if (ServiceUtil.isError(updateResult))
                return ServiceUtil.returnError("Failed to reset password: "
                        + ServiceUtil.getErrorMessage(updateResult));

            // 4. Get first name
            String partyId = userLogin.getString("partyId");
            GenericValue person = EntityQuery.use(delegator)
                    .from("Person")
                    .where("partyId", partyId)
                    .queryOne();

            String firstName = (person != null)
                    ? person.getString("firstName") : "User";

            // 5. Send email
            String mailFrom = EntityUtilProperties.getPropertyValue(
                    "general", "mail.smtp.auth.user",
                    "sphinxofbizz@gmail.com", delegator);

            Map<String, Object> emailCtx = new HashMap<>();
            emailCtx.put("sendTo",      email);
            emailCtx.put("sendFrom",    mailFrom);
            emailCtx.put("subject",     "Your Password Has Been Reset - Sphinx Exam Portal");
            emailCtx.put("contentType", "text/plain");
            emailCtx.put("body",
                    "Dear " + firstName + ",\n\n" +
                    "Your password has been reset successfully.\n\n" +
                    "================================================\n" +
                    "YOUR NEW LOGIN CREDENTIALS\n" +
                    "================================================\n" +
                    "Username : " + email + "\n" +
                    "Password : " + newPassword + "\n\n" +
                    "================================================\n" +
                    "IMPORTANT\n" +
                    "================================================\n" +
                    "* If you did not request this reset,\n" +
                    "  please contact your administrator immediately.\n" +
                    "* Do not share your password with anyone.\n\n" +
                    "Best Regards,\n" +
                    "Sphinx Exam Portal Team");

            dispatcher.runAsync("sendMail", emailCtx);

            return ServiceUtil.returnSuccess("Password reset email sent successfully");

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    private static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}