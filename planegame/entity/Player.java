package com.planegame.entity;

import com.planegame.util.ImageLoader;

import java.awt.*;
import java.util.ArrayList;

public class Player extends GameObject {
    // 常量定义
    public static final int SPEED = 5;
    private static final int DEFAULT_WIDTH = 70;
    private static final int DEFAULT_HEIGHT = 70;
    private static final int COOLDOWN_TIME = 10;
    private static final int PLAYER_OFFSET_BOTTOM = 80;

    // 射击参数
    private static final int BULLET_WIDTH = 5;
    private static final int BULLET_HEIGHT = 10;
    private static final int BULLET_SPEED = -8;
    private static final int BULLET_OFFSET_X1 = 10;
    private static final int BULLET_OFFSET_X2 = 35;
    private static final int BULLET_OFFSET_Y = -5;

    // ========== 新增：生命值相关常量 ==========
    private static final int DEFAULT_HEALTH = 3;       // 默认生命值
    private static final int INVINCIBLE_DURATION = 120;  // 无敌时间（帧数，约1秒）
    private static final int BLINK_INTERVAL = 5;        // 闪烁间隔（帧数）

    // 静态资源
    private static Image playerImage;
    private static boolean imageLoaded = false;

    // 静态初始化块，加载玩家图片
    static {
        try {
            String imagePath = "src/com/planegame/resource/images/player_plane.png";
            Image img = Toolkit.getDefaultToolkit().getImage(imagePath);
            img = waitForImage(img);
            playerImage = img.getScaledInstance(DEFAULT_WIDTH, DEFAULT_HEIGHT, Image.SCALE_SMOOTH);
            playerImage = waitForImage(playerImage);
            System.out.println("玩家图片加载成功");
            imageLoaded = true;
        } catch (Exception e) {
            System.err.println("加载玩家图片失败！");
            e.printStackTrace();
        }
    }

    // 实例变量
    private int xSpeed = 0;
    private int ySpeed = 0;
    private ArrayList<Bullet> bullets;
    private boolean canShoot = true;
    private int shootCooldown = 0;
    // ========== 新增：生命值相关变量 ==========
    private int health;              // 当前生命值
    private int maxHealth;          // 最大生命值
    private boolean invincible;     // 是否无敌
    private int invincibleTimer;    // 无敌计时器

    public Player(Image image, int x, int y) {
        super(image, x, y, 70, 70);
        bullets = new ArrayList<>();

        // ========== 初始化生命值 ==========
        this.health = DEFAULT_HEALTH;
        this.maxHealth = DEFAULT_HEALTH;
        this.invincible = false;
        this.invincibleTimer = 0;
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
     * 创建玩家（使用文件图片）
     */
    public static Player createPlayer(int x, int y) {
        return new Player(playerImage, x, y);
    }

    /**
     * 创建玩家音效（使用文件wav格式）
     */

    public static Image getPlayerImage() {
        return playerImage;
    }

    /**
     * 创建玩家（默认位置）
     */
    public static Player createPlayerAtCenter(int panelWidth, int panelHeight) {
        int x = panelWidth / 2 - 25; // 宽度50的一半
        int y = panelHeight - 80;
        return new Player(playerImage, x, y);
    }

    // 添加setX方法
    public void setX(int x) {
        this.x = x;
    }

    // 添加setY方法
    public void setY(int y) {
        this.y = y;
    }

    public void setSpeed(int xSpeed, int ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void shoot() {
        if (canShoot) {
            Image bulletImg = ImageLoader.getImage("bullet", 5, 10);
            // 调整子弹发射位置，从飞机两侧发射
            bullets.add(new Bullet(bulletImg, x + 10, y - 5, 5, 10, -8));
            bullets.add(new Bullet(bulletImg, x + 35, y - 5, 5, 10, -8));
            canShoot = false;
            shootCooldown = COOLDOWN_TIME;
        }
    }

    // ========== 玩家受到伤害 ==========
    public boolean takeDamage(int damage) {
        // 无敌状态下不受伤害
        if (invincible) {
            return false;
        }

        health -= damage;

        // 生命值不能小于0
        if (health < 0) {
            health = 0;
        }

        // 进入无敌状态
        invincible = true;
        invincibleTimer = INVINCIBLE_DURATION;

        System.out.println("玩家受到伤害！剩余生命值: " + health);

        return true;
    }

    // ========== 新增：检查玩家是否存活 ==========
    public boolean isAlive() {
        return health > 0;
    }

    // ========== 新增：获取当前生命值 ==========
    public int getHealth() {
        return health;
    }

    // ========== 新增：获取最大生命值 ==========
    public int getMaxHealth() {
        return maxHealth;
    }

    // ========== 新增：增加生命值 ==========
    public void addHealth(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    // ========== 新增：重置生命值 ==========
    public void resetHealth() {
        health = DEFAULT_HEALTH;
        invincible = false;
        invincibleTimer = 0;
    }

    // ========== 新增：判断当前是否应该闪烁 ==========
    public boolean shouldBlink() {
        if (!invincible) return false;
        // 每 BLINK_INTERVAL 帧切换一次闪烁状态
        return (invincibleTimer / BLINK_INTERVAL) % 2 == 0;
    }

    public void update(int panelWidth, int panelHeight) {
        x += xSpeed;
        y += ySpeed;

        if (x < 0) x = 0;
        if (x > panelWidth - width) x = panelWidth - width;
        if (y < 0) y = 0;
        if (y > panelHeight - height) y = panelHeight - height;

        if (shootCooldown > 0) {
            shootCooldown--;
            if (shootCooldown == 0) {
                canShoot = true;
            }
        }

        // ========== 更新无敌状态 ==========
        if (invincible) {
            invincibleTimer--;
            if (invincibleTimer <= 0) {
                invincible = false;
            }
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            if (bullet.isOutOfBounds(panelHeight)) {
                bullets.remove(i);
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // ========== 闪烁效果：无敌时半透明显示 ==========
        float alpha = 1.0f;
        if (invincible && shouldBlink()) {
            alpha = 0.4f;  // 半透明闪烁
        }

        // 应用透明度
        Composite originalComposite = null;
        if (alpha < 1.0f) {
            originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // 如果图片为空，绘制默认造型
            g.setColor(Color.WHITE);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLUE);
            g.drawRect(x, y, width - 1, height - 1);
        }

        // 恢复透明度
        if (originalComposite != null) {
            g2d.setComposite(originalComposite);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    /**
     * 重置玩家状态
     */
    public void reset(int panelWidth, int panelHeight) {
        x = panelWidth / 2 - width / 2;
        y = panelHeight - 80;
        xSpeed = 0;
        ySpeed = 0;
        bullets.clear();
        canShoot = true;
        shootCooldown = 0;
        // ========== 重置生命值 ==========
        resetHealth();
    }

    /**
     * 获取玩家的边界矩形（用于碰撞检测）
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}