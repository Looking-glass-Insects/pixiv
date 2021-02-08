package com.eps3rd.pixiv.model;

import com.eps3rd.pixiv.interfaces.ListShow;
import com.eps3rd.pixiv.models.MangaSeriesItem;

import java.util.List;

public class ListMangaSeries implements ListShow<MangaSeriesItem> {

    private String next_url;
    private List<MangaSeriesItem> illust_series_details;

    @Override
    public List<MangaSeriesItem> getList() {
        return illust_series_details;
    }

    @Override
    public String getNextUrl() {
        return next_url;
    }
}
