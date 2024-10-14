package art.yang.alarm.service.impl;

import art.yang.alarm.entity.SendResult;
import art.yang.alarm.message.Message;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @Author arTGOD
 * @Date 2024/10/14 10:45
 * @Description
 */
@Service
@Slf4j
public class WxWorkBotService {

    @Resource
    @Qualifier("wxWorkAlarmExecutor")
    private Executor wxWorkAlarmExecutor;

    @Async("wxWorkAlarmExecutor")
    public void send(Message message, String webhook){
        wxWorkAlarmExecutor.execute(() -> {
            HttpResponse response = HttpRequest.post(webhook)
                    .header("Content-Type", "application/json")
                    .body(message.toJsonString())
                    .execute();
            if (response.getStatus() == HttpStatus.HTTP_OK) {
                log.info("发送告警至企微告警机器人成功,Msg---->{}",message.toJsonString());
            }else {
                log.error("发送告警至企微告警机器人失败,errorMsg---->{}",response.body());
            }
        });
    }
}
