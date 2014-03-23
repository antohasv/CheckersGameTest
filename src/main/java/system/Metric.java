package system;

public enum Metric {
    MEMORY_USAGE("memoryUsage"), TOTAL_MEMORY("totalMemory"), TIME("time"), CCU("ccu");

    private String fileName;
    Metric(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return  SystemInfo.SERVICE_DIRECTORY + "/" + fileName;
    }
}