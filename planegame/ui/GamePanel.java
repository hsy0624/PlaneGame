package com.planegame.ui;

import com.planegame.audio.PlayerWithSound;
import com.planegame.entity.Boss;
import com.planegame.entity.Bullet;
import com.planegame.entity.Enemy;
import com.planegame.entity.Player;
import com.planegame.util.FontUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * 游戏主面板类
 * 负责游戏的核心逻辑、渲染和用户交互
 */
public class GamePanel extends JPanel implements ActionListener {

    private static final int ENEMY_SPAWN_RATE = 30;  // 敌机生成频率
    private static final int BOSS_COOLDOWN_EXTRA_SCORE = 300;  // 新增：击败Boss后需要额外300分才能再次召唤
    private Image backgroundImage1;  // 背景图片1
    private Image backgroundImage2;  // 背景图片2
    private int scrollY = 0;         // 滚动偏移量
    private int scrollSpeed = 2;     // 滚动速度（数值越大滚动越快）
    // ==================== 游戏对象 ====================
    private Player player;                 // 玩家对象
    private ArrayList<Enemy> enemies;      // 普通敌机列表
    private ArrayList<Bullet> enemyBullets; // 普通敌机发射的子弹列表
    private Boss boss;                     // Boss敌机对象
    private ArrayList<Bullet> bossBullets; // Boss发射的子弹列表
    // ==================== 游戏状态 ====================
    private Timer timer;                   // 游戏主循环定时器
    private int score = 0;                 // 当前得分
    private int highScore = 0;             // 历史最高分
    private boolean gameRunning = false;   // 游戏是否正在运行
    private boolean gameOver = false;      // 游戏是否结束
    private boolean gamePaused = false;    // 游戏是否暂停
    private int enemySpawnCounter = 0;     // 敌机生成计数器
    // ==================== Boss相关 ====================
    private boolean bossSpawned = false;   // Boss是否已生成
    private int bossSpawnScore = 200;      // 触发Boss出现的分数阈值
    private int bossCooldownScore = 0;     // 新增：Boss冷却期间需要额外累积的分数
    // ==================== UI按钮 ====================
    private JButton startButton;           // 开始游戏按钮
    private JButton restartButton;         // 重新开始按钮
    private JButton pauseButton;           // 暂停按钮
    private JButton resumeButton;          // 继续按钮
    private JButton menuButton;            // 返回主菜单按钮

    // ==================== 按键状态 ====================
    private boolean leftPressed = false;   // 左方向键是否按下
    private boolean rightPressed = false;  // 右方向键是否按下
    private boolean upPressed = false;     // 上方向键是否按下
    private boolean downPressed = false;   // 下方向键是否按下
    private boolean spacePressed = false;  // 空格键是否按下

    private Image gameLogo;


    /**
     * 构造函数
     * 初始化面板、按钮、按键绑定和游戏基础设置
     */
    public GamePanel() {
        try {
            backgroundImage1 = new ImageIcon("D:/JAVA/java-proj/PlaneGame/src/com/planegame/resource/images/background1.png").getImage();
            backgroundImage2 = new ImageIcon("D:/JAVA/java-proj/PlaneGame/src/com/planegame/resource/images/background2.png").getImage();
        } catch (Exception e) {
            System.err.println("背景图片加载失败，使用默认黑色背景");
        }
        setLayout(null);
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(600, 800));

        createButtons();          // 创建UI按钮
        setupKeyBindings();       // 设置键盘控制绑定
        initGame();               // 初始化游戏数据
    }

    /**
     * 创建并配置所有UI按钮
     */
    private void createButtons() {
        // 开始按钮 - 位于面板中央，用于启动游戏
        startButton = new JButton("开始游戏");
        startButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        startButton.setBounds(200, 350, 200, 60);
        startButton.setBackground(Color.GREEN);
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(startButton);

        // 重新开始按钮 - 游戏结束后显示，初始隐藏
        restartButton = new JButton("重新开始");
        restartButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        restartButton.setBounds(200, 400, 200, 60);
        restartButton.setBackground(Color.ORANGE);
        restartButton.setForeground(Color.BLACK);
        restartButton.setFocusPainted(false);
        restartButton.setVisible(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(restartButton);

        // 暂停按钮 - 位于右上角，游戏运行时显示
        pauseButton = new JButton("暂停");
        pauseButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        pauseButton.setBounds(500, 10, 80, 30);
        pauseButton.setBackground(Color.YELLOW);
        pauseButton.setForeground(Color.BLACK);
        pauseButton.setFocusPainted(false);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
            }
        });
        add(pauseButton);

        // 继续按钮 - 与暂停按钮同位置，暂停时显示
        resumeButton = new JButton("继续");
        resumeButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        resumeButton.setBounds(500, 10, 80, 30);
        resumeButton.setBackground(Color.GREEN);
        resumeButton.setForeground(Color.BLACK);
        resumeButton.setFocusPainted(false);
        resumeButton.setVisible(false);
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });
        add(resumeButton);

        // 返回主菜单按钮 - 暂停时显示，位于暂停按钮左侧
        menuButton = new JButton("主菜单");
        menuButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        menuButton.setBounds(410, 10, 100, 30);
        menuButton.setBackground(Color.LIGHT_GRAY);
        menuButton.setForeground(Color.BLACK);
        menuButton.setFocusPainted(false);
        menuButton.setVisible(false);
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMenu();
            }
        });
        add(menuButton);
    }

    /**
     * 开始新游戏
     * 重置游戏状态，隐藏菜单按钮，启动游戏循环
     */
    private void startGame() {
        gameRunning = true;
        gameOver = false;
        gamePaused = false;
        startButton.setVisible(false);
        restartButton.setVisible(false);
        pauseButton.setVisible(true);
        resumeButton.setVisible(false);
        menuButton.setVisible(false);

        // 重置玩家位置到屏幕底部中央
        if (player != null) {
            player.reset(getWidth(), getHeight());
        }

        requestFocusInWindow();
    }

    /**
     * 暂停游戏
     * 切换按钮显示状态，停止游戏逻辑更新
     */
    private void pauseGame() {
        gamePaused = true;
        pauseButton.setVisible(false);
        resumeButton.setVisible(true);
        menuButton.setVisible(true);
    }

    /**
     * 继续游戏
     * 恢复按钮显示状态，继续游戏逻辑更新
     */
    private void resumeGame() {
        gamePaused = false;
        pauseButton.setVisible(true);
        resumeButton.setVisible(false);
        menuButton.setVisible(false);
        requestFocusInWindow();
    }

    /**
     * 重新开始游戏
     * 清空所有游戏对象，重置分数和状态，保持最高分记录
     */
    private void restartGame() {
        // 更新最高分记录
        if (score > highScore) {
            highScore = score;
        }

        // 清空所有游戏对象
        enemies.clear();
        enemyBullets.clear();
        if (bossBullets != null) {
            bossBullets.clear();
        }
        if (player != null) {
            player.getBullets().clear();
            player.reset(getWidth(), getHeight());
        }

        // 重置游戏状态变量
        score = 0;
        gameRunning = true;
        gameOver = false;
        gamePaused = false;
        bossSpawned = false;
        boss = null;
        enemySpawnCounter = 0;
        bossCooldownScore = 0;

        // 重置玩家位置
        int startX = getWidth() / 2 - 25;
        int startY = getHeight() - 100;
        if (player != null) {
            player.setX(startX);
            player.setY(startY);
            player.setSpeed(0, 0);
        }

        // 重置按钮显示状态
        restartButton.setVisible(false);
        pauseButton.setVisible(true);
        resumeButton.setVisible(false);
        menuButton.setVisible(false);
        requestFocusInWindow();
    }

    /**
     * 游戏结束处理
     * 更新最高分，停止游戏逻辑，显示重新开始按钮
     */
    private void gameOver() {
        if (score > highScore) {
            highScore = score;
        }

        gameRunning = false;
        gameOver = true;
        gamePaused = false;
        pauseButton.setVisible(false);
        resumeButton.setVisible(false);
        menuButton.setVisible(false);
        restartButton.setVisible(true);
    }

    /**
     * 返回主菜单
     * 清除游戏数据，回到开始界面，保留最高分记录
     */
    private void returnToMenu() {
        // 更新最高分
        if (score > highScore) {
            highScore = score;
        }

        // 清除所有游戏对象
        enemies.clear();
        enemyBullets.clear();
        if (player != null) {
            player.getBullets().clear();
            player.reset(getWidth(), getHeight());
        }
        if (player != null) {
            player.getBullets().clear();
        }

        // 重置游戏状态
        gameRunning = false;
        gameOver = false;
        gamePaused = false;
        bossSpawned = false;
        boss = null;
        score = 0;
        enemySpawnCounter = 0;
        bossCooldownScore = 0;

        // 重置玩家位置到屏幕中央底部
        int startX = 275;
        int startY = 700;
        if (player != null) {
            player.setX(startX);
            player.setY(startY);
            player.setSpeed(0, 0);
        }

        // 恢复按钮到主菜单状态
        startButton.setVisible(true);
        restartButton.setVisible(false);
        pauseButton.setVisible(false);
        resumeButton.setVisible(false);
        menuButton.setVisible(false);

        requestFocusInWindow();
    }

    /**
     * 设置键盘按键绑定
     * 支持方向键和WASD两种控制方式，ESC键控制暂停/继续
     */
    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // 左移控制：LEFT键 或 A键
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "leftPressed");
        inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        inputMap.put(KeyStroke.getKeyStroke("A"), "leftPressed");
        inputMap.put(KeyStroke.getKeyStroke("released A"), "leftReleased");

        // 右移控制：RIGHT键 或 D键
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "rightPressed");
        inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        inputMap.put(KeyStroke.getKeyStroke("D"), "rightPressed");
        inputMap.put(KeyStroke.getKeyStroke("released D"), "rightReleased");

        // 上移控制：UP键 或 W键
        inputMap.put(KeyStroke.getKeyStroke("UP"), "upPressed");
        inputMap.put(KeyStroke.getKeyStroke("released UP"), "upReleased");
        inputMap.put(KeyStroke.getKeyStroke("W"), "upPressed");
        inputMap.put(KeyStroke.getKeyStroke("released W"), "upReleased");

        // 下移控制：DOWN键 或 S键
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "downPressed");
        inputMap.put(KeyStroke.getKeyStroke("released DOWN"), "downReleased");
        inputMap.put(KeyStroke.getKeyStroke("S"), "downPressed");
        inputMap.put(KeyStroke.getKeyStroke("released S"), "downReleased");

        // 射击控制：空格键
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "spacePressed");
        inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "spaceReleased");

        // 暂停控制：ESC键
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "pause");

        // 绑定左移动作
        actionMap.put("leftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = true;
                updatePlayerSpeed();
            }
        });

        actionMap.put("leftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = false;
                updatePlayerSpeed();
            }
        });

        // 绑定右移动作
        actionMap.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = true;
                updatePlayerSpeed();
            }
        });

        actionMap.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = false;
                updatePlayerSpeed();
            }
        });

        // 绑定上移动作
        actionMap.put("upPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = true;
                updatePlayerSpeed();
            }
        });

        actionMap.put("upReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = false;
                updatePlayerSpeed();
            }
        });

        // 绑定下移动作
        actionMap.put("downPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downPressed = true;
                updatePlayerSpeed();
            }
        });

        actionMap.put("downReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downPressed = false;
                updatePlayerSpeed();
            }
        });

        // 绑定射击动作
        actionMap.put("spacePressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spacePressed = true;
            }
        });

        actionMap.put("spaceReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spacePressed = false;
            }
        });

        // 绑定暂停/继续动作
        actionMap.put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameRunning && !gameOver) {
                    if (gamePaused) {
                        resumeGame();
                    } else {
                        pauseGame();
                    }
                }
            }
        });
    }

    /**
     * 根据按键状态更新玩家移动速度
     */
    private void updatePlayerSpeed() {
        if (player != null && gameRunning && !gamePaused) {
            int xSpeed = 0;
            int ySpeed = 0;

            if (leftPressed) xSpeed -= Player.SPEED;
            if (rightPressed) xSpeed += Player.SPEED;
            if (upPressed) ySpeed -= Player.SPEED;
            if (downPressed) ySpeed += Player.SPEED;

            player.setSpeed(xSpeed, ySpeed);
        }
    }

    /**
     * 初始化游戏数据
     * 创建玩家对象，清空游戏列表，启动定时器
     */
    private void initGame() {
        enemies = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        bossBullets = new ArrayList<>();
        score = 0;
        gameRunning = false;
        gameOver = false;
        gamePaused = false;
        bossSpawned = false;
        boss = null;
        enemySpawnCounter = 0;

        int startX = 275;
        int startY = 700;
        player = PlayerWithSound.createPlayerWithSound(startX, startY);

        // 启动游戏主循环定时器，约60FPS
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(16, this);
        timer.start();
    }

    /**
     * 生成普通敌机
     * 在屏幕顶部随机位置创建敌机
     */
    private void spawnEnemy() {
        if (getWidth() > 0) {
            enemies.add(Enemy.createRandomEnemy(getWidth()));
        }
    }

    /**
     * 生成Boss敌机
     * 在屏幕顶部中央位置创建Boss
     */
    private void spawnBoss() {
        boss = Boss.createBoss(getWidth() / 2 - 60, 50);
        bossSpawned = true;
    }

    /**
     * 检查所有碰撞事件
     * 包括：玩家子弹与敌机碰撞、玩家与敌机碰撞、Boss子弹与玩家碰撞
     */
    private void checkCollisions() {
        if (player == null) return;

        ArrayList<Bullet> bullets = player.getBullets();

        // 1. 玩家子弹与普通敌机的碰撞检测
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            Rectangle enemyRect = enemy.getRect();

            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (bullet.getRect().intersects(enemyRect)) {
                    bulletIterator.remove();  // 子弹消失
                    enemyIterator.remove();   // 敌机消失
                    score += 10;              // 击毁得分
                    break;
                }
            }
        }

        // 2. 玩家子弹与Boss的碰撞检测
        if (boss != null && !boss.isDead()) {
            Rectangle bossRect = boss.getRect();
            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (bullet.getRect().intersects(bossRect)) {
                    bulletIterator.remove();          // 子弹消失
                    boss.takeDamage(5);               // Boss扣血
                    if (boss.isDead()) {
                        score += 100;                 // 击败Boss加分
                        boss = null;
                        bossSpawned = false;
                        bossCooldownScore += BOSS_COOLDOWN_EXTRA_SCORE;
                        System.out.println("Boss被击败！下次Boss需要额外 " + bossCooldownScore + " 分");
                    }
                    break;
                }
            }
        }

        // 3. 玩家与普通敌机的碰撞检测
        Rectangle playerRect = player.getRect();
        Iterator<Enemy> enemyIter = enemies.iterator();
        while (enemyIter.hasNext()) {
            Enemy enemy = enemyIter.next();
            if (enemy.getRect().intersects(playerRect)) {
                // 玩家受到伤害
                player.takeDamage(1);
                // 敌机消失
                enemyIter.remove();

                // 检查玩家是否死亡
                if (!player.isAlive()) {
                    gameOver();
                    return;
                }
                break;  // 一次只处理一次碰撞
            }
        }

        // 4. 玩家与Boss的碰撞检测
        if (boss != null && boss.getRect().intersects(playerRect)) {
            player.takeDamage(1);

            if (!player.isAlive()) {
                gameOver();
                return;
            }
        }

        // 5. 普通敌机子弹与玩家的碰撞检测
        Iterator<Bullet> enemyBulletIterator = enemyBullets.iterator();
        while (enemyBulletIterator.hasNext()) {
            Bullet bullet = enemyBulletIterator.next();
            if (bullet.getRect().intersects(playerRect)) {
                player.takeDamage(1);
                enemyBulletIterator.remove();  // 子弹消失

                if (!player.isAlive()) {
                    gameOver();
                    return;
                }
                break;  // 一次只处理一次碰撞
            }
        }

        // 6. Boss子弹与玩家的碰撞检测
        Iterator<Bullet> bossBulletIterator = bossBullets.iterator();
        while (bossBulletIterator.hasNext()) {
            Bullet bullet = bossBulletIterator.next();
            if (bullet.getRect().intersects(playerRect)) {
                player.takeDamage(1);
                if (!player.isAlive()) {
                    gameOver();
                    return;
                }
                break;
            }
        }

        // ========== 7. 新增：玩家子弹与普通敌机子弹的碰撞检测 ==========
        Iterator<Bullet> playerBulletIterator = bullets.iterator();
        while (playerBulletIterator.hasNext()) {
            Bullet playerBullet = playerBulletIterator.next();
            Rectangle playerBulletRect = playerBullet.getRect();

            // 与普通敌机子弹碰撞
            Iterator<Bullet> enemyBulletIter = enemyBullets.iterator();
            while (enemyBulletIter.hasNext()) {
                Bullet enemyBullet = enemyBulletIter.next();
                if (playerBulletRect.intersects(enemyBullet.getRect())) {
                    // 双方子弹都消失
                    playerBulletIterator.remove();
                    enemyBulletIter.remove();
                    break;  // 玩家子弹已消失，退出内层循环
                }
            }
        }
    }

    /**
     * 绘制游戏画面
     * 根据游戏状态绘制不同的界面（开始界面/游戏界面/结束界面）
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundImage2 != null) {

            g.drawImage(backgroundImage2, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 如果没有加载成功图片，使用原来的黑色背景
            super.paintComponent(g);
        }

        // 状态1：游戏未开始，显示开始界面
        if (!gameRunning && !gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(FontUtils.getChineseFont(Font.BOLD, 60));
            g.drawString("飞机大战", 180, 250);

            // 显示历史最高分
            g.setFont(FontUtils.getChineseFont(Font.BOLD, 24));
            g.setColor(Color.RED);
            g.drawString("最高分: " + highScore, 200, 320);

            // 显示操作说明
            g.setFont(FontUtils.getChineseFont(Font.PLAIN, 20));
            g.drawString("使用方向键或WASD移动", 170, 450);
            g.drawString("按空格键发射子弹", 170, 480);
            g.drawString("击败200分出现Boss", 170, 510);

        }
        // 状态2：游戏运行中，绘制游戏元素
        else if (gameRunning && player != null) {
            // 绘制玩家飞机
            player.draw(g);

            // 绘制普通敌机
            for (Enemy enemy : enemies) {
                enemy.draw(g);
            }

            // 绘制Boss
            if (boss != null) {
                boss.draw(g);
            }

            // 绘制普通敌机子弹
            for (Bullet bullet : enemyBullets) {
                bullet.draw(g);
            }

            // 绘制Boss子弹
            for (Bullet bullet : bossBullets) {
                bullet.draw(g);
            }

            // 绘制分数信息
            g.setColor(Color.WHITE);
            g.setFont(FontUtils.getChineseFont(Font.BOLD, 20));
            g.drawString("得分: " + score, 10, 30);
            g.drawString("最高分: " + highScore, 10, 55);

            // ========== 绘制玩家生命值 ==========
            g.setColor(Color.RED);
            g.drawString("生命值: " + player.getHealth(), 10, 750);

            // 新增：显示下次Boss所需分数（如果没有Boss在场）
            if (boss == null && gameRunning) {
                int nextBossScore = bossSpawnScore + bossCooldownScore;
                g.setFont(FontUtils.getChineseFont(Font.PLAIN, 30));
                g.setColor(Color.RED);
                g.drawString("下次Boss: " + nextBossScore + "分", 10, 100);
            }

            // 如果有Boss，显示Boss生命值
            if (boss != null) {
                g.setFont(FontUtils.getChineseFont(Font.BOLD, 16));
                g.drawString("Boss生命值: " + boss.getHealth(), 10, 80);
            }

            // 如果游戏暂停，显示暂停遮罩和提示
            if (gamePaused) {
                g.setColor(new Color(0, 0, 0, 180));  // 半透明黑色遮罩
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.WHITE);
                g.setFont(FontUtils.getChineseFont(Font.BOLD, 48));
                g.drawString("游戏暂停", 180, 350);

                g.setFont(FontUtils.getChineseFont(Font.PLAIN, 20));
                g.drawString("点击'继续'按钮或按ESC键继续", 140, 420);
                g.drawString("点击'主菜单'按钮返回开始界面", 140, 480);
            }

        }
        // 状态3：游戏结束，显示结束界面
        else if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(FontUtils.getChineseFont(Font.BOLD, 48));
            g.drawString("GAME OVER", 120, 250);

            g.setFont(FontUtils.getChineseFont(Font.BOLD, 30));
            g.drawString("得分: " + score, 220, 320);

            // 显示最高分
            g.setColor(Color.YELLOW);
            g.setFont(FontUtils.getChineseFont(Font.BOLD, 24));
            g.drawString("最高分: " + highScore, 220, 370);

        }
    }

    /**
     * 定时器回调方法
     * 每帧更新游戏逻辑：玩家移动、敌人生成、位置更新、碰撞检测
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning && player != null && !gamePaused) {

            // 处理射击
            if (spacePressed) {
                player.shoot();
            }

            // 更新玩家位置
            player.update(getWidth(), getHeight());

            // 检查是否达到Boss出现条件
            if (!bossSpawned && score >= (bossSpawnScore + bossCooldownScore)) {
                spawnBoss();
            }

            // 生成普通敌机（仅在未出现Boss时生成）
            enemySpawnCounter++;
            if (enemySpawnCounter >= ENEMY_SPAWN_RATE && !bossSpawned) {
                spawnEnemy();
                enemySpawnCounter = 0;
            }

            // 更新普通敌机位置，并让它们尝试射击
            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                enemy.update();

                // 新增：敌机尝试射击
                enemy.tryShoot(enemyBullets);

                if (enemy.isOutOfBounds(getHeight())) {
                    iterator.remove();
                }
            }

            // 新增：更新普通敌机子弹位置
            Iterator<Bullet> enemyBulletIterator = enemyBullets.iterator();
            while (enemyBulletIterator.hasNext()) {
                Bullet bullet = enemyBulletIterator.next();
                bullet.update();
                if (bullet.isOutOfBounds(getHeight())) {
                    enemyBulletIterator.remove();
                }
            }


            // 更新Boss位置和射击
            if (boss != null && !boss.isDead()) {
                boss.update();

                // Boss射击逻辑
                if (boss.canShoot()) {
                    bossBullets.add(boss.createBullet());
                }
            }

            // 更新Boss子弹位置
            for (Bullet bullet : bossBullets) {
                bullet.update();
            }

            // 检测所有碰撞
            checkCollisions();
        }

        // 刷新画面
        repaint();
    }
}