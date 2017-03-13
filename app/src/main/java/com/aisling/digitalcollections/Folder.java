package com.aisling.digitalcollections;

import java.util.ArrayList;

/**
 * Created by Ais on 15/02/2017.
 */

public class Folder {

    private ArrayList<Document> docs;
    private String name;

    public Folder(String name)
    {
        this.docs = new ArrayList<Document>();
        this.name = name;
    }

    public String getFolderName()
    {
        return this.name;
    }

    public void addToFolder(Document doc)
    {
        docs.add(doc);
    }

    public boolean contains(Document doc){
        return docs.contains(doc);
    }

}
