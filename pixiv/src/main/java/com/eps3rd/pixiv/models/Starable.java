package com.eps3rd.pixiv.models;

/**
 * 可收藏的
 */
public interface Starable {

    int getItemID();

    void setItemID(int id);

    boolean isItemStared();

    void setItemStared(boolean isLiked);
}
