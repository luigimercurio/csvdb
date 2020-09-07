package csvdb.util;

import csvdb.model.Invoice;

import java.util.Set;

public class CsvDbResponse {
    private boolean success = true;
    private String errorType;
    private String errorDescription;
    private String message;
    private Set<Invoice.Key> duplicateKeys;
    private long updatedInvoices;
    private long insertedInvoices;
    private long invoiceCount;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<Invoice.Key> getDuplicateKeys() {
        return duplicateKeys;
    }

    public void setDuplicateKeys(Set<Invoice.Key> duplicateKeys) {
        this.duplicateKeys = duplicateKeys;
    }

    public long getUpdatedInvoices() {
        return updatedInvoices;
    }

    public void setUpdatedInvoices(long updatedInvoices) {
        this.updatedInvoices = updatedInvoices;
    }

    public long getInsertedInvoices() {
        return insertedInvoices;
    }

    public void setInsertedInvoices(long insertedInvoices) {
        this.insertedInvoices = insertedInvoices;
    }

    public long getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(long invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    public void setError(Throwable error) {
        StringBuilder sb;

        if (error == null) {
            return;
        }
        sb = new StringBuilder();
        while (error instanceof RuntimeException && error.getCause() != null) {
            sb.append(sb.length() == 0 ? "" : ": ");
            sb.append(error.getLocalizedMessage());
            error = error.getCause();
        }
        success = false;
        errorType = error.getClass().getName();
        errorDescription = error.getLocalizedMessage();
        message = sb.toString();
        if (error instanceof DuplicateKeysException) {
            duplicateKeys = ((DuplicateKeysException) error).getDuplicateKeys();
        }
    }

}
