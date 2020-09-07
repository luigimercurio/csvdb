package csvdb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@IdClass(Invoice.Key.class)
@Table(name = "invoices")
public class Invoice {
    public static class Key implements Serializable {
        private String supplierId;
        private String invoiceId;

        public Key(){
        }

        public Key(String supplierId, String invoiceId) {
            this.supplierId = supplierId;
            this.invoiceId = invoiceId;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(supplierId, invoiceId);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Invoice.Key &&
                Objects.equals(supplierId, ((Key) obj).supplierId) &&
                Objects.equals(invoiceId, ((Key) obj).invoiceId);
        }
    }

    @Id
    @Column(name = "supplier_id", length = 20)
    private String supplierId;

    @Id
    @Column(name = "invoice_id", length = 36)
    private String invoiceId;

    @NotNull
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;// (YYYY-MM-DD, date invoice was created)

    @NotNull
    @Column(name = "invoice_amount")
    private Double invoiceAmount;

    @NotNull
    @Column(name = "terms")
    private Integer terms;// (number of days from invoice date until invoice must be paid)

    @Column(name = "payment_date")
    private LocalDate paymentDate;// (YYYY-MM-DD, nullable)

    @Column(name = "payment_amount")
    private Double paymentAmount;// (nullable)

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Integer getTerms() {
        return terms;
    }

    public void setTerms(Integer terms) {
        this.terms = terms;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
