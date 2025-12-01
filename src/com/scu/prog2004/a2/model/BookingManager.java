package com.scu.prog2004.a2.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // 新增：兼容低版本Java的Stream收集器

/**
 * 单例模式的预约管理器（全局唯一）
 * 职责：统一管理预约的增删改查、序列化持久化、排序，符合单一职责原则
 * 线程安全：懒汉式单例+volatile关键字，避免多线程下实例创建问题
 */
public class BookingManager implements Serializable { // 实现Serializable，支持整体序列化
    private static final long serialVersionUID = 1L; // 序列化版本号（确保反序列化兼容性）
    // 单例实例：volatile保证可见性，避免指令重排序导致的实例空指针
    private static volatile BookingManager instance;
    // 预约列表：final确保引用不可变，内部通过方法控制修改，符合封装原则
    private final List<Booking> bookingList;

    /**
     * 私有构造器：防止外部实例化，确保单例唯一性
     * 初始化预约列表为ArrayList，兼顾查询与修改效率
     */
    private BookingManager() {
        this.bookingList = new ArrayList<>();
    }

    /**
     * 获取单例实例（双重检查锁，线程安全且高效）
     * @return 全局唯一的BookingManager实例
     */
    public static BookingManager getInstance() {
        if (instance == null) { // 第一次检查：避免频繁加锁
            synchronized (BookingManager.class) { // 加锁：保证线程安全
                if (instance == null) { // 第二次检查：避免多线程同时进入后重复创建
                    instance = new BookingManager();
                }
            }
        }
        return instance;
    }

    /**
     * 新增预约（带边界校验，避免无效数据）
     * @param booking 待添加的预约对象（非空、未取消）
     */
    public void addBooking(Booking booking) {
        // 防御性校验：过滤空对象和已取消的预约
        if (booking == null) {
            LoggerUtil.error("❌ 新增预约失败：预约对象不能为空");
            return;
        }
        if (booking.isCancelled()) {
            LoggerUtil.error("❌ 新增预约失败：已取消的预约无法添加（预约ID：" + booking.getBookingId() + "）");
            return;
        }
        // 避免重复添加：按预约ID去重
        boolean isDuplicate = bookingList.stream()
                .anyMatch(b -> b.getBookingId().equals(booking.getBookingId()));
        if (isDuplicate) {
            LoggerUtil.error("❌ 新增预约失败：该预约已存在（预约ID：" + booking.getBookingId() + "）");
            return;
        }

        bookingList.add(booking);
        LoggerUtil.info("✅ 预约成功！预约ID：" + booking.getBookingId() + " | 游客：" + booking.getVisitor().getName());
    }

    /**
     * 按游客ID查询有效预约（优化：直接传ID，避免创建临时Visitor对象）
     * @param visitorId 游客唯一ID（非空）
     * @return 该游客的所有有效预约（返回不可修改列表，避免外部篡改）
     */
    public List<Booking> getBookingsByVisitorId(String visitorId) {
        // 校验游客ID非空
        if (visitorId == null || visitorId.isBlank()) {
            LoggerUtil.error("❌ 查询预约失败：游客ID不能为空");
            return Collections.emptyList(); // 返回空列表，避免NullPointerException
        }

        // 筛选未取消且游客ID匹配的预约
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookingList) {
            if (!booking.isCancelled() && booking.getVisitor().getVisitorId().equals(visitorId)) {
                result.add(booking);
            }
        }
        return Collections.unmodifiableList(result); // 返回不可修改列表，保护内部数据
    }

    /**
     * 按预约ID取消预约（支持链式调用，返回Optional便于后续处理）
     * @param bookingId 预约唯一ID（非空）
     * @return 被取消的预约（Optional.empty()表示未找到或已取消）
     */
    public Optional<Booking> cancelBookingById(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            LoggerUtil.error("❌ 取消预约失败：预约ID不能为空");
            return Optional.empty();
        }

        for (Booking booking : bookingList) {
            if (booking.getBookingId().equals(bookingId)) {
                if (booking.isCancelled()) {
                    LoggerUtil.error("❌ 取消预约失败：该预约已取消（预约ID：" + bookingId + "）");
                    return Optional.empty();
                }
                booking.cancelBooking();
                LoggerUtil.info("✅ 预约取消成功（预约ID：" + bookingId + "）");
                return Optional.of(booking);
            }
        }

        LoggerUtil.error("❌ 取消预约失败：未找到该预约（预约ID：" + bookingId + "）");
        return Optional.empty();
    }

    /**
     * 打印所有有效预约（按预约时间升序排列，提升用户体验）
     */
    public void printAllBookings() {
        LoggerUtil.info("\n===== 全局有效预约列表 =====");
        // 修复：用collect(Collectors.toList())替换Stream.toList()，兼容Java 8+
        List<Booking> validBookings = bookingList.stream()
                .filter(booking -> !booking.isCancelled())
                .sorted(Comparator.comparing(Booking::getBookingTime))
                .collect(Collectors.toList());

        if (validBookings.isEmpty()) {
            LoggerUtil.info("📭 暂无有效预约");
            LoggerUtil.info("=========================\n");
            return;
        }

        int index = 1;
        for (Booking booking : validBookings) {
            LoggerUtil.info(String.format("%d. %s", index++, booking));
        }
        LoggerUtil.info("=========================\n");
    }

    /**
     * 序列化：保存所有预约到文件（支持断点续存，覆盖原有文件）
     * @param filePath 保存路径（建议以.dat为后缀）
     */
    public void saveBookingsToFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            LoggerUtil.error("❌ 保存预约失败：文件路径不能为空");
            return;
        }

        // try-with-resources自动关闭流，避免资源泄漏
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(bookingList);
            LoggerUtil.info("✅ 预约数据已保存到：" + new File(filePath).getAbsolutePath());
            LoggerUtil.info("📊 保存数量：" + bookingList.size() + "条（含已取消预约）");
        } catch (FileNotFoundException e) {
            LoggerUtil.error("❌ 保存预约失败：文件路径不存在（" + filePath + "）");
        } catch (IOException e) {
            LoggerUtil.error("❌ 保存预约失败：IO错误（" + e.getMessage() + "）");
        }
    }

    /**
     * 反序列化：从文件加载预约（清空现有列表，避免数据冲突）
     * @param filePath 加载路径（需与保存路径一致）
     */
    @SuppressWarnings("unchecked") // 显式抑制未检查转换警告（已知文件存储List<Booking>）
    public void loadBookingsFromFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            LoggerUtil.error("❌ 加载预约失败：文件路径不能为空");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            LoggerUtil.error("❌ 加载预约失败：文件不存在（" + filePath + "）");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // 清空现有列表，避免加载后数据重复
            bookingList.clear();
            List<Booking> loadedBookings = (List<Booking>) ois.readObject();
            bookingList.addAll(loadedBookings);
            // 统计有效预约数量
            long validCount = bookingList.stream().filter(b -> !b.isCancelled()).count();
            LoggerUtil.info("✅ 从文件加载预约成功（" + filePath + "）");
            LoggerUtil.info("📊 加载总数：" + loadedBookings.size() + "条 | 有效预约：" + validCount + "条");
        } catch (ClassNotFoundException e) {
            LoggerUtil.error("❌ 加载预约失败：Booking类未找到（可能是类结构修改）");
        } catch (IOException e) {
            LoggerUtil.error("❌ 加载预约失败：IO错误（" + e.getMessage() + "）");
        }
    }

    /**
     * 策略模式：按会员等级降序排序预约（铂金→黄金→标准）
     * 排序后不影响原列表顺序，返回新列表（保护性拷贝）
     * @return 排序后的预约列表（不可修改）
     */
    public List<Booking> sortBookingsByMembership() {
        if (bookingList.isEmpty()) {
            LoggerUtil.error("❌ 排序预约失败：预约列表为空");
            return Collections.emptyList();
        }

        // 保护性拷贝：避免修改原列表，符合封装原则
        List<Booking> sortedList = new ArrayList<>(bookingList);
        // 优化：用List.sort替换Collections.sort，代码更简洁
        sortedList.sort(Comparator.comparing(
                booking -> booking.getVisitor().getMembershipType(),
                Comparator.reverseOrder() // 会员等级降序
        ));

        LoggerUtil.info("✅ 预约已按会员等级排序（规则：铂金会员→黄金会员→标准会员）");
        return Collections.unmodifiableList(sortedList);
    }

    /**
     * 获取有效预约总数（对外提供统计接口，隐藏内部列表）
     * @return 未取消的预约数量
     */
    public long getValidBookingCount() {
        return bookingList.stream().filter(b -> !b.isCancelled()).count();
    }
}