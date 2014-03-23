package system;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.TimeHelper;

public class SystemInfoTest {
    private Thread sysInfoService;
    @BeforeMethod
    public void setUp() throws Exception {
        SystemInfo sysInfo = new SystemInfo();
        sysInfoService = new Thread(sysInfo);
        sysInfoService.start();
    }

    @Test
    public void testIsMetricLogNotEmpty() throws Exception {
        TimeHelper.sleep(SystemInfo.REFRESH_STATE);
        String memoryUsage = SystemInfo.getMetricInfo(Metric.MEMORY_USAGE);
        String totalMemory = SystemInfo.getMetricInfo(Metric.TOTAL_MEMORY);
        String time = SystemInfo.getMetricInfo(Metric.TIME);
        String ccu = SystemInfo.getMetricInfo(Metric.CCU);

        Assert.assertNotEquals("[]", memoryUsage);
        Assert.assertNotEquals("[]", totalMemory);
        Assert.assertNotEquals("[]", time);
        Assert.assertNotEquals("[]", ccu);
    }

    @Test
    public void testMetricValue() {
        final String defaultValue = "defaultValue";
        //String memoryUsage = SystemInfoManager.getServiceMetric(, defaultValue);
    }

    @Test
    public void testIsMetricLogUpdate() throws Exception {
        String memoryUsage = SystemInfo.getMetricInfo(Metric.MEMORY_USAGE);
        String totalMemory = SystemInfo.getMetricInfo(Metric.TOTAL_MEMORY);
        String time = SystemInfo.getMetricInfo(Metric.TIME);
        String ccu = SystemInfo.getMetricInfo(Metric.CCU);

        TimeHelper.sleep(2 * SystemInfo.REFRESH_STATE);
        Assert.assertNotEquals(memoryUsage, SystemInfo.getMetricInfo(Metric.MEMORY_USAGE));
        Assert.assertNotEquals(totalMemory, SystemInfo.getMetricInfo(Metric.TOTAL_MEMORY));
        Assert.assertNotEquals(time, SystemInfo.getMetricInfo(Metric.TIME));
        Assert.assertNotEquals(ccu, SystemInfo.getMetricInfo(Metric.CCU));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        sysInfoService.interrupt();
    }
}
