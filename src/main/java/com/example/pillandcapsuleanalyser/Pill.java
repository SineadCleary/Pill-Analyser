package com.example.pillandcapsuleanalyser;

import javafx.scene.text.Text;

public class Pill {
    private int root;
    private String name;
    private int num;
    private int size;
    private int type;
    Text numText;

    private int xPos, yPos;

    public Pill(int root, String name, int num, int size, int x, int y, int type) {
        this.root = root;
        setName(name);
        setNum(num);
        setSize(size);
        setXPos(x);
        setYPos(y);
        this.type = type;
    }

    public int getRoot(){
        return root;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public int getType(){
        return type;
    }
}
