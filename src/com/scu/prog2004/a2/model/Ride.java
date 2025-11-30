package com.scu.prog2004.a2.model;

import java.io.*;
import java.util.*;

/**
 * 游乐设施类：实现RideInterface，核心业务逻辑载体
 * 设计思路：高内聚封装所有游乐设施相关功能（队列、历史、运行、IO）
 * 选择最优集合实现：Queue用LinkedList（FIFO高效），History用LinkedList（迭代器遍历方便）
 */
public class Ride implements RideInterface, Serializable { // 新增实现Serializable
    private static final long serialVersionUID = 1L; // 新增序列化版本号
    // 核心属性（满足"至少3个实例变量"，包含Employee类型）
    private final String rideId;          // 游乐设施唯一ID（只读）
    private final String name;            // 设施名称（如"过山车"）
    private Employee operator;            // 操作员（Employee类型，作业要求）
    private int maxRider;                 // 单次最大载客量（Part5要求）
    private int numOfCycles;              // 已运行周期数（Part5要求，默认0）
    private RideType rideType;            // HD级扩展：设施类型（带动态行为）

    // 集合属性（Part3-4要求）
    private final Queue<Visitor> waitingQueue; // 等待队列（FIFO）
    private final LinkedList<Visitor> rideHistory; // 游乐历史（支持迭代器）

    /**
     * 默认构造器：初始化默认值，生成唯一ID
     */
    public Ride() {
        this.rideId = String.format("RIDE-%tY%<tm%<td-%d",
                System.currentTimeMillis(), (int) (Math.random() * 100));
        this.name = "默认设施";
        this.maxRider = 2; // 默认单次2人（Part5要求至少1人）
        this.numOfCycles = 0;
        this.rideType = RideType.THRILL; // 默认刺激类设施
        this.waitingQueue = new LinkedList<>(); // LinkedList实现Queue，高效FIFO
        this.rideHistory = new LinkedList<>();
    }

    /**
     * 带参构造器：初始化所有核心属性
     * @param rideId 设施ID
     * @param name 设施名称
     * @param operator 操作员（可为null）
     * @param maxRider 最大载客量（校验≥1）
     */
    public Ride(String rideId, String name, Employee operator, int maxRider) {
        this.rideId = rideId;
        this.name = (name != null && !name.isBlank()) ? name : "未知设施";
        this.operator = operator;
        this.maxRider = Math.max(1, maxRider); // 防御性编程：确保≥1
        this.numOfCycles = 0;
        this.rideType = RideType.THRILL; // 默认刺激类设施
        this.waitingQueue = new LinkedList<>();
        this.rideHistory = new LinkedList<>();
    }

    // 访问器和修改器（封装属性）
    public String getRideId() {
        return rideId;
    }

    public String getName() {
        return name;
    }

    public Employee getOperator() {
        return operator;
    }

    public void setOperator(Employee operator) {
        this.operator = operator;
        LoggerUtil.info("设施[" + name + "]操作员已更新为：" + (operator != null ? operator.getName() : "无"));
    }

    public int getMaxRider() {
        return maxRider;
    }

    public void setMaxRider(int maxRider) {
        if (maxRider >= 1) {
            this.maxRider = maxRider;
            LoggerUtil.info("设施[" + name + "]单次最大载客量已更新为：" + maxRider + "人");
        } else {
            LoggerUtil.error("警告：设施[" + name + "]最大载客量不能小于1！属性未更新");
        }
    }

    public int getNumOfCycles() {
        return numOfCycles;
    }

    public RideType getRideType() {
        return rideType;
    }

    public void setRideType(RideType rideType) {
        this.rideType = rideType;
    }

    // 扩展：年龄校验方法（根据设施类型动态判断）
    public boolean checkVisitorEligibility(Visitor visitor) {
        boolean eligible = rideType.checkAge(visitor);
        if (!eligible) {
            LoggerUtil.error(visitor.getName() + "（" + visitor.getAge() + "岁）不符合" + rideType + "设施的年龄要求！");
        }
        return eligible;
    }

    // ========================= Part3：等待队列实现 =========================
    @Override
    public void addVisitorToQueue(Visitor visitor) {
        if (visitor == null) {
            LoggerUtil.error("❌ 设施[" + name + "]无法添加空游客到队列！");
            return;
        }
        // 扩展：添加年龄校验
        if (!checkVisitorEligibility(visitor)) {
            return;
        }
        waitingQueue.offer(visitor); // Queue的offer()比add()更安全（队列满时返回false而非抛异常）
        LoggerUtil.info("✅ 游客[" + visitor.getVisitorId() + "]已加入设施[" + name + "]等待队列，当前队列长度：" + waitingQueue.size());
    }

    @Override
    public void removeVisitorFromQueue() {
        if (waitingQueue.isEmpty()) {
            LoggerUtil.error("❌ 设施[" + name + "]等待队列为空，无法移除游客！");
            return;
        }
        Visitor removed = waitingQueue.poll(); // 移除并返回队首元素（FIFO）
        LoggerUtil.info("✅ 游客[" + removed.getVisitorId() + "]已从设施[" + name + "]等待队列移除，当前队列长度：" + waitingQueue.size());
    }

    @Override
    public void printQueue() {
        System.out.printf("%n========== 设施[%s]等待队列（长度：%d）==========%n", name, waitingQueue.size());
        if (waitingQueue.isEmpty()) {
            System.out.println("📭 队列无等待游客");
            return;
        }
        // 按队列顺序打印（FIFO），使用增强for循环遍历
        int index = 1;
        for (Visitor visitor : waitingQueue) {
            System.out.printf("%d. %s%n", index++, visitor);
        }
        System.out.println("==============================================%n");
    }

    // ========================= Part4A：游乐历史实现 =========================
    @Override
    public void addVisitorToHistory(Visitor visitor) {
        if (visitor != null) {
            rideHistory.add(visitor);
            LoggerUtil.info("✅ 游客[" + visitor.getVisitorId() + "]已添加到设施[" + name + "]游乐历史，历史总人数：" + rideHistory.size());
        } else {
            LoggerUtil.error("❌ 设施[" + name + "]无法添加空游客到历史记录！");
        }
    }

    @Override
    public boolean checkVisitorFromHistory(Visitor visitor) {
        if (visitor == null) {
            LoggerUtil.error("❌ 设施[" + name + "]无法检查空游客是否在历史中！");
            return false;
        }
        // 遍历历史记录（使用Iterator）
        Iterator<Visitor> iterator = rideHistory.iterator();
        while (iterator.hasNext()) {
            Visitor historyVisitor = iterator.next();
            // 按游客唯一ID判断（比equals更精准）
            if (historyVisitor.getVisitorId().equals(visitor.getVisitorId())) {
                LoggerUtil.info("✅ 游客[" + visitor.getVisitorId() + "]已在设施[" + name + "]游乐历史中");
                return true;
            }
        }
        LoggerUtil.info("❌ 游客[" + visitor.getVisitorId() + "]不在设施[" + name + "]游乐历史中");
        return false;
    }

    @Override
    public int numberOfVisitors() {
        int count = rideHistory.size();
        LoggerUtil.info("📊 设施[" + name + "]游乐历史总人数：" + count + "人");
        return count;
    }

    @Override
    public void printRideHistory() {
        System.out.printf("%n========== 设施[%s]游乐历史（总人数：%d）==========%n", name, rideHistory.size());
        if (rideHistory.isEmpty()) {
            System.out.println("📜 历史无游乐记录");
            return;
        }
        // 必须使用Iterator遍历
        Iterator<Visitor> iterator = rideHistory.iterator();
        int index = 1;
        while (iterator.hasNext()) {
            Visitor visitor = iterator.next();
            System.out.printf("%d. %s%n", index++, visitor);
        }
        System.out.println("==============================================%n");
    }

    // ========================= Part4B：历史排序实现 =========================
    /**
     * 按自定义规则排序游乐历史（使用Collections.sort + Comparator）
     */
    public void sortRideHistory() {
        if (rideHistory.isEmpty()) {
            LoggerUtil.error("❌ 设施[" + name + "]游乐历史为空，无法排序！");
            return;
        }
        // 使用自定义Comparator排序（会员等级降序+年龄升序）
        Collections.sort(rideHistory, new VisitorComparator());
        LoggerUtil.info("✅ 设施[" + name + "]游乐历史已完成排序（规则：会员等级降序→年龄升序）");
    }

    // ========================= Part5：运行游乐周期 =========================
    @Override
    public void runOneCycle() {
        System.out.printf("%n========== 设施[%s]开始运行一次周期 ==========%n", name);

        // 校验1：是否有操作员
        if (operator == null) {
            LoggerUtil.error("❌ 运行失败：无操作员分配！请先指定操作员");
            System.out.println("==============================================%n");
            return;
        }

        // 校验2：等待队列是否有游客
        if (waitingQueue.isEmpty()) {
            LoggerUtil.error("❌ 运行失败：等待队列为空，无游客可乘坐");
            System.out.println("==============================================%n");
            return;
        }

        // 核心逻辑：按maxRider从队列取游客，添加到历史
        int ridersCount = 0;
        while (!waitingQueue.isEmpty() && ridersCount < maxRider) {
            Visitor rider = waitingQueue.poll();
            addVisitorToHistory(rider); // 自动添加到历史
            ridersCount++;
        }

        // 更新周期数
        numOfCycles++;
        LoggerUtil.info("✅ 设施[" + name + "]第" + numOfCycles + "次周期运行成功！本次载客：" + ridersCount + "人，剩余等待人数：" + waitingQueue.size());
        System.out.println("==============================================%n");
    }

    // ========================= Part6：导出历史到文件 =========================
    /**
     * 导出游乐历史到CSV文件（逗号分隔，每个游客一行）
     * @param filePath 文件路径（如"rideHistory_rollerCoaster.csv"）
     */
    public void exportRideHistory(String filePath) {
        if (rideHistory.isEmpty()) {
            LoggerUtil.error("❌ 设施[" + name + "]游乐历史为空，无需导出！");
            return;
        }

        // 使用try-with-resources自动关闭流（IO最佳实践，避免资源泄漏）
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 写入CSV表头（便于阅读）
            writer.write("visitorId,personId,name,age,membershipType,hasInsurance");
            writer.newLine();

            // 遍历历史，写入每个游客数据（包含所有核心属性）
            for (Visitor visitor : rideHistory) {
                String line = String.format("%s,%s,%s,%d,%s,%b",
                        visitor.getVisitorId(),
                        visitor.getId(),
                        visitor.getName().replace(",", " "), // 处理姓名中的逗号（避免CSV格式错误）
                        visitor.getAge(),
                        visitor.getMembershipType().name(), // 修改：存储枚举常量名
                        visitor.isHasRideInsurance()
                );
                writer.write(line);
                writer.newLine();
            }

            LoggerUtil.info("✅ 设施[" + name + "]游乐历史已成功导出到：" + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            LoggerUtil.error("❌ 设施[" + name + "]导出历史失败！错误信息：" + e.getMessage());
        }
    }

    // ========================= Part7：从文件导入历史 =========================
    /**
     * 从CSV文件导入游乐历史（读取文件并添加到LinkedList）
     * @param filePath 文件路径
     */
    public void importRideHistory(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            LoggerUtil.error("❌ 导入失败：文件不存在！路径：" + file.getAbsolutePath());
            return;
        }

        // 临时存储导入的游客（避免导入过程中污染原历史）
        List<Visitor> importedVisitors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true; // 跳过表头

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 分割CSV行（处理空行）
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    LoggerUtil.error("警告：跳过无效行（格式错误）：" + line);
                    continue;
                }

                // 解析每个字段（带异常处理，避免单个字段错误导致整体失败）
                try {
                    String visitorId = parts[0].trim();
                    String personId = parts[1].trim();
                    String name = parts[2].trim();
                    int age = Integer.parseInt(parts[3].trim());
                    // 解析会员类型（修改：直接使用枚举常量名）
                    Visitor.MembershipType membershipType = Visitor.MembershipType.valueOf(
                            parts[4].trim()
                    );
                    boolean hasInsurance = Boolean.parseBoolean(parts[5].trim());

                    // 创建游客对象并添加到临时列表
                    Visitor visitor = new Visitor(personId, name, age, visitorId, membershipType, hasInsurance);
                    importedVisitors.add(visitor);
                } catch (IllegalArgumentException e) {
                    LoggerUtil.error("警告：跳过无效行（数据转换失败）：" + line + "，错误：" + e.getMessage());
                }
            }

            // 导入成功后，合并到原历史（避免重复添加）
            for (Visitor visitor : importedVisitors) {
                if (!checkVisitorFromHistory(visitor)) { // 检查是否已存在
                    addVisitorToHistory(visitor);
                }
            }

            LoggerUtil.info("✅ 从文件[" + filePath + "]导入成功！共导入：" + importedVisitors.size() + "人，当前历史总人数：" + rideHistory.size());
        } catch (IOException e) {
            LoggerUtil.error("❌ 导入历史失败！错误信息：" + e.getMessage());
        }
    }
}