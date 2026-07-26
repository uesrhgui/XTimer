package cn.bitoffer.xtimer.model;

import cn.bitoffer.api.dto.xtimer.NotifyHTTPParam;
import cn.bitoffer.api.dto.xtimer.TimerDTO;
import cn.bitoffer.common.model.BaseModel;
import cn.bitoffer.xtimer.utils.JSONUtil;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 定时任类
 *
 * @author JunTroy
 * @email 1437743263@qq.com
 */
public class TimerModel extends BaseModel implements Serializable {

    /**
     * 定时任务ID
     */
    private Long timerId;

    /**
     * APP名称（所属业务）
     */
    private String app;

    /**
     * 定时任务-名称
     */
    private String name;

    /**
     * 定时任务-状态 0-新建 1-激活 2-未激活
     */
    private int status;

    /**
     * 定时任务-定时配置
     */
    private String cron;

    /**
     * Name 定时任务-回调上下文
     */
    private String notifyHTTPParam;

    /**
     * 包装类转对象
     *
     * @param timerDTO
     * @return
     */
    public static TimerModel voToObj(TimerDTO timerDTO) {
        if (timerDTO == null) {
            return null;
        }
        TimerModel timerModel = new TimerModel();
        timerModel.setApp(timerDTO.getApp());
        timerModel.setTimerId(timerDTO.getTimerId());
        timerModel.setName(timerDTO.getName());
        timerModel.setStatus(timerDTO.getStatus());
        timerModel.setCron(timerDTO.getCron());
        timerModel.setNotifyHTTPParam(JSONUtil.toJsonString(timerDTO.getNotifyHTTPParam()));
        return timerModel;
    }

    /**
     * 对象转包装类
     *
     * @param timerModel
     * @return
     */
    public static TimerDTO objToVo(TimerModel timerModel) {
        if (timerModel == null) {
            return null;
        }
        TimerDTO timerDTO = new TimerDTO();
        timerDTO.setApp(timerModel.getApp());
        timerDTO.setTimerId(timerModel.getTimerId());
        timerDTO.setName(timerModel.getName());
        timerDTO.setStatus(timerModel.getStatus());
        timerDTO.setCron(timerModel.getCron());

        NotifyHTTPParam httpParam = JSONUtil.parseObject(timerModel.getNotifyHTTPParam(),NotifyHTTPParam.class);
        timerDTO.setNotifyHTTPParam(httpParam);

        BeanUtils.copyProperties(timerModel, timerDTO);
        return timerDTO;
    }

    public Long getTimerId() {
        return timerId;
    }

    public void setTimerId(Long timerId) {
        this.timerId = timerId;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getNotifyHTTPParam() {
        return notifyHTTPParam;
    }

    public void setNotifyHTTPParam(String notifyHTTPParam) {
        this.notifyHTTPParam = notifyHTTPParam;
    }
}
