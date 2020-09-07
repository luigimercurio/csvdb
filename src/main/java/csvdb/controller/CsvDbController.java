package csvdb.controller;

import csvdb.model.Invoice;
import csvdb.util.CsvDbCounters;
import csvdb.util.CsvDbResponse;
import csvdb.util.CsvDbService;
import csvdb.util.CsvDbUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/csv")
public class CsvDbController {

    final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CsvDbService fileService;

    @PostMapping("/upload")
    public ResponseEntity<CsvDbResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        CsvDbCounters counters;

        counters = new CsvDbCounters();
        if (CsvDbUtil.hasCSVFormat(file)) {
            try {
                fileService.save(file, counters);
                return buildResponse(
                    "Data uploaded successfully: " + file.getOriginalFilename(),
                    HttpStatus.OK,
                    null,
                    counters
                );
            } catch (Exception exception) {
                return buildResponse(
                    "Could not upload the data: " + file.getOriginalFilename(),
                    HttpStatus.EXPECTATION_FAILED,
                    exception,
                    counters
                );
            }
        } else {
            return buildResponse(
                "Uploaded data must be in CSV format",
                HttpStatus.BAD_REQUEST,
                new IllegalArgumentException("Wrong data format"),
                counters
            );
        }
    }

    private ResponseEntity<CsvDbResponse> buildResponse(
        String message, HttpStatus status, Throwable ex, CsvDbCounters counters
    ) {
        CsvDbResponse response;

        logger.debug(ex == null ? message : message + " [" + ex.toString() + ']');
        response = new CsvDbResponse();
        response.setMessage(message);
        response.setSuccess(status == HttpStatus.OK);
        response.setError(ex);
        response.setInsertedInvoices(counters.getTotalCount() - counters.getInitialCount());
        response.setUpdatedInvoices(counters.getUploadedCount() - response.getInsertedInvoices());
        response.setInvoiceCount(counters.getTotalCount());
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/data")
    public ResponseEntity<List<Invoice>> getData() {
        try {
            List<Invoice> invoices = fileService.getAllInvoices();

            if (invoices.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(invoices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile() {
        String filename = "data.csv";
        InputStreamResource file = new InputStreamResource(fileService.load());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/csv"))
            .body(file);
    }

}
