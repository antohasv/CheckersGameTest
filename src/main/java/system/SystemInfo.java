package system;

import utils.TimeHelper;
import utils.VFS;

public class SystemInfo implements Runnable {
    public static final String SERVICE_DIRECTORY = "statistic";
    public static final String METRIC_DIVIDER = ", ";
    public static final int REFRESH_STATE = 10000;

    public static String getMetricInfo(Metric metric) {
        return ("[" + VFS.readFile(metric.getFilePath()) + "]");
    }

    public void run() {
        cleanLogInMetricFiles();
        updateMetric(false);
        while (true) {
            TimeHelper.sleep(REFRESH_STATE);
            updateMetric(true);
        }
    }

    private void cleanLogInMetricFiles() {
        for (Metric metric : Metric.values()) {
            VFS.cleanFile(metric.getFilePath());
        }
    }

    private void updateMetric(boolean isDivider) {
        for (Metric metric : Metric.values()) {
            String serviceMetric = SystemInfoManager.getServiceMetric(metric, "");
            if (!serviceMetric.equals("")) {
                VFS.writeToEndOfFile(metric.getFilePath(), isDivider ? METRIC_DIVIDER + serviceMetric : serviceMetric);
            }
        }
    }
}