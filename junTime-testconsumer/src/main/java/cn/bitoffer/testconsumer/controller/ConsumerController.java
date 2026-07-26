package cn.bitoffer.testconsumer.controller;

import cn.bitoffer.api.dto.xtimer.TimerDTO;
import cn.bitoffer.api.feign.XTimerClient;
import cn.bitoffer.common.model.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/xtimer")
@Slf4j
public class ConsumerController {

    @Resource
    private XTimerClient xTimerClient;

    @PostMapping("/createTimer")
    public ResponseEntity<Long> createTimer(@RequestBody TimerDTO timerDTO) {
        Long timer = xTimerClient.CreateTimer(timerDTO);
        return ResponseEntity.ok(timer);
    }

    @GetMapping("/enableTimer")
    public ResponseEntity<Void> enableTimer(
            @RequestParam(value = "app") String app,
            @RequestParam(value = "timerId") Long timerId,
            @RequestHeader MultiValueMap<String, String> headers
    ) {
        xTimerClient.EnableTimer(app, timerId, headers);
        return ResponseEntity.ok();
    }
}
