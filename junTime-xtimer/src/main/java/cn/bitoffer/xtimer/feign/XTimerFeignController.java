package cn.bitoffer.xtimer.feign;

import cn.bitoffer.api.dto.xtimer.TimerDTO;
import cn.bitoffer.api.feign.XTimerClient;
import cn.bitoffer.xtimer.model.Example;
import cn.bitoffer.xtimer.service.XTimerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微服接口模版
 * DemoClient 为对外提供的微服务接口
 * ProviderFeignController 为该接口的实现
 **/
@RestController
@Slf4j
public class XTimerFeignController implements XTimerClient {

    @Autowired
    private XTimerService xTimerService;

    @Override
    public Long CreateTimer(TimerDTO timerDTO) {
        return xTimerService.CreateTimer(timerDTO);
    }

    @Override
    public void EnableTimer(String app, Long timerId, MultiValueMap<String, String> headers) {
        xTimerService.EnableTimer(app,timerId);
    }
}
