package com.planegame.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片资源加载器（使用BufferedImage替代createImage）
 */
public class ImageLoader {
    private static Map<String, Image> imageCache = new HashMap<>();

    /**
     * 获取图片，如果缓存中没有则创建
     */
    public static Image getImage(String key, int width, int height) {
        if (!imageCache.containsKey(key)) {
            // 根据不同的key使用不同的颜色
            Color color = getColorByKey(key);
            imageCache.put(key, createColoredImage(width, height, color, key));
        }
        return imageCache.get(key);
    }

    /**
     * 根据key获取对应的颜色
     */
    private static Color getColorByKey(String key) {
        if (key.contains("player") || key.equals("player")) {
            return Color.WHITE;
        } else if (key.contains("enemy") || key.equals("enemy")) {
            return Color.RED;
        } else if (key.contains("boss") || key.equals("boss")) {
            return Color.MAGENTA;
        } else if (key.contains("bullet") && !key.contains("boss")) {
            return Color.YELLOW;
        } else if (key.contains("bossBullet")) {
            return Color.ORANGE;
        } else if (key.contains("powerup") || key.contains("item")) {
            return Color.CYAN;
        } else {
            return Color.GRAY; // 默认颜色
        }
    }

    /**
     * 创建纯色图片（使用BufferedImage）
     */
    private static Image createColoredImage(int width, int height, Color color, String key) {
        // 使用BufferedImage替代createImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        try {
            // 开启抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 根据key和color绘制不同的造型
            if (key.contains("bullet") && !key.contains("boss") && !key.contains("enemy")) { // 玩家子弹
                drawPlayerBullet(g2d, width, height);
            } else if (key.contains("enemyBullet")) { // 敌人子弹（新增）
                drawEnemyBullet(g2d, width, height);
            } else if (key.contains("bossBullet")) { // Boss子弹
                drawBossBullet(g2d, width, height);
            } else { // 默认绘制
                g2d.setColor(color);
                g2d.fillRect(0, 0, width, height);
                g2d.setColor(Color.WHITE);
                g2d.drawRect(0, 0, width - 1, height - 1);
            }

        } finally {
            g2d.dispose();
        }

        return image;
    }

    /**
     * 绘制玩家子弹
     */
    private static void drawPlayerBullet(Graphics2D g2d, int width, int height) {
        // 子弹主体
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(0, 0, width, height);

        // 弹头（橙色）
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(1, 1, width - 2, height / 2);

        // 尾焰（红色）
        g2d.setColor(Color.RED);
        g2d.fillRect(width / 2 - 1, height - 3, 2, 3);

        // 发光效果
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.fillOval(-2, -2, width + 4, height + 4);

        // 边框
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, width - 1, height - 1);
    }

    /**
     * 绘制Boss子弹
     */
    private static void drawBossBullet(Graphics2D g2d, int width, int height) {
        // 子弹主体
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(0, 0, width, height);

        // 核心
        g2d.setColor(Color.RED);
        g2d.fillOval(width / 4, height / 4, width / 2, height / 2);

        // 发光效果
        g2d.setColor(new Color(255, 165, 0, 100));
        g2d.fillOval(-3, -3, width + 6, height + 6);

        // 边框
        g2d.setColor(Color.YELLOW);
        g2d.drawOval(0, 0, width - 1, height - 1);
    }

    private static void drawEnemyBullet(Graphics2D g2d, int width, int height) {
        // 子弹主体（红色）
        g2d.setColor(new Color(255, 0, 0)); // 纯红色
        g2d.fillRect(0, 0, width, height);

        // 弹头（深红色）
        g2d.setColor(new Color(200, 0, 0));
        g2d.fillRect(1, 1, width - 2, height / 2);

        // 尾焰（暗红色）
        g2d.setColor(new Color(150, 0, 0));
        g2d.fillRect(width / 2 - 1, height - 3, 2, 3);

        // 发光效果（红色光晕）
        g2d.setColor(new Color(255, 0, 0, 100));
        g2d.fillOval(-2, -2, width + 4, height + 4);

        // 边框（橙色）
        g2d.setColor(new Color(255, 100, 0));
        g2d.drawRect(0, 0, width - 1, height - 1);
    }

    /**
     * 清除缓存（如果需要重新加载图片时使用）
     */
    public static void clearCache() {
        imageCache.clear();
    }

    /**
     * 预加载所有游戏图片
     */
    public static void preloadImages() {
        // 预加载玩家子弹
        getImage("bullet", 5, 10);

        // 预加载Boss子弹
        getImage("bossBullet", 10, 20);
    }
}