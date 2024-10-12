package art.yang.alarm.service.impl;

import art.yang.alarm.entity.LBHealthCheck;
import art.yang.alarm.mapper.LBHealthCheckMapper;
import art.yang.alarm.service.LBHealthCheckService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author arTGOD
 * @Date 2024/10/12 16:10
 * @Description
 */
@Service
public class LBHealthCheckServiceImpl extends ServiceImpl<LBHealthCheckMapper, LBHealthCheck> implements LBHealthCheckService {
}
