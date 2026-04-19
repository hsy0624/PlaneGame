package com.planegame.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * 音效播放器
 */
public class AudioPlayer {
    private String audioFilePath;

    public AudioPlayer(String filePath) {
        this.audioFilePath = filePath;
    }

    public void play() {
        // 在新线程中播放，避免阻塞游戏主线程
        new Thread(() -> {
            try {
                File audioFile = new File(audioFilePath);
                if (!audioFile.exists()) {
                    System.err.println("音效文件不存在: " + audioFilePath);
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();

                // 播放完成后自动关闭资源
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("播放音效失败: " + e.getMessage());
            }
        }).start();
    }
}