package art.yang.alarm.schedule;

import art.yang.alarm.AlarmApplication;
import art.yang.alarm.config.AlarmConfig;
import art.yang.alarm.entity.AlarmLog;
import art.yang.alarm.entity.LBHealthCheck;
import art.yang.alarm.mapper.AlarmLogMapper;
import art.yang.alarm.mapper.LBHealthCheckMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @Author arTGOD
 * @Date 2024/10/14 15:29
 * @Description
 */
@Service
@Slf4j
public class DBCleanupSchedule {

    @Resource
    private AlarmLogMapper alarmLogMapper;
    @Resource
    private LBHealthCheckMapper lbHealthCheckMapper;

    private final int alarmLogRetentionDays;
    private final int lbCheckRetentionDays;
    private  DBCleanupSchedule(AlarmConfig alarmConfig){
        this.alarmLogRetentionDays = alarmConfig.getAlarmLogRetentionDays();
        this.lbCheckRetentionDays = alarmConfig.getLbCheckRetentionDays();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanOldData() {
        LocalDateTime alarmLogDataTime = LocalDateTime.now().minus(alarmLogRetentionDays, ChronoUnit.DAYS);
        LocalDateTime lbCheckDataTime = LocalDateTime.now().minus(lbCheckRetentionDays, ChronoUnit.DAYS);

        LambdaQueryWrapper<AlarmLog> alarmLogWrapper = new LambdaQueryWrapper<>();
        alarmLogWrapper.lt(AlarmLog::getTimestamp, alarmLogDataTime);
        int deletedAlarmLogCount = alarmLogMapper.delete(alarmLogWrapper);

        log.info("Deleted {} records from tb_alarm_log older than {}", deletedAlarmLogCount, alarmLogDataTime);

        LambdaQueryWrapper<LBHealthCheck> lbCheckWrapper = new LambdaQueryWrapper<>();
        lbCheckWrapper.lt(LBHealthCheck::getTimestamp, lbCheckDataTime);
        int deletedLBCheckCount = lbHealthCheckMapper.delete(lbCheckWrapper);

        log.info("Deleted {} records from tb_lb_health_check older than {}", deletedLBCheckCount, lbCheckDataTime);

    }
}
