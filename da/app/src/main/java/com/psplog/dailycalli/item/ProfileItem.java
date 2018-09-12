package com.psplog.dailycalli.item;

public class ProfileItem {
    int user_id;

    public ProfileItem(int user_id, String user_img, String user_intro, String user_nickname, String user_email, int postCount, int follower, int following) {
        this.user_id = user_id;
        this.user_img = user_img;
        this.user_intro = user_intro;
        this.user_nickname = user_nickname;
        this.user_email = user_email;
        this.postCount = postCount;
        this.follower = follower;
        this.following = following;
    }

    String user_img;
    String user_intro;
    String user_nickname;
    String user_email;
    int postCount;
    int follower;
    int following;

    public int getUser_id() {
        return user_id;
    }

    public String getUser_img() {
        return user_img;
    }

    public String getUser_intro() {
        return user_intro;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_email() {
        return user_email;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getFollower() {
        return follower;
    }

    public int getFollowing() {
        return following;
    }
}