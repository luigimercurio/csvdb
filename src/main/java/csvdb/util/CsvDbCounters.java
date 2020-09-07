package csvdb.util;

public class CsvDbCounters {
    private long initialCount = -1L;
    private long uploadedCount = -1L;
    private long totalCount = -1L;

    public long getInitialCount() {
        return initialCount;
    }

    public void setInitialCount(long initialCount) {
        this.initialCount = initialCount;
    }

    public long getUploadedCount() {
        return uploadedCount;
    }

    public void setUploadedCount(long uploadedCount) {
        this.uploadedCount = uploadedCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
