package com.planegame.entity;

import java.awt.*;

/**
 * 所有游戏对象的基类
 */
public abstract class GameObject {
    protected int x, y;           // 坐标
    protected int width, height;   // 尺寸
    protected Image image;         // 图片

    public GameObject(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * 绘制对象
     */
    public abstract void draw(Graphics g);

    /**
     * 获取对象的碰撞矩形
     */
    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    // Getter和Setter方法
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
