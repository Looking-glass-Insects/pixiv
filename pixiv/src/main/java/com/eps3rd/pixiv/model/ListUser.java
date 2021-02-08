package com.eps3rd.pixiv.model;

import com.eps3rd.pixiv.interfaces.ListShow;
import com.eps3rd.pixiv.models.UserPreviewsBean;

import java.util.List;


public class ListUser implements ListShow<UserPreviewsBean> {

    private String next_url;
    private List<UserPreviewsBean> user_previews;

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }

    public List<UserPreviewsBean> getUser_previews() {
        return user_previews;
    }

    public void setUser_previews(List<UserPreviewsBean> user_previews) {
        this.user_previews = user_previews;
    }

    @Override
    public List<UserPreviewsBean> getList() {
        return user_previews;
    }

    @Override
    public String getNextUrl() {
        return next_url;
    }
}
