package com.scu.prog2004.a2.model;

/**
 * 游乐设施接口：定义游乐设施的核心行为契约
 * 设计思路：使用接口而非抽象类，因为行为是"必须实现"的契约，无继承限制
 * 所有方法均为抽象方法，强制Ride类实现完整功能
 */
public interface RideInterface {
    /**
     * 添加游客到等待队列
     * @param visitor 要添加的游客（非空）
     */
    void addVisitorToQueue(Visitor visitor);

    /**
     * 从等待队列移除首个游客（FIFO）
     */
    void removeVisitorFromQueue();

    /**
     * 打印等待队列中所有游客的详细信息
     */
    void printQueue();

    /**
     * 将游客添加到游乐历史记录
     * @param visitor 已完成游乐的游客（非空）
     */
    void addVisitorToHistory(Visitor visitor);

    /**
     * 检查游客是否在游乐历史中
     * @param visitor 要检查的游客
     * @return true=存在，false=不存在
     */
    boolean checkVisitorFromHistory(Visitor visitor);

    /**
     * 获取游乐历史中的游客总数
     * @return 历史游客数（非负）
     */
    int numberOfVisitors();

    /**
     * 打印游乐历史中所有游客的详细信息（必须用Iterator）
     */
    void printRideHistory();

    /**
     * 运行一次游乐周期（核心业务逻辑）
     */
    void runOneCycle();
}