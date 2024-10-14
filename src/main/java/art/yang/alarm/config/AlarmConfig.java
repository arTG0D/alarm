package art.yang.alarm.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alarm")
public class AlarmConfig {
    private int alarmThreshold;
    private int alarmThresholdTime;
    private int alarmWindowTime;
    private String webhook;
    private int alarmLogRetentionDays;
    private int lbCheckRetentionDays;
}
