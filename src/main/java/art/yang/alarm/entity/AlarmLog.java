package art.yang.alarm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author arTGOD
 * @Date 2024/10/12 16:06
 * @Description
 */
@Data
@TableName("tb_alarm_log")
public class AlarmLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String pool;
    private String rsIp;
    private String alarmMessage;
    private String status;
    private LocalDateTime timestamp;
}
