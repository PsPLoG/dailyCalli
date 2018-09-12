package com.psplog.dailycalli.item;

public class Comment_item {
   // { "com_id": 1, "user_id": 2, "com_txt": "와 정말 멋진 작품이에요!", "com_date": "2018-07-22T00:36:15.000Z", "com_parent": 0, "com_seq": 1, "user_nickname": null, "user_img": null },

    int com_id;

    public Comment_item(int com_id, int user_id, String com_txt, String com_date, int com_parent, int com_seq, String user_nickname, String user_img) {
        this.com_id = com_id;
        this.user_id = user_id;
        this.com_txt = com_txt;
        this.com_date = com_date;
        this.com_parent = com_parent;
        this.com_seq = com_seq;
        this.user_nickname = user_nickname;
        this.user_img = user_img;
    }

    int user_id;
    String com_txt;
    String com_date;

    public int getCom_id() {
        return com_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCom_txt() {
        return com_txt;
    }

    public String getCom_date() {
        return com_date;
    }

    public int getCom_parent() {
        return com_parent;
    }

    public int getCom_seq() {
        return com_seq;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_img() {
        return user_img;
    }

    int com_parent;
    int com_seq;
    String user_nickname;
    String user_img;
}
