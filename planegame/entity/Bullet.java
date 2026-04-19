package com.planegame.entity;

import java.awt.*;

public class Bullet extends GameObject {
    private int ySpeed;

    public Bullet(Image image, int x, int y, int width, int height, int ySpeed) {
        super(image, x, y, width, height);
        this.ySpeed = ySpeed;
    }

    public void update() {
        y += ySpeed;
    }

    public boolean isOutOfBounds(int panelHeight) {
        return y < 0 || y > panelHeight;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }
}