package com.eps3rd.pixiv.util;

import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;
import com.eps3rd.pixiv.models.IllustsBean;
import com.eps3rd.pixiv.models.UserBean;


public class GlideUtil {

    public static GlideUrl getMediumImg(IllustsBean illustsBean) {
        return new GlideUrlChild(illustsBean.getImage_urls().getMedium());
    }

    public static GlideUrl getUrl(String url) {
        return new GlideUrlChild(url);
    }

    public static GlideUrl getLargeImage(IllustsBean illustsBean) {
        return new GlideUrlChild(illustsBean.getImage_urls().getLarge());
    }

    public static final String DEFAULT_HEAD_IMAGE = "https://s.pximg.net/common/images/no_profile.png";

    public static GlideUrl getHead(UserBean userBean) {
        if (userBean == null) {
            return null;
        }

        if (userBean.getProfile_image_urls() == null) {
            return null;
        }

        String image = userBean.getProfile_image_urls().getMaxImage();

        if (TextUtils.equals(image, DEFAULT_HEAD_IMAGE)) {
            return new GlideUrlChild(image);
        } else {
            return new GlideUrlChild(userBean.getProfile_image_urls().getMaxImage());
        }
    }


    public static GlideUrl getSquare(IllustsBean illustsBean) {
        return new GlideUrlChild(illustsBean.getImage_urls().getSquare_medium());
    }

    public static GlideUrl getLargeImage(IllustsBean illustsBean, int i) {
        if (illustsBean.getPage_count() == 1) {
            return getLargeImage(illustsBean);
        } else {
            return new GlideUrlChild(illustsBean.getMeta_pages().get(i).getImage_urls().getLarge());
        }
    }

    public static GlideUrl getOriginalImage(IllustsBean illustsBean, int i) {
        if (illustsBean.getPage_count() == 1) {
            return new GlideUrlChild(illustsBean.getMeta_single_page().getOriginal_image_url());
        } else {
            return new GlideUrlChild(illustsBean.getMeta_pages().get(i).getImage_urls().getOriginal());
        }
    }
}
