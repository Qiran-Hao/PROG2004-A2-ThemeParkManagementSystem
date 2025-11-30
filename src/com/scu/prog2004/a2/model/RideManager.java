package com.scu.prog2004.a2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例模式的设施管理器（全局唯一设施池）
 * 优化：添加volatile关键字+双重检查锁，实现线程安全的懒汉式单例
 */
public class RideManager {
    // 新增volatile：避免多线程下指令重排序导致的实例空指针
    private static volatile RideManager instance;
    private final Map<String, Ride> ridePool; // 设施池（ID->设施）

    private RideManager() {
        ridePool = new HashMap<>();
        // 初始化默认设施（带RideType）
        Employee operator1 = new Employee("EMP001", "张三", 30, "EMP-2025", "过山车操作员");
        Ride rollerCoaster = new Ride("R001", "超级过山车", operator1, 4);
        rollerCoaster.setRideType(RideType.THRILL);
        ridePool.put("R001", rollerCoaster);

        Employee operator2 = new Employee("EMP002", "李四", 35, "EMP-2025-02", "海盗船操作员");
        Ride pirateShip = new Ride("R002", "海盗船", operator2, 5);
        pirateShip.setRideType(RideType.FAMILY);
        ridePool.put("R002", pirateShip);

        Employee carouselOp = new Employee("EMP003", "王阿姨", 40, "EMP-2025-03", "旋转木马操作员");
        Ride carousel = new Ride("R003", "旋转木马", carouselOp, 3);
        carousel.setRideType(RideType.KIDDIE);
        ridePool.put("R003", carousel);
    }

    // 优化：双重检查锁实现线程安全的单例获取
    public static RideManager getInstance() {
        if (instance == null) { // 第一次检查：避免频繁加锁
            synchronized (RideManager.class) { // 加锁：保证线程安全
                if (instance == null) { // 第二次检查：避免重复创建
                    instance = new RideManager();
                }
            }
        }
        return instance;
    }

    // 获取设施（不存在则创建）
    public Ride getRide(String rideId, String name, int maxRider, RideType type) {
        if (!ridePool.containsKey(rideId)) {
            Ride ride = new Ride(rideId, name, new Employee(), maxRider);
            ride.setRideType(type);
            ridePool.put(rideId, ride);
        }
        return ridePool.get(rideId);
    }

    // 获取默认设施
    public Ride getDefaultRide(String rideId) {
        return ridePool.getOrDefault(rideId, ridePool.get("R001"));
    }
}