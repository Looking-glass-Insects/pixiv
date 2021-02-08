package com.eps3rd.pixiv.util;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.eps3rd.pixiv.api.PixivHeaders;

import java.util.HashMap;


public class GlideUrlChild extends GlideUrl {

    public GlideUrlChild(String url) {
        this(url, formatHeader());
    }

    public GlideUrlChild(String url, Headers headers) {
        super(url, headers);
    }

    private static Headers formatHeader() {
        PixivHeaders pixivHeaders = new PixivHeaders();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Params.MAP_KEY_SMALL, Params.IMAGE_REFERER);
        hashMap.put("x-client-time", pixivHeaders.getXClientTime());
        hashMap.put("x-client-hash", pixivHeaders.getXClientHash());
        hashMap.put(Params.USER_AGENT, Params.PHONE_MODEL);
        return () -> hashMap;
    }
}
