package com.scu.prog2004.a2.model;

import java.util.Comparator;

/**
 * 游客比较器：实现Comparator接口，用于游乐历史排序
 * 设计思路：按"会员等级（降序）+ 年龄（升序）"排序，符合业务逻辑（会员优先）
 * 使用至少2个实例变量比较，不使用Comparable
 */
public class VisitorComparator implements Comparator<Visitor> {

    /**
     * 比较逻辑：
     * 1. 先按会员等级降序（铂金 > 黄金 > 标准）
     * 2. 会员等级相同时，按年龄升序（年轻游客优先）
     * @param v1 第一个游客
     * @param v2 第二个游客
     * @return 比较结果（-1/0/1）
     */
    @Override
    public int compare(Visitor v1, Visitor v2) {
        // 空值处理（健壮性：避免空指针异常）
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return 1;
        if (v2 == null) return -1;

        // 1. 比较会员等级（枚举自然顺序：PLATINUM > GOLD > STANDARD，反转实现降序）
        int membershipCompare = v2.getMembershipType().compareTo(v1.getMembershipType());
        if (membershipCompare != 0) {
            return membershipCompare;
        }

        // 2. 会员等级相同，比较年龄（升序）
        return Integer.compare(v1.getAge(), v2.getAge());
    }
}
