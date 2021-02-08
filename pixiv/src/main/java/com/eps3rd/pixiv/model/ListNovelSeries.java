package com.eps3rd.pixiv.model;

import com.eps3rd.pixiv.interfaces.ListShow;
import com.eps3rd.pixiv.models.NovelSeriesItem;

import java.util.List;



public class ListNovelSeries implements ListShow<NovelSeriesItem> {

    private String next_url;
    private List<NovelSeriesItem> novel_series_details;

    @Override
    public List<NovelSeriesItem> getList() {
        return novel_series_details;
    }

    @Override
    public String getNextUrl() {
        return next_url;
    }
}
