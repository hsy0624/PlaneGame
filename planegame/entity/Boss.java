package com.planegame.entity;

import com.planegame.util.ImageLoader;

import java.awt.*;
import java.util.Random;

/**
 * Boss敌机类
 */
public class Boss extends Enemy {
    private static final int BOSS_WIDTH = 220;   // Boss宽度
    private static final int BOSS_HEIGHT = 150;  // Boss高度
    private static final int BOSS_SPEED = 2;      // Boss移动速度
    private static final int SHOOT_INTERVAL = 30; // 射击间隔（帧数）
    // 静态Boss图片
    private static Image bossImage;

    // 静态初始化块，加载Boss图片
    static {
        try {
            // 修改为你的Boss图片路径
            String imagePath = "src/com/planegame/resource/images/boss1.png";
            Image img = Toolkit.getDefaultToolkit().getImage(imagePath);
            img = waitForImage(img);
            bossImage = img.getScaledInstance(BOSS_WIDTH, BOSS_HEIGHT, Image.SCALE_SMOOTH);
            bossImage = waitForImage(bossImage);
            System.out.println("Boss图片加载成功");
        } catch (Exception e) {
            System.err.println("加载Boss图片失败！");
            e.printStackTrace();
            // 创建默认图片
        }
    }

    private int health;              // Boss生命值
    private int maxHealth;           // 最大生命值
    private int movePattern;         // 移动模式
    private int moveCounter = 0;     // 移动计数器
    private int shootCounter = 0;    // 射击计数器
    private boolean movingRight = true; // 左右移动方向
    private int startX;              // 初始X坐标

    public Boss(Image image, int x, int y) {
        this(image, x, y, 100); // 默认100生命值
    }

    public Boss(Image image, int x, int y, int health) {
        super(image, x, y, BOSS_WIDTH, BOSS_HEIGHT);
        this.health = health;
        this.maxHealth = health;
        this.movePattern = new Random().nextInt(3);
        this.startX = x;
    }

    /**
     * 等待图片加载完成
     */
    private static Image waitForImage(Image img) {
        try {
            MediaTracker tracker = new MediaTracker(new Component() {
            });
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * 创建Boss（使用文件图片）
     */
    public static Boss createBoss(int x, int y) {
        return new Boss(bossImage, x, y);
    }

    @Override
    public void update() {
        moveCounter++;
        shootCounter++;

        // 根据移动模式更新位置
        switch (movePattern) {
            case 0: // 左右摆动
                if (movingRight) {
                    x += BOSS_SPEED;
                    if (x > startX + 150) movingRight = false;
                } else {
                    x -= BOSS_SPEED;
                    if (x < startX - 150) movingRight = true;
                }
                // 上下小幅正弦运动
                y = 70 + (int) (30 * Math.sin(moveCounter * 0.05));
                break;

            case 1: // 水平8字运动
                x = startX + (int) (100 * Math.sin(moveCounter * 0.03));
                y = 60 + (int) (40 * Math.sin(moveCounter * 0.06));
                break;

            case 2: // 垂直8字运动
                x = startX + (int) (80 * Math.sin(moveCounter * 0.04));
                y = 50 + (int) (60 * Math.sin(moveCounter * 0.02));
                break;
        }

        // 边界检查
        if (x < 30) x = 30;
        if (x > 450) x = 450;
    }

    /**
     * 检查Boss是否可以射击
     */
    public boolean canShoot() {
        if (shootCounter >= SHOOT_INTERVAL) {
            shootCounter = 0;
            return true;
        }
        return false;
    }

    /**
     * Boss受到伤害
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * 检查Boss是否死亡
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * 获取当前生命值
     */
    public int getHealth() {
        return health;
    }

    /**
     * 获取最大生命值
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * 创建Boss子弹
     */
    public Bullet createBullet() {
        Image bulletImg = ImageLoader.getImage("bossBullet", 10, 20);
        return new Bullet(bulletImg, x + width / 2 - 5, y + height, 10, 20, 5);
    }

    @Override
    public void draw(Graphics g) {
        // 绘制Boss
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, width, height);
        }

        // 绘制血条
        g.setColor(Color.RED);
        g.fillRect(x, y - 15, width, 10);

        g.setColor(Color.GREEN);
        int healthWidth = (int) ((double) health / maxHealth * width);
        g.fillRect(x, y - 15, healthWidth, 10);

        // 绘制血条边框
        g.setColor(Color.WHITE);
        g.drawRect(x, y - 15, width, 10);
    }
}