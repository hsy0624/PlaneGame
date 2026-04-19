package com.planegame.audio;

import com.planegame.entity.Player;

import java.awt.*;

/**
 * 带射击音效的玩家类
 * 继承Player，不修改原Player类
 */
public class PlayerWithSound extends Player {

    private static final long SHOOT_DELAY = 85; //延迟
    private static AudioPlayer shootAudio;

    // 静态初始化块，只加载一次音效
    static {
        try {
            shootAudio = new AudioPlayer("src/com/planegame/resource/sounds/player-发射.wav");
            System.out.println("射击音效加载成功");
        } catch (Exception e) {
            System.err.println("加载射击音效失败");
        }
    }

    private long lastShootTime = 0;

    public PlayerWithSound(Image image, int x, int y) {
        super(image, x, y);
    }

    /**
     * 创建带音效的玩家
     */
    public static Player createPlayerWithSound(int x, int y) {
        // 使用 getter 方法访问父类的私有静态字段
        return new PlayerWithSound(Player.getPlayerImage(), x, y);
    }

    @Override
    public void shoot() {
        long currentTime = System.currentTimeMillis();

        // 限制射击频率
        if (currentTime - lastShootTime < SHOOT_DELAY) {
            return;
        }
        lastShootTime = currentTime;

        // 在新线程中播放音效，避免卡顿
        if (shootAudio != null) {
            new Thread(() -> {
                try {
                    shootAudio.play();
                } catch (Exception e) {
                    // 静默失败，不影响游戏
                }
            }).start();
        }

        super.shoot();
    }
}