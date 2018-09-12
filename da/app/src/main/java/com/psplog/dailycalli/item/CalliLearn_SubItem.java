package com.psplog.dailycalli.item;

public class CalliLearn_SubItem {
    int calli_id;
    String calli_img;

    public CalliLearn_SubItem(int calli_id, String calli_img) {
        this.calli_id = calli_id;
        this.calli_img = calli_img;
    }

    public int getCalli_id() {
        return calli_id;
    }

    public String getCalli_img() {
        return calli_img;
    }
}
