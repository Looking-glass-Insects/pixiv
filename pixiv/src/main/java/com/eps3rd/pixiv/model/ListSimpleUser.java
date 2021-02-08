package com.eps3rd.pixiv.model;

import com.eps3rd.pixiv.interfaces.ListShow;
import com.eps3rd.pixiv.models.UserBean;

import java.util.List;

public class ListSimpleUser implements ListShow<UserBean> {

    private String next_url;
    private List<UserBean> users;

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String pNext_url) {
        next_url = pNext_url;
    }

    public List<UserBean> getUsers() {
        return users;
    }

    public void setUsers(List<UserBean> pUsers) {
        users = pUsers;
    }

    @Override
    public List<UserBean> getList() {
        return users;
    }

    @Override
    public String getNextUrl() {
        return next_url;
    }
}
