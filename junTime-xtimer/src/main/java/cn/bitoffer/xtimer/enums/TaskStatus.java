package cn.bitoffer.xtimer.enums;

/**
 * 任务状态
 */
public enum TaskStatus {
    NotRun(0), // 未运行
    Running(1), // 运行中
    Succeed(2), // 成功
    Failed(3); // 失败

    private TaskStatus(int status) {
        this.status = status;
    }
    private int status;

    public int getStatus() {
        return this.status;
    }
}
