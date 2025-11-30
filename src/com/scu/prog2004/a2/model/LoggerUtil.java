package com.scu.prog2004.a2.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义日志工具（替代零散System.out）
 */
public class LoggerUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 可配置：调试模式开关（生产环境改false）
    private static final boolean DEBUG_ENABLED = Boolean.parseBoolean("true");

    // 信息日志
    public static void info(String message) {
        System.out.printf("[INFO] %s - %s%n", LocalDateTime.now().format(FORMATTER), message);
    }

    // 错误日志
    public static void error(String message) {
        System.err.printf("[ERROR] %s - %s%n", LocalDateTime.now().format(FORMATTER), message);
    }

    // 调试日志（可开关）
    public static void debug(String message) {
        if (DEBUG_ENABLED) { // 用常量替代固定值，解决“条件始终为true”的提示
            System.out.printf("[DEBUG] %s - %s%n", LocalDateTime.now().format(FORMATTER), message);
        }
    }
}