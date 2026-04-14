/* 圖片轉換工具 */
package com.gjun.ecs.utils;

import java.util.Base64;

public class ImageUtils {
    public static String toBase64Src(byte[] data, String type) {
        // 如果資料不全，直接回傳空字串或預設圖片路徑
        if (data == null || type == null || data.length == 0) return "";
        return "data:" + type + ";base64," + Base64.getEncoder().encodeToString(data);
    }
}