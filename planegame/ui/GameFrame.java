package com.planegame.ui;

import javax.swing.*;

/**
 * 游戏主窗口
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("飞机大战");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 添加游戏面板
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack(); // 根据GamePanel的preferredSize调整窗口大小
        setLocationRelativeTo(null); // 窗口居中
        setResizable(false); // 禁止调整大小
        setVisible(true);
    }

    public static void main(String[] args) {
        // 确保在事件调度线程中创建GUI
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}