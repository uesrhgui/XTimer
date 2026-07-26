package cn.bitoffer.xtimer.enums;

/**
 * 定时器状态枚举（打开 or 关闭）
 */
public enum TimerStatus {
    Unable(1), // 关闭
    Enable(2),; // 打开

    private TimerStatus(int status) {
        this.status = status;
    }
    private int status;

    public int getStatus() {
        return this.status;
    }

    public static TimerStatus getTimerStatus(int status){
        for (TimerStatus value:TimerStatus.values()) {
            if(value.status == status){
                return value;
            }
        }
        return null;
    }
}
