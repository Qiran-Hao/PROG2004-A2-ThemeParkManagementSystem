package com.scu.prog2004.a2.model;

/**
 * 设施类型枚举（带动态年龄校验规则）
 */
public enum RideType {
    THRILL("刺激类") {
        @Override
        public boolean checkAge(Visitor visitor) {
            return visitor.getAge() >= 16; // 刺激类需16岁以上
        }
    },
    FAMILY("家庭类") {
        @Override
        public boolean checkAge(Visitor visitor) {
            return true; // 无年龄限制
        }
    },
    KIDDIE("儿童类") {
        @Override
        public boolean checkAge(Visitor visitor) {
            return visitor.getAge() >= 3 && visitor.getAge() <= 12;
        }
    };

    private final String displayName;

    RideType(String displayName) {
        this.displayName = displayName;
    }

    // 抽象方法：不同类型的年龄校验规则（策略模式）
    public abstract boolean checkAge(Visitor visitor);

    @Override
    public String toString() {
        return displayName;
    }
}