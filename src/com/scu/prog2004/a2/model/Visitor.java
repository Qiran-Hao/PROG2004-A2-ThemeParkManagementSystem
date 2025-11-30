package com.scu.prog2004.a2.model;

import java.io.Serializable; // 新增导入

/**
 * 游客类：继承自Person，专注主题公园游客属性
 * 设计思路：扩展通用属性，添加游客特有属性（游客ID、会员类型）
 * 会员类型用枚举约束（避免非法值），体现数据合法性设计
 */
public class Visitor extends Person implements Serializable { // 新增实现Serializable
    private static final long serialVersionUID = 1L; // 新增序列化版本号

    // 会员类型枚举（约束取值范围，比String更安全）
    public enum MembershipType {
        STANDARD("标准会员"), GOLD("黄金会员"), PLATINUM("铂金会员");
        private final String displayName;
        MembershipType(String displayName) {
            this.displayName = displayName;
        }
        @Override
        public String toString() {
            return displayName;
        }
    }

    // 游客特有属性（满足"至少2个实例变量"要求）
    private final String visitorId;       // 游客唯一ID（只读）
    private MembershipType membershipType; // 会员类型
    private boolean hasRideInsurance;     // 可选扩展：是否购买游乐保险（体现HD的扩展性）

    /**
     * 默认构造器：生成默认游客ID，默认标准会员
     */
    public Visitor() {
        super();
        this.visitorId = String.format("VIS-%tY%<tm%<td-%d",
                System.currentTimeMillis(), (int) (Math.random() * 1000));
        this.membershipType = MembershipType.STANDARD;
        this.hasRideInsurance = false;
    }

    /**
     * 带参构造器：初始化所有属性（父类+子类）
     * @param id 人员唯一ID（父类）
     * @param name 姓名（父类）
     * @param age 年龄（父类）
     * @param visitorId 游客ID
     * @param membershipType 会员类型（非空校验）
     * @param hasRideInsurance 是否购买保险
     */
    public Visitor(String id, String name, int age, String visitorId,
                   MembershipType membershipType, boolean hasRideInsurance) {
        super(id, name, age);
        this.visitorId = visitorId;
        this.membershipType = (membershipType != null) ? membershipType : MembershipType.STANDARD;
        this.hasRideInsurance = hasRideInsurance;
    }

    // 访问器
    public String getVisitorId() {
        return visitorId;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public boolean isHasRideInsurance() {
        return hasRideInsurance;
    }

    // 修改器
    public void setMembershipType(MembershipType membershipType) {
        if (membershipType != null) {
            this.membershipType = membershipType;
        } else {
            LoggerUtil.error("警告：会员类型不能为空！属性未更新");
        }
    }

    public void setHasRideInsurance(boolean hasRideInsurance) {
        this.hasRideInsurance = hasRideInsurance;
    }

    /**
     * 重写toString：详细展示游客信息（便于打印队列/历史）
     */
    @Override
    public String toString() {
        return String.format("Visitor{visitorId='%s', membershipType=%s, hasInsurance=%b, %s}",
                visitorId, membershipType, hasRideInsurance, super.toString());
    }
}