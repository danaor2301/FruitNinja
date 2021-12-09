package com.example.fruitninja;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.graphics.drawable.IconCompat;

import java.util.Random;


public class Fruit {

    private float startX, x, y, maxY, endX, midX, a, p, q, length;
    boolean leftToRight, falling = false, sliced = false;
    int score = 0;
    Bitmap bitmap;

    public Fruit(float startX, float endX, float x, float y, float midX, float maxY, float a, float p, float q, float length, boolean leftToRight) {
        this.startX = startX;
        this.x = x;
        this.y = y;
        this.maxY = maxY;
        this.endX = endX;
        this.midX = midX;
        this.a = a;
        this.p = p;
        this.q = q;
        this.length = length;
        this.leftToRight = leftToRight;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getMidX() {
        return midX;
    }

    public void setMidX(float midX) {
        this.midX = midX;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getP() {
        return p;
    }

    public void setP(float p) {
        this.p = p;
    }

    public float getQ() {
        return q;
    }

    public void setQ(float q) {
        this.q = q;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public void setLeftToRight(boolean leftToRight) {
        this.leftToRight = leftToRight;
    }

    public boolean getSliced()
    {
        return this.sliced;
    }
    public int isSliced()
    {
        if (this.sliced == true)
            this.score = 1;
        return this.score;
    }


}
