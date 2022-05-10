package com.photogalleryapp.app.model.search;

import java.util.Date;

public class Search {
    public Date startDate;
    public Date endDate;
    public String keyword;
    public Double lat;
    public Double lon;

    public Search (Date sdate, Date edate, String kword, Double lat, Double lon){
        startDate = sdate;
        endDate = edate;
        keyword = kword;
        this.lat = lat;
        this.lon = lon;
    }
}
