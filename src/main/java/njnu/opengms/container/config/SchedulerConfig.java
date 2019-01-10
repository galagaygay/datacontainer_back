package njnu.opengms.container.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @ClassName SchedulerConfig
 * @Description todo
 * @Author sun_liber
 * @Date 2018/11/15
 * @Version 1.0.0
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Scheduled (cron = "0 0 3 * * ?")
    public void Schedule() {
        System.out.println("凌晨三点对你思念是一天又一天");
    }
}
