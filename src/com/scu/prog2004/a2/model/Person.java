package com.scu.prog2004.a2.model;

import java.io.Serializable; // 新增导入

/**
 * 抽象基类：表示主题公园中的"人"（员工/游客的共同父类）
 * 设计思路：Person不会被直接实例化（无具体场景），因此设为抽象类
 * 封装通用属性（id、姓名、年龄），提供标准访问器和修改器
 */
public abstract class Person implements Serializable { // 新增实现Serializable
    private static final long serialVersionUID = 1L; // 新增序列化版本号

    // 核心属性（满足"至少3个实例变量"要求，选择业务有意义的属性）
    private final String id;       // 唯一标识（不可修改，构造时初始化）
    private String name;           // 姓名
    private int age;               // 年龄

    /**
     * 默认构造器
     * 生成默认唯一ID（格式：PERSON-YYYYMMDD-HHMMSS-随机数）
     */
    public Person() {
        this.id = String.format("PERSON-%tY%<tm%<td-%<tH%<tM%<tS-%d",
                System.currentTimeMillis(), (int) (Math.random() * 1000));
    }

    /**
     * 带参构造器（初始化所有属性）
     * @param id 唯一标识
     * @param name 姓名
     * @param age 年龄（校验非负）
     */
    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = Math.max(0, age); // 防御性编程：避免负年龄
    }

    // 访问器（getter）- 封装属性，仅暴露必要访问
    public String getId() {
        return id; // 只读属性，无setter
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // 修改器（setter）- 带参数校验，保证数据合法性
    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        } else {
            LoggerUtil.error("警告：姓名不能为空！属性未更新");
        }
    }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        } else {
            LoggerUtil.error("警告：年龄不能为负数！属性未更新");
        }
    }

    /**
     * 重写toString：便于打印对象详情（调试/展示用）
     * 格式统一，包含所有核心属性
     */
    @Override
    public String toString() {
        return String.format("Person{id='%s', name='%s', age=%d}", id, name, age);
    }
}
