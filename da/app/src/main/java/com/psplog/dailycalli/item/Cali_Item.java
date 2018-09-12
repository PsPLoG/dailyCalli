package com.psplog.dailycalli.item;

public class Cali_Item {

    public int getUser_id() {
        return user_id;
    }

    public int getCalli_id() {
        return calli_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getCalli_img() {
        return calli_img;
    }

    public String getCalli_txt() {
        return calli_txt;
    }

    int user_id;
    public int calli_id;
    String user_nickname;

    public String getUser_img() {
        return user_img;
    }

    String user_img;

    public Cali_Item(int user_id, int calli_id, String user_nickname, String calli_img, String calli_txt,
                     String user_ing) {
        this.user_id = user_id;
        this.calli_id = calli_id;
        this.user_nickname = user_nickname;
        this.calli_img = calli_img;
        this.calli_txt = calli_txt;
        this.user_img=user_ing;
    }

    String calli_img;
    String calli_txt;

}