package com.example.campusgeoquiz;

public class Product {
    private String image;
    private int lvl;

    public Product(){

    }

    public Product(String image, int lvl) {
        this.image = image;
        this.lvl = lvl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }
}