package system;

public enum Metric {
    MEMORY_USAGE("MemoryUsage"), TOTAL_MEMORY("TotalMemory"), TIME("Time"), CCU("CCU");

    private String fileName;
    Metric(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return  SystemInfo.SERVICE_DIRECTORY + "/" + fileName;
    }
}