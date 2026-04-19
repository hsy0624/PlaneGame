package com.planegame.entity;

import com.planegame.util.ImageLoader;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Enemy extends GameObject {
    private static final int COOLDOWN_TIME = 240; // 冷却时间（帧数），可以调整
    private static final int SHOOT_CHANCE = 60;   //
    private static Random random = new Random();
    private static Image[] enemyImages; // 存储多种敌人图片

    // 静态初始化块，加载敌人图片
    static {
        try {
            // 方法1：使用绝对路径（Windows）
            String imagePath1 = "src/com/planegame/resource/images/enemy1.png";
            String imagePath2 = "src/com/planegame/resource/images/enemy.png";

            // 加载图片
            Image img1 = Toolkit.getDefaultToolkit().getImage(imagePath1);
            Image img2 = Toolkit.getDefaultToolkit().getImage(imagePath2);

            // 等待图片加载完成（可选）
            img1 = waitForImage(img1);
            img2 = waitForImage(img2);

            enemyImages = new Image[]{img1, img2};

        } catch (Exception e) {
            System.err.println("加载敌人图片失败！");
            e.printStackTrace();
        }
    }

    private int speed;
    // 添加射击相关的属
    private int shootCooldown;  // 射击冷却时间
    private int currentCooldown; // 当前冷却时间
    private boolean canShoot;

    public Enemy(Image image, int x, int y, int width, int height) {
        super(image, x, y, width, height);
        this.speed = random.nextInt(3) + 2;
        // 初始化射击属性
        this.shootCooldown = COOLDOWN_TIME;
        this.currentCooldown = 0;
        this.canShoot = true;
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

    public static Enemy createRandomEnemy(int panelWidth) {
        if (enemyImages == null || enemyImages.length == 0) {
            // 如果图片数组为空，创建默认敌人
            return new Enemy(null, random.nextInt(panelWidth - 60), -40, 60, 60);
        }
        // 随机选择一种敌人图片
        int imageIndex = random.nextInt(enemyImages.length);
        Image enemyImg = enemyImages[imageIndex];
        int x = random.nextInt(panelWidth - 60);
        return new Enemy(enemyImg, x, -40, 60, 60);
    }

    public void update() {
        y += speed;

        // 更新射击冷却
        if (!canShoot) {
            currentCooldown++;
            if (currentCooldown >= shootCooldown) {
                canShoot = true;
                currentCooldown = 0;
            }
        }
    }

    /**
     * 尝试射击（随机几率）
     *
     * @param enemyBullets 子弹列表（传入以便直接添加）
     */
    public void tryShoot(ArrayList<Bullet> enemyBullets) {
        if (canShoot) {
            // 随机决定是否射击
            int chance = random.nextInt(SHOOT_CHANCE);
            if (chance == 0) {  // 1/SHOOT_CHANCE 的几率射击
                shoot(enemyBullets);
            }
        }
    }

    /**
     * 发射子弹
     *
     * @param enemyBullets 子弹列表
     */
    private void shoot(ArrayList<Bullet> enemyBullets) {
        // 获取敌人子弹图片
        Image bulletImg = ImageLoader.getImage("enemyBullet", 6, 12);

        // 从敌人中心偏下位置发射子弹
        int bulletX = x + width / 2 - 3;
        int bulletY = y + height;

        // 创建敌机子弹（向下移动，速度5）
        Bullet bullet = new Bullet(bulletImg, bulletX, bulletY, 6, 12, 5);
        enemyBullets.add(bullet);

        // 重置冷却
        canShoot = false;
        currentCooldown = 0;
    }

    public boolean isOutOfBounds(int panelHeight) {
        return y > panelHeight;
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }
}