package com.iak.sinju.movieapps.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class Video {
    @SerializedName("id")
    private String id;

    @SerializedName("iso_639_1")
    private String languageCode;

    @SerializedName("iso_3166_1")
    private String nationalCode;

    @SerializedName("key")
    private String videoKey;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("size")
    private int size;

    @SerializedName("type")
    private String type;

    public String getId() {
        return id;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}
