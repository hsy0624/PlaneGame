package com.planegame.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 字体工具类 - 自动检测系统中可用的中文字体
 */
public class FontUtils {

    private static Font chineseFont;

    /**
     * 获取支持中文的字体
     */
    public static Font getChineseFont(int style, int size) {
        if (chineseFont == null) {
            chineseFont = findChineseFont();
        }
        return chineseFont.deriveFont(style, size);
    }

    /**
     * 查找系统中可用的中文字体
     */
    private static Font findChineseFont() {
        // 常用中文字体列表（按优先级排序）
        String[] preferredFonts = {
                "Microsoft YaHei",      // Windows 微软雅黑
                "SimSun",               // Windows 宋体
                "SimHei",               // Windows 黑体
                "KaiTi",                // Windows 楷体
                "FangSong",             // Windows 仿宋
                "PingFang SC",          // macOS 苹方
                "Hiragino Sans GB",     // macOS 冬青黑体
                "STHeiti",              // macOS 华文黑体
                "STKaiti",              // macOS 华文楷体
                "WenQuanYi Micro Hei",  // Linux 文泉驿微米黑
                "Noto Sans CJK SC",     // Linux Noto字体
                "Dialog"                // 默认字体
        };

        // 获取系统所有可用字体
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();

        // 查找首选字体
        for (String preferredFont : preferredFonts) {
            for (String availableFont : availableFonts) {
                if (availableFont.equalsIgnoreCase(preferredFont)) {
                    System.out.println("找到中文字体: " + availableFont);
                    return new Font(availableFont, Font.PLAIN, 12);
                }
            }
        }

        // 如果都没找到，返回默认字体
        System.out.println("未找到中文字体，使用默认字体");
        return new Font("Dialog", Font.PLAIN, 12);
    }

    /**
     * 获取所有支持中文的字体列表（用于调试）
     */
    public static List<String> getChineseFonts() {
        List<String> chineseFonts = new ArrayList<>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();

        // 中文字体通常包含这些字符集
        String[] chineseKeywords = {"宋", "黑", "楷", "仿", "雅黑", "微软", "苹方",
                "冬青", "华文", "文泉", "Noto", "Song", "Hei",
                "Kai", "Fang", "Ming", "Yao"};

        for (String fontName : availableFonts) {
            for (String keyword : chineseKeywords) {
                if (fontName.contains(keyword)) {
                    chineseFonts.add(fontName);
                    break;
                }
            }
        }

        return chineseFonts;
    }
}