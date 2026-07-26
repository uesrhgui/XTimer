package cn.bitoffer.xtimer.manager;

import cn.bitoffer.xtimer.model.TimerModel;

/**
 * 数据迁移
 * @author JunTroy
 * &#064;email  1437743263@qq.com
 */
public interface MigratorManager {

    /**
     * 数据迁移
     * @param timerModel
     */
    public void migrateTimer(TimerModel timerModel);
}
