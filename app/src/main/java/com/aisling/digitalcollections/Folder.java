package com.aisling.digitalcollections;

import java.util.ArrayList;

/**
 * Created by Ais on 15/02/2017.
 */

public class Folder {

    private ArrayList<String> urls;
    private String name;

    public Folder(String name)
    {
        this.urls = new ArrayList<String>();
        this.name = name;
    }

    public String getFolderName()
    {
        return this.name;
    }

    public void addToFolder(String url)
    {
        urls.add(url);
    }

}
