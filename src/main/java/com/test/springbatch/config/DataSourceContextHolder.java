package com.test.springbatch.config;

public class DataSourceContextHolder {

    private DataSourceContextHolder() {
        // Private constructor to prevent instantiation
    }

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setDataSourceKey(String key) {
        contextHolder.set(key);
    }

    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    public static void clearDataSourceKey() {
        contextHolder.remove();
    }
}