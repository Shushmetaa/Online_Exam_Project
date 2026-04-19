package com.vastpro.services.examreport;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.math.BigDecimal;
import java.util.*;
public class CreateSendExamReportCsvEmail {

	    private static final String MODULE =
	        CreateSendExamReportCsvEmail.class.getName();

	    public static Map<String, Object> sendExamReportCsvEmail(
	            DispatchContext dctx,
	            Map<String, ? extends Object> context) {

	        Delegator delegator = dctx.getDelegator();

	        String partyId       = (String) context.get("partyId");
	        String examId        = (String) context.get("examId");
	        String performanceId = (String) context.get("performanceId");

	        try {

	            // ── 1. Fetch PartyPerformance ───────────────────────────
	            GenericValue performance = EntityQuery.use(delegator)
	                .from("PartyPerformance")
	                .where("performanceId", Long.parseLong(performanceId))
	                .queryOne();

	            if (performance == null) {
	                return ServiceUtil.returnError(
	                    "Performance record not found for performanceId: "
	                    + performanceId);
	            }

	            // ── 2. Fetch ExamMaster ─────────────────────────────────
	            GenericValue exam = EntityQuery.use(delegator)
	                .from("ExamMaster")
	                .where("examId", examId)
	                .queryOne();

	            if (exam == null) {
	                return ServiceUtil.returnError(
	                    "Exam not found for examId: " + examId);
	            }

	            // ── 3. Fetch DetailedPartyPerformance (topic breakdown) ─
	            List<GenericValue> topicPerformance = EntityQuery.use(delegator)
	                .from("DetailedPartyPerformance")
	                .where("partyId",       partyId,
	                       "examId",        examId,
	                       "performanceId", Long.parseLong(performanceId))
	                .queryList();

	            // ── 4. Fetch user email ─────────────────────────────────
	            String toEmail = getPartyEmail(delegator, partyId);
	            if (toEmail == null) {
	                return ServiceUtil.returnError(
	                    "No email address found for partyId: " + partyId);
	            }

	            // ── 5. Build CSV content ────────────────────────────────
	            String csvContent = buildCsv(performance, exam, topicPerformance);

	            // ── 6. Send email with CSV attachment ───────────────────
	            sendEmailWithCsvAttachment(
	                toEmail,
	                exam.getString("examName"),
	                csvContent,
	                performanceId);

	            return ServiceUtil.returnSuccess(
	                "Exam report CSV emailed successfully.");

	        } catch (Exception e) {
	            Debug.logError(e, MODULE);
	            return ServiceUtil.returnError(
	                "Error sending exam report email: " + e.getMessage());
	        }
	    }

	    // ── Get email address for party ─────────────────────────────────
	    private static String getPartyEmail(
	            Delegator delegator, String partyId) throws Exception {

	        List<GenericValue> contactMechs = EntityQuery.use(delegator)
	            .from("PartyAndContactMech")
	            .where("partyId",           partyId,
	                   "contactMechTypeId", "EMAIL_ADDRESS")
	            .queryList();

	        if (contactMechs != null && !contactMechs.isEmpty()) {
	            return contactMechs.get(0).getString("infoString");
	        }
	        return null;
	    }

	    // ── Build CSV string ────────────────────────────────────────────
	    private static String buildCsv(
	            GenericValue performance,
	            GenericValue exam,
	            List<GenericValue> topicPerformance) {

	        StringBuilder csv = new StringBuilder();

	        // ── Section 1: Overall Result ───────────────────────────────
	        csv.append("OVERALL RESULT\n");
	        csv.append("Field,Value\n");
	        csv.append("Exam Name,")
	           .append(safe(exam.getString("examName"))).append("\n");
	        csv.append("Exam ID,")
	           .append(safe(exam.getString("examId"))).append("\n");
	        csv.append("Attempted On,")
	           .append(safe(String.valueOf(performance.getTimestamp("date"))))
	           .append("\n");
	        csv.append("Score (%),")
	           .append(safe(String.valueOf(performance.getBigDecimal("score"))))
	           .append("\n");
	        csv.append("Total Questions,")
	           .append(safe(String.valueOf(performance.getLong("noOfQuestions"))))
	           .append("\n");
	        csv.append("Correct Answers,")
	           .append(safe(String.valueOf(performance.getLong("totalCorrect"))))
	           .append("\n");
	        csv.append("Wrong Answers,")
	           .append(safe(String.valueOf(performance.getLong("totalWrong"))))
	           .append("\n");

	        Integer userPassed = performance.getInteger("userPassed");
	        csv.append("Result,")
	           .append(userPassed != null && userPassed == 1 ? "PASSED" : "FAILED")
	           .append("\n");
	        csv.append("Attempt No,")
	           .append(safe(String.valueOf(performance.getLong("attemptNo"))))
	           .append("\n");

	        // ── Section 2: Topic Breakdown ──────────────────────────────
	        if (topicPerformance != null && !topicPerformance.isEmpty()) {
	            csv.append("\nTOPIC BREAKDOWN\n");
	            csv.append("Topic ID,Your %,Pass %,"
	                     + "Correct Questions,Total Questions,Result\n");

	            for (GenericValue topic : topicPerformance) {
	                BigDecimal userPct =
	                    topic.getBigDecimal("userTopicPercentage");
	                BigDecimal passPct =
	                    topic.getBigDecimal("topicPassPercentage");
	                Integer topicPassed =
	                    topic.getInteger("userPassedThisTopic");

	                csv.append(safe(topic.getString("topicId"))).append(",")
	                   .append(userPct  != null ? userPct  : "0").append(",")
	                   .append(passPct  != null ? passPct  : "0").append(",")
	                   .append(safe(String.valueOf(
	                       topic.getLong("correctQuestionsInthisTopic"))))
	                   .append(",")
	                   .append(safe(String.valueOf(
	                       topic.getLong("totalQuestionsInThisTopic"))))
	                   .append(",")
	                   .append(topicPassed != null && topicPassed == 1
	                           ? "PASS" : "FAIL")
	                   .append("\n");
	            }
	        }

	        return csv.toString();
	    }

	    // ── Null-safe cell value ────────────────────────────────────────
	    private static String safe(String value) {
	        return value != null ? value : "";
	    }

	    // ── Send email with CSV attachment ──────────────────────────────
	    private static void sendEmailWithCsvAttachment(
	            String toEmail,
	            String examName,
	            String csvContent,
	            String performanceId) throws Exception {

	        Properties props = new Properties();
	        props.put("mail.smtp.host",            "smtp.yourdomain.com");
	        props.put("mail.smtp.port",            "587");
	        props.put("mail.smtp.auth",            "true");
	        props.put("mail.smtp.starttls.enable", "true");

	        Session session = Session.getInstance(props, new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(
	                    "noreply@yourdomain.com",
	                    "your_smtp_password");
	            }
	        });

	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress("noreply@yourdomain.com"));
	        message.setRecipients(
	            Message.RecipientType.TO,
	            InternetAddress.parse(toEmail));
	        message.setSubject("Your Exam Report — " + examName);

	        // Email body
	        MimeBodyPart textPart = new MimeBodyPart();
	        textPart.setText(
	            "Dear Candidate,\n\n"
	          + "Please find your exam report attached as a CSV file.\n"
	          + "You can open it directly in Microsoft Excel or Google Sheets.\n\n"
	          + "Exam: " + examName + "\n\n"
	          + "Regards,\nExam Team");

	        // CSV attachment
	        MimeBodyPart attachmentPart = new MimeBodyPart();
	        byte[] csvBytes = csvContent.getBytes("UTF-8");
	        DataSource dataSource =
	            new ByteArrayDataSource(csvBytes, "text/csv");
	        attachmentPart.setDataHandler(new DataHandler(dataSource));
	        attachmentPart.setFileName(
	            "ExamReport_" + performanceId + ".csv");

	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(textPart);
	        multipart.addBodyPart(attachmentPart);
	        message.setContent(multipart);

	        Transport.send(message);
	    }
}
