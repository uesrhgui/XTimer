package cn.bitoffer.xtimer.service;

import cn.bitoffer.api.dto.xtimer.TimerDTO;

import java.util.List;

/**
 * @author JunTroy
 * @email 1437743263@qq.com
 */
public interface XTimerService {

    /**
     * 创建定时任务
     * @param timerDTO 定时任务参数
     * @return 定时任务ID
     */
    Long CreateTimer(TimerDTO timerDTO);

    /**
     * 删除定时任务
     * @param app 业务名称
     * @param timerId 定时任务ID
     * @return 删除结果
     */
    void DeleteTimer(String app ,Long timerId);

    /**
     * 修改定时任务
     * @param timerDTO 定时任务参数
     */
    void Update(TimerDTO timerDTO);

    /**
     * 获取定时任务
     * @param app 业务名称
     * @param id 定时任务ID
     * @return 定时任务
     */
    TimerDTO GetTimer(String app, long id);

    /**
     * 启用定时任务
     * @param app 业务名称
     * @param id 定时任务ID
     */
    void EnableTimer(String app, long id);

    /**
     * 关闭定时任务
     * @param app 业务名称
     * @param id 定时任务ID
     */
    void UnEnableTimer(String app, long id);

    /**
     * 获取业务下的所有定时任务
     * @param app 业务名称
     * @return 定时任务列表
     */
    List<TimerDTO> GetAppTimers(String app);
}
