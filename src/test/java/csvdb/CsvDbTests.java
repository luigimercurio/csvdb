package csvdb;

import csvdb.controller.CsvDbController;
import csvdb.model.Invoice;
import csvdb.util.CsvDbResponse;
import csvdb.util.CsvDbService;
import csvdb.util.CsvDbUtil;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {
    CsvDbController.class,
    CsvDbService.class
})
class CsvDbTests {
    static final String CSV_1 = "src/test/resources/csv/invoice_data_1.csv";
    static final String CSV_2 = "src/test/resources/csv/invoice_data_2.csv";
    static final String CSV_BAD = "src/test/resources/csv/invoice_data_duplicates.csv";

    @Autowired
    CsvDbController controller;

    @Autowired
    CsvDbService service;

    @Before
    public void clearDB() {
        service.clearTable();
    }

    private ResponseEntity<CsvDbResponse> uploadFile(String file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            MockMultipartFile multipartFile;

            multipartFile = new MockMultipartFile("data", "test.csv", "text/csv", fis);
            return controller.uploadFile(multipartFile);
        }
    }

    @Test
    void uploadFileTest() throws Exception {
        ResponseEntity<CsvDbResponse> responseEntity;
        CsvDbResponse response;

        responseEntity = uploadFile(CSV_1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(1000L, response.getInvoiceCount());
        assertEquals(1000L, response.getInsertedInvoices());
        assertEquals(0, response.getUpdatedInvoices());
    }

    @Test
    void reuploadFileTest() throws Exception {
        ResponseEntity<CsvDbResponse> responseEntity;
        CsvDbResponse response;

        uploadFile(CSV_1);
        responseEntity = uploadFile(CSV_1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(1000L, response.getInvoiceCount());
        assertEquals(0, response.getInsertedInvoices());
        assertEquals(1000L, response.getUpdatedInvoices());
    }

    @Test
    void uploadTwoFilesTest() throws Exception {
        ResponseEntity<CsvDbResponse> responseEntity;
        CsvDbResponse response;

        uploadFile(CSV_1);
        responseEntity = uploadFile(CSV_2);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(1969L, response.getInvoiceCount());
        assertEquals(969L, response.getInsertedInvoices());
        assertEquals(31L, response.getUpdatedInvoices());
    }

    @Test
    void uploadBadFileTest() throws Exception {
        ResponseEntity<CsvDbResponse> responseEntity;
        CsvDbResponse response;

        responseEntity = uploadFile(CSV_BAD);
        assertEquals(HttpStatus.EXPECTATION_FAILED, responseEntity.getStatusCode());
        response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(0, response.getInvoiceCount());
        assertEquals(0, response.getInsertedInvoices());
        assertEquals(0, response.getUpdatedInvoices());
        assertEquals(19, response.getDuplicateKeys().size());
    }

    @Test
    void testData() throws Exception {
        List<Invoice> invoices1;
        List<Invoice> invoices2;

        try (InputStream is = new FileInputStream(CSV_1)) {
            invoices1 = CsvDbUtil.csvToData(is);
        }
        uploadFile(CSV_1);
        invoices2 = controller.getData().getBody();
        assertNotNull(invoices2);
        assertEquals(invoices1.size(), invoices2.size());
    }

    @Test
    void testDownload() throws Exception {
        Resource resource;
        List<Invoice> invoices1;
        List<Invoice> invoices2;

        try (InputStream is = new FileInputStream(CSV_1)) {
            invoices1 = CsvDbUtil.csvToData(is);
        }
        uploadFile(CSV_1);
        resource = controller.getFile().getBody();
        assertNotNull(resource);
        invoices2 = CsvDbUtil.csvToData(resource.getInputStream());
        assertEquals(invoices1.size(), invoices2.size());
    }
}
