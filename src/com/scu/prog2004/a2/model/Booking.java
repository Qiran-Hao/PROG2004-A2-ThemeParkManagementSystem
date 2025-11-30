package com.scu.prog2004.a2.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 预约类（封装游乐设施预约信息）
 * 实现Serializable支持序列化持久化
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本号
    private final String bookingId;       // 预约ID（自动生成）
    private final Visitor visitor;        // 预约游客
    private final Ride ride;              // 预约设施
    private LocalDateTime bookingTime;    // 预约时间
    private boolean isCancelled;          // 是否取消

    // 构造器：自动生成预约ID
    public Booking(Visitor visitor, Ride ride, LocalDateTime bookingTime) {
        this.bookingId = String.format("BOOK-%tY%<tm%<td-%<tH%<tM-%d",
                System.currentTimeMillis(), (int) (Math.random() * 1000));
        this.visitor = visitor;
        this.ride = ride;
        this.bookingTime = bookingTime;
        this.isCancelled = false;
    }

    // Getter/Setter（含参数校验）
    public String getBookingId() { return bookingId; }
    public Visitor getVisitor() { return visitor; }
    public Ride getRide() { return ride; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public boolean isCancelled() { return isCancelled; }

    public void setBookingTime(LocalDateTime bookingTime) {
        if (bookingTime.isAfter(LocalDateTime.now())) {
            this.bookingTime = bookingTime;
        } else {
            LoggerUtil.error("警告：预约时间必须晚于当前时间！");
        }
    }

    public void cancelBooking() {
        this.isCancelled = true;
        LoggerUtil.info("✅ 预约[" + bookingId + "]已取消");
    }

    // 格式化输出预约信息
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("预约ID：%s | 游客：%s | 设施：%s | 时间：%s | 状态：%s",
                bookingId, visitor.getName(), ride.getName(),
                bookingTime.format(formatter), isCancelled ? "已取消" : "有效");
    }
}