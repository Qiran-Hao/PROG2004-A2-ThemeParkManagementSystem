package com.scu.prog2004.a2.model;

import java.io.Serializable; // 新增导入

/**
 * 员工类：继承自Person，专注主题公园运营人员属性
 * 设计思路：扩展Person的通用属性，添加员工特有属性（工号、岗位）
 * 遵循里氏替换原则（可替换Person类型使用）
 */
public class Employee extends Person implements Serializable { // 新增实现Serializable
    private static final long serialVersionUID = 1L; // 新增序列化版本号

    // 员工特有属性（满足"至少2个实例变量"要求）
    private final String employeeId; // 工号（唯一，只读）
    private String position;         // 岗位（如"过山车操作员"、"安全员"）

    /**
     * 默认构造器：调用父类默认构造，生成默认工号
     */
    public Employee() {
        super();
        this.employeeId = String.format("EMP-%tY%<tm%<td-%d",
                System.currentTimeMillis(), (int) (Math.random() * 100));
    }

    /**
     * 带参构造器：初始化父类属性+子类属性（满足作业要求）
     * @param id 人员唯一ID（父类）
     * @param name 姓名（父类）
     * @param age 年龄（父类）
     * @param employeeId 工号
     * @param position 岗位（校验非空）
     */
    public Employee(String id, String name, int age, String employeeId, String position) {
        super(id, name, age); // 调用父类构造器初始化通用属性
        this.employeeId = employeeId;
        this.position = (position != null && !position.isBlank()) ? position : "未知岗位";
    }

    // 访问器
    public String getEmployeeId() {
        return employeeId; // 只读属性
    }

    public String getPosition() {
        return position;
    }

    // 修改器（带校验）
    public void setPosition(String position) {
        if (position != null && !position.isBlank()) {
            this.position = position;
        } else {
            LoggerUtil.error("警告：岗位名称不能为空！属性未更新");
        }
    }

    /**
     * 重写toString：包含父类属性+子类属性，格式统一
     */
    @Override
    public String toString() {
        return String.format("Employee{employeeId='%s', position='%s', %s}",
                employeeId, position, super.toString());
    }
}