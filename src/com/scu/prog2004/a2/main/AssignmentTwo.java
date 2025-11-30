package com.scu.prog2004.a2.main;

import com.scu.prog2004.a2.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * 主类：整合「基础功能演示」与「交互式预约系统」
 * 职责分离（演示/交互独立）、输入校验、日志统一、边界处理
 * 覆盖作业Part1-Part7所有要求，同时扩展交互式体验
 */
public class AssignmentTwo {
    // 常量定义：避免魔法值，提升可维护性
    private static final String EXPORT_FILE_PATH = "rideHistory_Demo.csv";
    private static final String BOOKING_FILE_PATH = "bookings.dat";
    private static final RideManager RIDE_MANAGER = RideManager.getInstance();
    private static final BookingManager BOOKING_MANAGER = BookingManager.getInstance();
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    // 设施信息常量：统一管理，添加设施ID
    private static final String ROLLER_COASTER_ID = "R001";
    private static final String PIRATE_SHIP_ID = "R002";
    private static final String CAROUSEL_ID = "R003";
    private static final String ROLLER_COASTER_NAME = "超级过山车";
    private static final String PIRATE_SHIP_NAME = "海盗船";
    private static final String CAROUSEL_NAME = "旋转木马";

    public static void main(String[] args) {
        LoggerUtil.info("==================================================");
        LoggerUtil.info("🎢 PROG2004 A2 主题公园管理系统");
        LoggerUtil.info("==================================================\n");

        // 启动时加载历史预约数据（符合ULO4：IO机制应用）
        BOOKING_MANAGER.loadBookingsFromFile(BOOKING_FILE_PATH);
        LoggerUtil.info("📌 系统启动完成 | 当前有效预约：" + BOOKING_MANAGER.getValidBookingCount() + "条");

        // 主菜单循环：直到用户选择退出
        while (true) {
            printMainMenu();
            int choice = getSafeIntInput("请输入你的选择（1-3）：", 1, 3);
            switch (choice) {
                case 1:
                    runBaseDemo(); // 作业Part3-Part7基础功能演示
                    break;
                case 2:
                    startInteractiveMode(); // 增强版交互式系统（包含预约+设施运营）
                    break;
                case 3:
                    exitSystem(); // 退出系统（保存数据+资源释放）
                    return;
                default:
                    LoggerUtil.error("❌ 无效选择，请输入1-3之间的整数！");
            }
        }
    }

    /**
     * 打印主菜单（统一格式，提升用户体验）
     */
    private static void printMainMenu() {
        LoggerUtil.info("\n===== 主菜单 =====");
        LoggerUtil.info("1. 运行作业基础功能演示（Part3-Part7）");
        LoggerUtil.info("2. 使用增强版交互式系统（预约+设施运营）");
        LoggerUtil.info("3. 退出系统（自动保存预约数据）");
        LoggerUtil.info("===================");
    }

    /**
     * 运行作业基础功能演示（严格按Part3-Part7要求实现）
     * 每个Part独立调用，便于验证与调试
     */
    private static void runBaseDemo() {
        LoggerUtil.info("\n===== 开始基础功能演示（Part3-Part7） =====");
        AssignmentTwo demo = new AssignmentTwo();
        demo.partThree();    // Part3：等待队列
        demo.partFourA();    // Part4A：游乐历史
        demo.partFourB();    // Part4B：历史排序
        demo.partFive();     // Part5：运行周期
        demo.partSix();      // Part6：导出文件
        demo.partSeven();    // Part7：导入文件
        LoggerUtil.info("===== 基础功能演示结束 =====\n");
    }

    /**
     * 交互式系统（包含预约管理+设施运营）
     */
    private static void startInteractiveMode() {
        LoggerUtil.info("\n===== 进入增强版交互式系统 =====");
        while (true) {
            LoggerUtil.info("\n===== 交互式系统主菜单 =====");
            LoggerUtil.info("1. 预约管理（新增/查询/取消预约）");
            LoggerUtil.info("2. 设施运营管理（队列/历史/运行周期）");
            LoggerUtil.info("3. 返回主菜单");

            int choice = getSafeIntInput("请选择功能模块（1-3）：", 1, 3);
            switch (choice) {
                case 1:
                    runInteractiveBookingSystem(); // 预约系统
                    break;
                case 2:
                    manageRideOperations(); // 设施运营管理
                    break;
                case 3:
                    LoggerUtil.info("📌 返回主菜单");
                    return;
                default:
                    LoggerUtil.error("❌ 无效选择！");
            }
        }
    }

    /**
     * 设施运营管理子菜单（对应Part3-Part7的交互操作）
     */
    private static void manageRideOperations() {
        LoggerUtil.info("\n===== 设施运营管理 =====");

        // 选择要操作的设施（含管理员信息）
        LoggerUtil.info("\n===== 选择要操作的设施 =====");
        Ride rollerCoaster = RIDE_MANAGER.getDefaultRide(ROLLER_COASTER_ID);
        Employee op1 = rollerCoaster.getOperator();
        LoggerUtil.info(String.format("1. %s（%s | 操作员：%s | 年龄：%d岁）",
                ROLLER_COASTER_NAME, rollerCoaster.getRideType(), op1.getName(), op1.getAge()));

        Ride pirateShip = RIDE_MANAGER.getDefaultRide(PIRATE_SHIP_ID);
        Employee op2 = pirateShip.getOperator();
        LoggerUtil.info(String.format("2. %s（%s | 操作员：%s | 年龄：%d岁）",
                PIRATE_SHIP_NAME, pirateShip.getRideType(), op2.getName(), op2.getAge()));

        Ride carousel = RIDE_MANAGER.getDefaultRide(CAROUSEL_ID);
        Employee op3 = carousel.getOperator();
        LoggerUtil.info(String.format("3. %s（%s | 操作员：%s | 年龄：%d岁）",
                CAROUSEL_NAME, carousel.getRideType(), op3.getName(), op3.getAge()));

        int rideChoice = getSafeIntInput("请选择设施（1-3）：", 1, 3);
        Ride selectedRide = null;
        if (rideChoice == 1) {
            selectedRide = rollerCoaster;
        } else if (rideChoice == 2) {
            selectedRide = pirateShip;
        } else if (rideChoice == 3) {
            selectedRide = carousel;
        }

        if (selectedRide == null) {
            LoggerUtil.error("❌ 设施选择无效！");
            return;
        }

        // 设施运营子菜单
        while (true) {
            LoggerUtil.info("\n===== " + selectedRide.getName() + "运营管理 =====");
            LoggerUtil.info("1. 添加游客到等待队列（Part3）");
            LoggerUtil.info("2. 移除队列首位游客（Part3）");
            LoggerUtil.info("3. 查看等待队列（Part3）");
            LoggerUtil.info("4. 运行一次游乐周期（Part5）");
            LoggerUtil.info("5. 查看游乐历史（Part4）");
            LoggerUtil.info("6. 排序游乐历史（Part4B）");
            LoggerUtil.info("7. 导出历史到CSV（Part6）");
            LoggerUtil.info("8. 从CSV导入历史（Part7）");
            LoggerUtil.info("9. 返回上一级");

            int opChoice = getSafeIntInput("请选择操作（1-9）：", 1, 9);
            switch (opChoice) {
                case 1:
                    addVisitorToRideQueue(selectedRide);
                    break;
                case 2:
                    selectedRide.removeVisitorFromQueue();
                    break;
                case 3:
                    selectedRide.printQueue();
                    break;
                case 4:
                    selectedRide.runOneCycle();
                    break;
                case 5:
                    selectedRide.printRideHistory();
                    break;
                case 6:
                    selectedRide.sortRideHistory();
                    break;
                case 7:
                    String exportPath = "rideHistory_" + selectedRide.getRideId() + ".csv";
                    selectedRide.exportRideHistory(exportPath);
                    break;
                case 8:
                    String importPath = "rideHistory_" + selectedRide.getRideId() + ".csv";
                    selectedRide.importRideHistory(importPath);
                    break;
                case 9:
                    return;
                default:
                    LoggerUtil.error("❌ 无效操作！");
            }
        }
    }

    /**
     * 添加游客到设施队列（交互版）
     */
    private static void addVisitorToRideQueue(Ride ride) {
        LoggerUtil.info("\n===== 添加游客到[" + ride.getName() + "]队列 =====");
        String name = getSafeStringInput("请输入游客姓名：");
        if (name.isBlank()) {
            LoggerUtil.error("❌ 姓名不能为空！");
            return;
        }

        int age = getSafeIntInput("请输入游客年龄：", 0, 120);
        if (age == -1) {
            LoggerUtil.error("❌ 年龄输入无效！");
            return;
        }

        // 创建游客对象
        String visitorId = String.format("VIS-%tY%<tm%<td-%d", System.currentTimeMillis(), (int)(Math.random()*1000));
        String personId = "PER-" + System.currentTimeMillis();

        Visitor visitor = new Visitor(personId, name, age, visitorId,
                Visitor.MembershipType.STANDARD, false);

        // 调用Ride的核心方法（和自动模式共用）
        ride.addVisitorToQueue(visitor);
    }

    /**
     * 交互式预约系统
     */
    private static void runInteractiveBookingSystem() {
        LoggerUtil.info("\n===== 预约管理系统 =====");
        while (true) {
            printBookingSubMenu();
            int choice = getSafeIntInput("请输入你的选择（1-5）：", 1, 5);
            switch (choice) {
                case 1:
                    createBooking(); // 预约设施
                    break;
                case 2:
                    queryUserBookings(); // 查询我的预约
                    break;
                case 3:
                    cancelUserBooking(); // 取消预约
                    break;
                case 4:
                    BOOKING_MANAGER.printAllBookings(); // 查看所有预约（管理员视角）
                    break;
                case 5:
                    LoggerUtil.info("📌 退出预约系统，返回上一级");
                    return;
                default:
                    LoggerUtil.error("❌ 无效选择，请输入1-5之间的整数！");
            }
        }
    }

    /**
     * 打印预约系统子菜单
     */
    private static void printBookingSubMenu() {
        LoggerUtil.info("\n===== 预约系统子菜单 =====");
        LoggerUtil.info("1. 预约游乐设施（需实名登记）");
        LoggerUtil.info("2. 查询我的预约（需游客ID）");
        LoggerUtil.info("3. 取消预约（需预约ID）");
        LoggerUtil.info("4. 查看所有有效预约（管理员功能）");
        LoggerUtil.info("5. 返回上一级");
        LoggerUtil.info("========================");
    }

    /**
     * 预约游乐设施（带完整输入校验：姓名/年龄/时间/设施选择）
     */
    private static void createBooking() {
        LoggerUtil.info("\n===== 预约游乐设施 =====");

        // 1. 校验游客姓名（非空）
        String visitorName = getSafeStringInput("请输入你的姓名：");
        if (visitorName.isBlank()) {
            LoggerUtil.error("❌ 预约失败：姓名不能为空");
            return;
        }

        // 2. 校验游客年龄（1-120岁）
        int visitorAge = getSafeIntInput("请输入你的年龄：", 1, 120);
        if (visitorAge == -1) { // 输入无效
            LoggerUtil.error("❌ 预约失败：年龄必须为1-120之间的整数");
            return;
        }

        // 3. 创建游客对象（封装用户信息，符合OOP封装原则）
        Visitor visitor = new Visitor(
                "PERSON-" + System.currentTimeMillis(), // 全局唯一PersonID
                visitorName,
                visitorAge,
                "VIS-" + (int) (Math.random() * 10000), // 全局唯一VisitorID
                Visitor.MembershipType.STANDARD, // 默认标准会员
                false // 默认未购买保险
        );
        LoggerUtil.info("📌 游客信息登记完成 | 你的游客ID：" + visitor.getVisitorId() + "（请保存用于查询）");

        // 4. 选择游乐设施（带类型与年龄限制提示）
        LoggerUtil.info("\n===== 可选游乐设施 =====");
        LoggerUtil.info("1. " + ROLLER_COASTER_NAME + "（刺激类 | 要求16岁以上 | 单次4人）");
        LoggerUtil.info("2. " + PIRATE_SHIP_NAME + "（家庭类 | 无年龄限制 | 单次5人）");
        LoggerUtil.info("3. " + CAROUSEL_NAME + "（儿童类 | 要求3-12岁 | 单次3人）");

        int rideChoice = getSafeIntInput("请选择设施（1-3）：", 1, 3);
        if (rideChoice == -1) {
            LoggerUtil.error("❌ 预约失败：设施选择无效");
            return;
        }

        // 5. 获取选中设施并校验年龄限制（符合ULO2：多态应用，不同设施有不同规则）
        Ride selectedRide = null;
        if (rideChoice == 1) {
            selectedRide = RIDE_MANAGER.getDefaultRide(ROLLER_COASTER_ID);
        } else if (rideChoice == 2) {
            selectedRide = RIDE_MANAGER.getDefaultRide(PIRATE_SHIP_ID);
        } else if (rideChoice == 3) {
            selectedRide = RIDE_MANAGER.getDefaultRide(CAROUSEL_ID);
        }

        if (!selectedRide.checkVisitorEligibility(visitor)) {
            LoggerUtil.error("❌ 预约失败：不符合该设施的年龄要求（" + selectedRide.getRideType() + "）");
            return;
        }

        // 6. 选择预约时间（校验格式与有效性）
        LocalDateTime bookingTime = getSafeBookingTimeInput();
        if (bookingTime == null) {
            LoggerUtil.error("❌ 预约失败：时间输入无效");
            return;
        }

        // 7. 创建并提交预约
        Booking newBooking = new Booking(visitor, selectedRide, bookingTime);
        BOOKING_MANAGER.addBooking(newBooking);
        LoggerUtil.info("📌 预约流程完成 | 请牢记预约ID：" + newBooking.getBookingId() + "（用于取消/查询）");
    }

    /**
     * 查询用户预约（按游客ID查询）
     */
    private static void queryUserBookings() {
        LoggerUtil.info("\n===== 查询我的预约 =====");
        String visitorId = getSafeStringInput("请输入你的游客ID（如VIS-12345）：");
        if (visitorId.isBlank()) {
            LoggerUtil.error("❌ 查询失败：游客ID不能为空");
            return;
        }

        // 调用BookingManager优化后的方法
        List<Booking> userBookings = BOOKING_MANAGER.getBookingsByVisitorId(visitorId);
        if (userBookings.isEmpty()) {
            LoggerUtil.info("📭 未找到你的有效预约（游客ID：" + visitorId + "）");
            LoggerUtil.info("💡 提示：请确认游客ID正确，或检查预约是否已取消");
            return;
        }

        // 打印查询结果
        LoggerUtil.info("✅ 找到你的" + userBookings.size() + "条有效预约（游客ID：" + visitorId + "）");
        int index = 1;
        for (Booking booking : userBookings) {
            LoggerUtil.info(String.format("%d. %s", index++, booking));
        }
    }

    /**
     * 取消用户预约（带预约ID校验）
     */
    private static void cancelUserBooking() {
        LoggerUtil.info("\n===== 取消预约 =====");
        String bookingId = getSafeStringInput("请输入你的预约ID（如BOOK-20251201-1430-123）：");
        if (bookingId.isBlank()) {
            LoggerUtil.error("❌ 取消失败：预约ID不能为空");
            return;
        }

        // 调用BookingManager取消方法
        BOOKING_MANAGER.cancelBookingById(bookingId);
    }

    /**
     * 安全获取字符串输入（过滤空值与纯空格）
     */
    private static String getSafeStringInput(String prompt) {
        System.out.print(prompt);
        String input = SCANNER.nextLine().trim();
        while (input.isEmpty() && SCANNER.hasNextLine()) {
            input = SCANNER.nextLine().trim();
        }
        return input;
    }

    /**
     * 安全获取整数输入（指定范围，处理非数字输入）
     */
    private static int getSafeIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = SCANNER.nextInt();
                if (input >= min && input <= max) {
                    SCANNER.nextLine(); // 清除换行符残留
                    return input;
                } else {
                    LoggerUtil.error("❌ 输入超出范围，请输入" + min + "-" + max + "之间的整数！");
                }
            } catch (InputMismatchException e) {
                LoggerUtil.error("❌ 输入格式错误，请输入整数！");
                SCANNER.next(); // 清除非数字输入
            } finally {
                if (SCANNER.hasNextLine()) {
                    SCANNER.nextLine();
                }
            }
        }
    }

    /**
     * 安全获取预约时间（校验格式与有效性）
     */
    private static LocalDateTime getSafeBookingTimeInput() {
        while (true) {
            String timeStr = getSafeStringInput("请输入预约时间（格式：yyyy-MM-dd HH:mm，如2025-12-01 14:30）：");
            if (timeStr.isBlank()) {
                LoggerUtil.error("❌ 时间不能为空，请重新输入！");
                continue;
            }

            try {
                LocalDateTime inputTime = LocalDateTime.parse(timeStr, DATE_FORMATTER);
                LocalDateTime now = LocalDateTime.now();
                if (inputTime.isBefore(now)) {
                    LoggerUtil.error("❌ 预约时间不能早于当前时间（当前时间：" + now.format(DATE_FORMATTER) + "）");
                    continue;
                }
                if (inputTime.isBefore(now.plusMinutes(10))) {
                    LoggerUtil.error("❌ 预约需至少提前10分钟，请选择更晚的时间！");
                    continue;
                }
                return inputTime;
            } catch (DateTimeParseException e) {
                LoggerUtil.error("❌ 时间格式错误，请严格按「yyyy-MM-dd HH:mm」输入（如2025-12-01 14:30）！");
            }
        }
    }

    /**
     * 退出系统（保存数据+释放资源）
     */
    private static void exitSystem() {
        LoggerUtil.info("\n===== 退出系统 =====");
        // 保存预约数据
        BOOKING_MANAGER.saveBookingsToFile(BOOKING_FILE_PATH);
        // 关闭Scanner资源
        SCANNER.close();
        LoggerUtil.info("👋 系统已安全退出，感谢使用！");
    }

    // ====================== 作业Part3-Part7基础演示方法（保持不变） ======================

    /**
     * Part3：等待队列功能演示
     */
    public void partThree() {
        LoggerUtil.info("\n=== Part3：等待队列功能演示 ===");
        Employee coasterOperator = new Employee(
                "PERSON-EMP001",
                "张三",
                30,
                "EMP-2025-001",
                "过山车操作员"
        );
        Ride rollerCoaster = new Ride(
                ROLLER_COASTER_ID,
                ROLLER_COASTER_NAME,
                coasterOperator,
                4 // 单次最大4人
        );
        rollerCoaster.setRideType(RideType.THRILL);

        LoggerUtil.info("📌 开始添加5个游客到" + ROLLER_COASTER_NAME + "队列");
        for (int i = 1; i <= 5; i++) {
            Visitor visitor = new Visitor(
                    "PERSON-P3-" + i,
                    "游客" + i,
                    18 + i,
                    "VIS-P3-" + i,
                    i % 3 == 0 ? Visitor.MembershipType.PLATINUM : (i % 2 == 0 ? Visitor.MembershipType.GOLD : Visitor.MembershipType.STANDARD),
                    i % 2 == 0
            );
            rollerCoaster.addVisitorToQueue(visitor);
        }

        LoggerUtil.info("\n📌 从队列移除1个游客（FIFO顺序）");
        rollerCoaster.removeVisitorFromQueue();

        LoggerUtil.info("📌 " + ROLLER_COASTER_NAME + "队列详情（移除后）");
        rollerCoaster.printQueue();
    }

    /**
     * Part4A：游乐历史功能演示
     */
    public void partFourA() {
        LoggerUtil.info("\n=== Part4A：游乐历史功能演示 ===");
        Ride thunderstorm = new Ride(
                "RIDE002",
                "雷霆风暴",
                null,
                6
        );

        Visitor targetVisitor = null;
        LoggerUtil.info("📌 开始添加5个游客到「雷霆风暴」游乐历史");
        for (int i = 1; i <= 5; i++) {
            Visitor visitor = new Visitor(
                    "PERSON-P4A-" + i,
                    "历史游客" + i,
                    20 + i,
                    "VIS-P4A-" + i,
                    Visitor.MembershipType.STANDARD,
                    false
            );
            thunderstorm.addVisitorToHistory(visitor);
            if (i == 3) {
                targetVisitor = visitor;
                LoggerUtil.info("📌 标记目标游客（游客ID：" + targetVisitor.getVisitorId() + "）用于后续检查");
            }
        }

        LoggerUtil.info("\n📌 检查目标游客是否在历史中");
        thunderstorm.checkVisitorFromHistory(targetVisitor);
        thunderstorm.checkVisitorFromHistory(new Visitor());

        LoggerUtil.info("\n📌 打印「雷霆风暴」游乐历史人数");
        thunderstorm.numberOfVisitors();

        LoggerUtil.info("📌 打印「雷霆风暴」游乐历史详情（Iterator遍历）");
        thunderstorm.printRideHistory();
    }

    /**
     * Part4B：历史排序功能演示
     */
    public void partFourB() {
        LoggerUtil.info("\n=== Part4B：历史排序功能演示 ===");
        Ride logFlume = new Ride(
                "RIDE003",
                "激流勇进",
                null,
                8
        );

        LoggerUtil.info("📌 添加5个不同会员等级的游客到「激流勇进」历史");
        logFlume.addVisitorToHistory(new Visitor("P4B-1", "李四", 25, "VIS4B-1", Visitor.MembershipType.GOLD, true));
        logFlume.addVisitorToHistory(new Visitor("P4B-2", "王五", 20, "VIS4B-2", Visitor.MembershipType.PLATINUM, false));
        logFlume.addVisitorToHistory(new Visitor("P4B-3", "赵六", 30, "VIS4B-3", Visitor.MembershipType.STANDARD, true));
        logFlume.addVisitorToHistory(new Visitor("P4B-4", "孙七", 22, "VIS4B-4", Visitor.MembershipType.GOLD, false));
        logFlume.addVisitorToHistory(new Visitor("P4B-5", "周八", 19, "VIS4B-5", Visitor.MembershipType.PLATINUM, true));

        LoggerUtil.info("\n📌 排序前的游乐历史（无序）");
        logFlume.printRideHistory();

        LoggerUtil.info("📌 按规则排序（会员等级：铂金→黄金→标准；同等级按年龄升序）");
        logFlume.sortRideHistory();

        LoggerUtil.info("📌 排序后的游乐历史");
        logFlume.printRideHistory();
    }

    /**
     * Part5：运行游乐周期演示
     */
    public void partFive() {
        LoggerUtil.info("\n=== Part5：运行游乐周期演示 ===");
        Employee pirateOperator = new Employee(
                "PERSON-EMP002",
                "李师傅",
                35,
                "EMP-2025-002",
                "海盗船操作员"
        );
        Ride pirateShip = new Ride(
                PIRATE_SHIP_ID,
                PIRATE_SHIP_NAME,
                pirateOperator,
                5 // 单次最大5人
        );
        pirateShip.setRideType(RideType.FAMILY);

        LoggerUtil.info("📌 添加10个游客到" + PIRATE_SHIP_NAME + "队列");
        for (int i = 1; i <= 10; i++) {
            Visitor visitor = new Visitor(
                    "PERSON-P5-" + i,
                    "周期游客" + i,
                    10 + i,
                    "VIS-P5-" + i,
                    Visitor.MembershipType.STANDARD,
                    i % 3 == 0
            );
            pirateShip.addVisitorToQueue(visitor);
        }

        LoggerUtil.info("\n📌 运行前的" + PIRATE_SHIP_NAME + "队列");
        pirateShip.printQueue();

        LoggerUtil.info("📌 运行" + PIRATE_SHIP_NAME + "第1次周期");
        pirateShip.runOneCycle();

        LoggerUtil.info("📌 运行后的" + PIRATE_SHIP_NAME + "队列（剩余游客）");
        pirateShip.printQueue();
        LoggerUtil.info("📌 运行后的" + PIRATE_SHIP_NAME + "游乐历史（本次载客）");
        pirateShip.printRideHistory();
        LoggerUtil.info("📌 周期统计：已运行" + pirateShip.getNumOfCycles() + "次 | 本次载客" + 5 + "人");
    }

    /**
     * Part6：导出历史到文件演示
     */
    public void partSix() {
        LoggerUtil.info("\n=== Part6：导出历史到文件演示 ===");
        Ride exportRide = new Ride(
                "RIDE005",
                "导出测试设施",
                null,
                10
        );

        LoggerUtil.info("📌 添加5个游客到「导出测试设施」历史");
        for (int i = 1; i <= 5; i++) {
            Visitor visitor = new Visitor(
                    "PERSON-P6-" + i,
                    "导出游客" + i,
                    20 + i,
                    "VIS-P6-" + i,
                    i % 2 == 0 ? Visitor.MembershipType.GOLD : Visitor.MembershipType.STANDARD,
                    true
            );
            exportRide.addVisitorToHistory(visitor);
        }

        LoggerUtil.info("📌 导出历史到CSV文件（路径：" + EXPORT_FILE_PATH + "）");
        exportRide.exportRideHistory(EXPORT_FILE_PATH);
    }

    /**
     * Part7：从文件导入历史演示
     */
    public void partSeven() {
        LoggerUtil.info("\n=== Part7：从文件导入历史演示 ===");
        Ride importRide = new Ride(
                "RIDE006",
                "导入测试设施",
                null,
                10
        );
        LoggerUtil.info("📌 新建「导入测试设施」（初始历史为空）");
        importRide.numberOfVisitors();

        LoggerUtil.info("📌 从CSV文件导入历史（路径：" + EXPORT_FILE_PATH + "）");
        importRide.importRideHistory(EXPORT_FILE_PATH);

        LoggerUtil.info("\n📌 导入结果验证：");
        importRide.numberOfVisitors();
        LoggerUtil.info("📌 导入的游客详情：");
        importRide.printRideHistory();
    }
}
