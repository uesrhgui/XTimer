package cn.bitoffer.xtimer.service.impl;

import cn.bitoffer.api.dto.xtimer.TimerDTO;
import cn.bitoffer.common.redis.ReentrantDistributeLock;
import cn.bitoffer.xtimer.enums.TimerStatus;
import cn.bitoffer.xtimer.exception.BusinessException;
import cn.bitoffer.xtimer.exception.ErrorCode;
import cn.bitoffer.xtimer.manager.MigratorManager;
import cn.bitoffer.xtimer.mapper.TimerMapper;
import cn.bitoffer.xtimer.model.TimerModel;
import cn.bitoffer.xtimer.service.XTimerService;
import cn.bitoffer.xtimer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author JunTroy
 * @email 1437743263@qq.com
 */
@Service
@Slf4j
public class XTimerServiceImpl implements XTimerService {

    @Resource
    private TimerMapper timerMapper;

    @Resource
    private ReentrantDistributeLock reentrantDistributeLock;

    @Resource
    private MigratorManager migratorManager;

    private static final int  defaultGapSeconds= 3;

    @Override
    public Long CreateTimer(TimerDTO timerDTO) {
        // 1. 验证定时任务表达式是否合法
        boolean validExpression = CronExpression.isValidExpression(timerDTO.getCron());
        if (!validExpression) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "定时任务表达式不合法");
        }

        // 2. 创建定时任务
        TimerModel timerModel = TimerModel.voToObj(timerDTO);
        if (timerModel == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "定时任务参数错误");
        }
        // 3. 保存定时任务
        timerMapper.save(timerModel);
        return timerModel.getTimerId();
    }

    @Override
    public void DeleteTimer(String app, Long timerId) {
        // 1. 获取线程Token
        String lockToken = TimerUtils.GetTokenStr();
        // 2. 加锁（首先查询锁是否属于自己 --> setnx尝试加锁）
        boolean lockOk = reentrantDistributeLock.lock(
                TimerUtils.GetCreateLockKey(app), // 当前业务锁的key
                lockToken,
                defaultGapSeconds
        );
        if (!lockOk){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建/删除操作过于频繁，请稍后再试！");
        }
        timerMapper.deleteById(timerId);
    }

    @Override
    public void Update(TimerDTO timerDTO) {
        TimerModel timerModel = TimerModel.voToObj(timerDTO);
        if (timerModel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "定时任务参数错误");
        }
        timerMapper.update(timerModel);
    }

    @Override
    public TimerDTO GetTimer(String app, long id) {
        TimerModel timerModel = timerMapper.getTimerById(id);
        if (timerModel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "定时任务不存在");
        }
        return TimerModel.objToVo(timerModel);
    }

    @Override
    public void EnableTimer(String app, long id) {
        String lockToken = TimerUtils.GetTokenStr();
        boolean lockOk = reentrantDistributeLock.lock(
                TimerUtils.GetEnableLockKey(app), // 当前业务锁的key
                lockToken,
                defaultGapSeconds
        );
        if (!lockOk){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"打开/关闭操作过于频繁，请稍后再试！");
        }
        doEnableTimer(id);
    }

    /**
     * 激活定时任务
     * @param id
     */
    @Transactional
    public void doEnableTimer(long id) {
        TimerModel timerModel = timerMapper.getTimerById(id);
        if (timerModel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "定时任务不存在");
        }
        if (timerModel.getStatus() == TimerStatus.Enable.getStatus()){
            log.warn("定时任务已激活，任务ID：{}", timerModel.getTimerId());
        }
        timerModel.setStatus(TimerStatus.Enable.getStatus());
        timerMapper.update(timerModel);

        // 迁移定时任务
        migratorManager.migrateTimer(timerModel);
    }

    @Override
    public void UnEnableTimer(String app, long id) {
        String lockToken = TimerUtils.GetTokenStr();
        boolean lockOk = reentrantDistributeLock.lock(
                TimerUtils.GetEnableLockKey(app), // 当前业务锁的key
                lockToken,
                defaultGapSeconds
        );
        if (!lockOk){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"激活/关闭操作过于频繁，请稍后再试！");
        }
        doUnEnableTimer(id);
    }

    @Transactional
    public void doUnEnableTimer(long id) {
        TimerModel timerModel = timerMapper.getTimerById(id);
        if (timerModel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "定时任务不存在");
        }
        if (timerModel.getStatus() == TimerStatus.Unable.getStatus()){
            log.warn("定时任务已关闭，任务ID：{}", timerModel.getTimerId());
        }
        timerModel.setStatus(TimerStatus.Unable.getStatus());
        timerMapper.update(timerModel);
    }

    @Override
    public List<TimerDTO> GetAppTimers(String app) {
        // TODO : 获取指定app下的所有定时任务
        return null;
    }
}
