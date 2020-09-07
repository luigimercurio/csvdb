package csvdb.util;

import csvdb.model.Invoice;
import org.apache.commons.csv.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvDbUtil {
    public static String MIME_TYPE = "text/csv";

    private static final String SUPPLIER_ID = "Supplier Id";
    private static final String INVOICE_ID = "Invoice Id";
    private static final String INVOICE_DATE = "Invoice Date";
    private static final String INVOICE_AMOUNT = "Invoice Amount";
    private static final String TERMS = "Terms";
    private static final String PAYMENT_DATE = "Payment Date";
    private static final String PAYMENT_AMOUNT = "Payment Amount";

    public static boolean hasCSVFormat(MultipartFile file) {
        return MIME_TYPE.equals(file.getContentType());
    }

    public static List<Invoice> csvToData(InputStream is) {
        try (
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(
                fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
        ) {
            List<Invoice> invoices = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Invoice invoice = new Invoice();
                String paymentDate;
                String paymentAmount;

                invoice.setSupplierId(notNull(csvRecord, SUPPLIER_ID));
                invoice.setInvoiceId(notNull(csvRecord, INVOICE_ID));
                invoice.setInvoiceDate(LocalDate.parse(notNull(csvRecord, INVOICE_DATE), DateTimeFormatter.ISO_LOCAL_DATE));
                invoice.setInvoiceAmount(Double.parseDouble(notNull(csvRecord, INVOICE_AMOUNT)));
                invoice.setTerms(Integer.parseInt(notNull(csvRecord, TERMS)));
                paymentDate = csvRecord.get(PAYMENT_DATE);
                invoice.setPaymentDate(empty(paymentDate) ? null : LocalDate.parse(paymentDate, DateTimeFormatter.ISO_LOCAL_DATE));
                paymentAmount = csvRecord.get(PAYMENT_AMOUNT);
                invoice.setPaymentAmount(empty(paymentAmount) ? null : Double.parseDouble(paymentAmount));
                invoices.add(invoice);
            }
            return invoices;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    private static boolean empty(String value) {
        return value == null || value.length() == 0;
    }

    private static String notNull(CSVRecord record, String field) {
        String value = record.get(field);

        if (empty(value)) {
            throw new IllegalArgumentException("Field '" + field + "' cannot be empty");
        }
        return value;
    }

    public static ByteArrayInputStream dataToCsv(List<Invoice> invoices) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)
        ) {
            List<String> header = Arrays.asList(
                SUPPLIER_ID,
                INVOICE_ID,
                INVOICE_DATE,
                INVOICE_AMOUNT,
                TERMS,
                PAYMENT_DATE,
                PAYMENT_AMOUNT
            );
            csvPrinter.printRecord(header);
            for (Invoice invoice : invoices) {
                LocalDate paymentDate;
                Double paymentAmount;

                paymentDate = invoice.getPaymentDate();
                paymentAmount = invoice.getPaymentAmount();
                List<String> data = Arrays.asList(
                    invoice.getSupplierId(),
                    invoice.getInvoiceId(),
                    invoice.getInvoiceDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    String.format("%.2f", invoice.getInvoiceAmount()),
                    invoice.getTerms().toString(),
                    paymentDate == null ? "" : paymentDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    paymentAmount == null ? "" : String.format("%.2f", paymentAmount)
                );
                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}
