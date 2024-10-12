package art.yang.alarm.service.impl;

import art.yang.alarm.entity.AlarmLog;
import art.yang.alarm.mapper.AlarmLogMapper;
import art.yang.alarm.service.AlarmLogService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author arTGOD
 * @Date 2024/10/12 16:11
 * @Description
 */
@Service
public class AlarmLogServiceImpl extends ServiceImpl<AlarmLogMapper, AlarmLog> implements AlarmLogService {
}
