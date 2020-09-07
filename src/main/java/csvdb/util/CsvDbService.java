package csvdb.util;

import csvdb.model.Invoice;
import csvdb.model.data.InvoiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CsvDbService {
    @Autowired
    InvoiceDao dao;

    public void save(MultipartFile file, CsvDbCounters counters) {
        counters.setInitialCount(dao.count());
        counters.setUploadedCount(0);
        counters.setTotalCount(counters.getInitialCount());
        try {
            List<Invoice> invoices;
            Set<Invoice.Key> duplicates;

            invoices = CsvDbUtil.csvToData(file.getInputStream());
            duplicates = checkDuplicates(invoices);
            if (duplicates.size() != 0) {
                throw new DuplicateKeysException(duplicates);
            }
            dao.saveAll(invoices);
            counters.setUploadedCount(invoices.size());
            counters.setTotalCount(dao.count());
        } catch (Exception e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage(), e);
        }
    }

    private Set<Invoice.Key> checkDuplicates(List<Invoice> invoices) {
        Set<Invoice.Key> set;
        Set<Invoice.Key> duplicates;

        set = new HashSet<>();
        duplicates = new HashSet<>();
        for (Invoice invoice : invoices) {
            Invoice.Key key;

            key = new Invoice.Key(invoice.getSupplierId(), invoice.getInvoiceId());
            if (set.contains(key)) {
                duplicates.add(key);
            } else {
                set.add(key);
            }
        }
        return duplicates;
    }

    public ByteArrayInputStream load() {
        return CsvDbUtil.dataToCsv(dao.findAll());
    }

    public List<Invoice> getAllInvoices() {
        return dao.findAll();
    }

    public void clearTable() {
        dao.deleteAll();
    }
}
