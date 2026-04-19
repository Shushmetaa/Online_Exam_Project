package com.vastpro.services.userservice;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class GetCertificate {

    public static Map<String, Object> generateUserCertificate(
            DispatchContext dctx,
            Map<String, ?> context) {

        String examId  = (String) context.get("examId");
        String partyId = (String) context.get("partyId");

        try {

            // Get employee full name from Person table
            GenericValue person = EntityQuery.use(dctx.getDelegator())
                    .from("Person")
                    .where("partyId", partyId)
                    .queryOne();

            if (person == null) {
                return ServiceUtil.returnError("No person found for partyId: " + partyId);
            }

            String firstName  = person.getString("firstName")  != null ? person.getString("firstName")  : "";
            String middleName = person.getString("middleName") != null ? " " + person.getString("middleName") : "";
            String lastName   = person.getString("lastName")   != null ? " " + person.getString("lastName")  : "";
            String fullName   = (firstName + middleName + lastName).trim();

            // Get exam name from ExamMaster
            GenericValue exam = EntityQuery.use(dctx.getDelegator())
                    .from("ExamMaster")
                    .where("examId", examId)
                    .queryOne();

            if (exam == null) {
                return ServiceUtil.returnError("No exam found for examId: " + examId);
            }

            String examName = exam.getString("examName");

            // Get best PASSED performance from PartyPerformance
            //   userPassed = 1 → passed
            //   userPassed = 0 → failed → no certificate
            //   We take the highest score among passed attempts
            List<GenericValue> allPerformances = EntityQuery.use(dctx.getDelegator())
                    .from("PartyPerformance")
                    .where("partyId", partyId, "examId", examId, "userPassed", 1L)
                    .orderBy("-score")   // highest score first
                    .queryList();

            if (allPerformances == null || allPerformances.isEmpty()) {
                return ServiceUtil.returnError(
                    "No passed performance found for partyId=" + partyId +
                    " and examId=" + examId +
                    ". Certificate can only be generated for passed exams.");
            }

            // Best passed attempt = first result after ordering by -score
            GenericValue bestPerf = allPerformances.get(0);

            Double scoreValue = bestPerf.getDouble("score");
            String score      = scoreValue != null ? String.format("%.1f%%", scoreValue) : "N/A";
            String grade      = calculateGrade(scoreValue);

            Timestamp completedTs = bestPerf.getTimestamp("date");
            String completedDate  = "N/A";
            if (completedTs != null) {
                completedDate = new SimpleDateFormat("dd MMMM yyyy").format(completedTs);
            }

            Long performanceId = bestPerf.getLong("performanceId");

            //  Get topic-wise scores from DetailedPartyPerformance
            //   Linked by performanceId to the best passed attempt above
            List<GenericValue> topicDetails = EntityQuery.use(dctx.getDelegator())
                    .from("DetailedPartyPerformance")
                    .where("performanceId", performanceId)
                    .orderBy("topicId")
                    .queryList();

            StringBuilder topicRowsXml = new StringBuilder();
            int rowIndex = 0;

            for (GenericValue topic : topicDetails) {
                // Get topic name from ExamTopicDetails using examId + topicId
                String topicId   = topic.getString("topicId");
                String topicName = topicId; // fallback

                GenericValue topicDef = EntityQuery.use(dctx.getDelegator())
                        .from("ExamTopicDetails")
                        .where("examId", examId, "topicId", topicId)
                        .queryOne();
                if (topicDef != null && topicDef.getString("topicName") != null) {
                    topicName = topicDef.getString("topicName");
                }

                Long   correct = topic.getLong("correctQuestionsInthisTopic");
                Long   total   = topic.getLong("totalQuestionsInThisTopic");
                String correctStr = correct != null ? String.valueOf(correct) : "0";
                String totalStr   = total   != null ? String.valueOf(total)   : "0";

                Double userPct = topic.getDouble("userTopicPercentage");
                String pctStr  = userPct != null ? String.format("%.1f%%", userPct) : "N/A";

                String rowBg = (rowIndex % 2 == 0) ? "#f5f5f5" : "#ffffff";
                rowIndex++;

                topicRowsXml
                    .append("<fo:table-row background-color=\"").append(rowBg).append("\">")

                    // Topic Name
                    .append("<fo:table-cell padding=\"3pt\" border=\"0.25pt solid #dddddd\">")
                    .append("<fo:block font-size=\"9pt\" font-family=\"Helvetica\">")
                    .append(escapeXml(topicName)).append("</fo:block>")
                    .append("</fo:table-cell>")

                    // Correct / Total
                    .append("<fo:table-cell padding=\"3pt\" border=\"0.25pt solid #dddddd\">")
                    .append("<fo:block font-size=\"9pt\" font-family=\"Helvetica\" font-weight=\"bold\" color=\"#1a237e\" text-align=\"center\">")
                    .append(correctStr).append(" / ").append(totalStr)
                    .append("</fo:block>")
                    .append("</fo:table-cell>")

                    // Percentage
                    .append("<fo:table-cell padding=\"3pt\" border=\"0.25pt solid #dddddd\">")
                    .append("<fo:block font-size=\"9pt\" font-family=\"Helvetica\" text-align=\"center\" color=\"#666\">")
                    .append(pctStr).append("</fo:block>")
                    .append("</fo:table-cell>")

                    .append("</fo:table-row>");
            }

            if (topicDetails.isEmpty()) {
                topicRowsXml
                    .append("<fo:table-row>")
                    .append("<fo:table-cell number-columns-spanned=\"3\" padding=\"6pt\">")
                    .append("<fo:block text-align=\"center\" color=\"#999\" font-size=\"9pt\">")
                    .append("No topic data available.")
                    .append("</fo:block>")
                    .append("</fo:table-cell>")
                    .append("</fo:table-row>");
            }

            //  Load FO template from disk
            String templatePath = "plugins/exam/widget/certificate/certificateTemplate.fo";
            String foTemplate   = new String(
                    java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(templatePath)));

            //Replace placeholders with real values
            foTemplate = foTemplate
                    .replace("${userName}",   escapeXml(fullName))
                    .replace("${employeeId}", escapeXml(partyId))
                    .replace("${examName}",   escapeXml(examName))
                    .replace("${score}",      score)
                    .replace("${grade}",      escapeXml(grade))
                    .replace("${date}",       completedDate)
                    .replace("${topicRows}",  topicRowsXml.toString());

            // STEPConvert FO → PDF using Apache FOP
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

            FopFactory  fopFactory  = FopFactory.newInstance(new java.io.File(".").toURI());
            Fop         fop         = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOut);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            transformer.transform(
                new StreamSource(new StringReader(foTemplate)),
                new javax.xml.transform.sax.SAXResult(fop.getDefaultHandler())
            );

            // Return PDF bytes
            Map<String, Object> serviceResult = ServiceUtil.returnSuccess();
            serviceResult.put("certificatePdf", pdfOut.toByteArray());
            serviceResult.put("fileName", "certificate_" + partyId + "_" + examId + ".pdf");
            return serviceResult;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("PDF generation failed: " + e.getMessage());
        }
    }

    /**
     * Grade from score — adjust thresholds to match your organisation.
     */
    private static String calculateGrade(Double score) {
        if (score == null) return "N/A";
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        return "Pass";
    }

    private static String escapeXml(String input) {
        if (input == null) return "";
        return input
            .replace("&",  "&amp;")
            .replace("<",  "&lt;")
            .replace(">",  "&gt;")
            .replace("\"", "&quot;")
            .replace("'",  "&apos;");
    }
}