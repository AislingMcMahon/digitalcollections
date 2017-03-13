package com.aisling.digitalcollections;

import java.util.ArrayList;

/**
 * Created by Ais on 15/02/2017.
 */

public class Folder {

    private ArrayList<String> docs;
    private String name;

    public Folder(String name)
    {
        this.docs = new ArrayList<String>();
        this.name = name;
    }

    public String getFolderName()
    {
        return this.name;
    }

    public void addToFolder(String pid)
    {
        docs.add(pid);
    }

    public boolean contains(String doc){
        return docs.contains(doc);
    }

}
