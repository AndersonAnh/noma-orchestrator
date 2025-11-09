package ru.vtb.msa.noma.orchestrator.service;

import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportService {

    private static final String TEMPLATE_PATH = "reports/transfer-receipt.jrxml";

    /* Generate transfer receipt PDF as bytes. Optionally can save to a file if outputPath is provided.
     * @return PDF bytes
     */
    public byte[] generateTransferReceiptPdf(TransferReceiptParams params, Path outputPathIfNotNull) {
        try (InputStream inputStream = new ClassPathResource(TEMPLATE_PATH).getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(inputStream);

            Map<String, Object> paramsMap = getStringObjectMap(params);

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, paramsMap, new JREmptyDataSource());
            byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);

            if (outputPathIfNotNull != null) {
                Files.createDirectories(outputPathIfNotNull.getParent());
                Files.write(outputPathIfNotNull, pdf);
            }
            return pdf;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate transfer receipt PDF", e);
        }
    }

    private static Map<String, Object> getStringObjectMap(TransferReceiptParams params) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("senderAccountId", params.senderAccountId().toString());
        paramsMap.put("receiverAccountId", params.receiverAccountId().toString());
        paramsMap.put("amount", params.amount());
        paramsMap.put("currency", params.currency());
        paramsMap.put("description", params.description());
        paramsMap.put("timestamp", params.timestamp());
        paramsMap.put("senderBalanceAfter", params.senderBalanceAfter());
        paramsMap.put("receiverBalanceAfter", params.receiverBalanceAfter());
        return paramsMap;
    }

    /* Parameters record for report.
     */
    public record TransferReceiptParams(
            UUID senderAccountId,
            UUID receiverAccountId,
            Double amount,
            String currency,
            String description,
            String timestamp,
            Double senderBalanceAfter,
            Double receiverBalanceAfter
    ) {
    }
}
