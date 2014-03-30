package system;

import frontend.UserDataImpl;
import utils.TimeHelper;

public class SystemInfoManager {
    private static Runtime runtime;

    static {
        runtime = Runtime.getRuntime();
    }

    public static String getServiceMetric(Metric metric, String defaultValue) {
        String value = defaultValue;
        switch (metric) {
            case MEMORY_USAGE:
                value = String.valueOf((int) runtime.totalMemory() - runtime.freeMemory());
                break;
            case TOTAL_MEMORY:
                value = String.valueOf((int) runtime.totalMemory());
                break;
            case TIME:
                value = TimeHelper.getTime();
                break;
            case CCU:
                value = String.valueOf(UserDataImpl.getCCU());
                break;
        }
        return value;
    }
}
