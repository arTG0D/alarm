package art.yang.alarm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author arTGOD
 * @Date 2024/10/12 16:04
 * @Description
 */
@Data
@TableName("tb_lb_health_check")
public class LBHealthCheck {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String pool;
    private String rsIp;
    private String status;
    private LocalDateTime timestamp;
}
