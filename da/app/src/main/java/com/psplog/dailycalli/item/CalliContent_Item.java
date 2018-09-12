package com.psplog.dailycalli.item;

public class CalliContent_Item {
    public CalliContent_Item(int calli_id, String calli_img, String calli_title, String calli_txt, String calli_date, String calli_tag, String calliBool, String user_nickname, String user_img, int commentCount, int likeCount, String likeBool,int user_id) {
        this.calli_id = calli_id;
        this.user_id = user_id;
        this.calli_img = calli_img;
        this.calli_title = calli_title;
        this.calli_txt = calli_txt;
        this.calli_date = calli_date;
        this.calli_tag = calli_tag;
        if(calliBool.equals("true"))
            this.calliBool = true;
        else
            this.calliBool = false;
        this.user_nickname = user_nickname;
        this.user_img = user_img;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        if(likeBool.equals("true"))
            this.likeBool = true;
        else
            this.likeBool = false;
    }

    public int getCalli_id() {
        return calli_id;
    }

    public String getCalli_img() {
        return calli_img;
    }

    public String getCalli_title() {
        return calli_title;
    }

    public String getCalli_txt() {
        return calli_txt;
    }

    public String getCalli_date() {
        return calli_date;
    }

    public String getCalli_tag() {
        return calli_tag;
    }

    public boolean isCalliBool() {
        return calliBool;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_img() {
        return user_img;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLikeBool() {
        return likeBool;
    }

    int calli_id;

    public int getUser_id() {
        return user_id;
    }

    int user_id;
    String calli_img;
    String calli_title;
    String calli_txt;
    String calli_date;
    String calli_tag;
    boolean calliBool;
    String user_nickname;
    String user_img;
    int commentCount;
    int likeCount;
    boolean likeBool;
}
