package com.hammerdough.util;

public class UnitConvertUtil {

    /**
     * 毫秒 → 纳秒
     */
    public static long msToNs(int ms) {
        return (long) ms * 1000 * 1000;
    }

    /**
     * KB → 字节
     */
    public static long kbToByte(int kb) {
        return (long) kb * 1024;
    }

    /**
     * 纳秒 → 毫秒（返回前端展示）
     */
    public static long nsToMs(long ns) {
        return ns / 1000 / 1000;
    }

    /**
     * 字节 → KB（返回前端展示）
     */
    public static long byteToKb(long byteNum) {
        return byteNum / 1024;
    }
}