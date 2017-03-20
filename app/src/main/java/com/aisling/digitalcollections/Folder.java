package com.aisling.digitalcollections;

import java.util.ArrayList;

/**
 * Created by Ais on 15/02/2017.
 */

public class Folder {

    private ArrayList<String> docs;
    private String name;
    private Integer id;
    private static int counter = 0;
    private int resource = R.drawable.folderblue;

    public Folder(String name)
    {
        this.docs = new ArrayList<>();
        this.name = name;
        id = counter;
        counter++;
    }

    public String getFolderName()
    {
        return this.name;
    }

    public Integer getId(){ return this.id; }

    public void addToFolder(String pid)
    {
        docs.add(pid);
    }

    public boolean contains(String doc){
        for (String s : this.docs)
        {
            if(s.equals(doc))
            {
                return true;
            }
        }
        return false;
    }

    public String getFirstElement()
    {
        return this.docs.get(0);
    }

    public String[] toArray()
    {
        String[] folderArray =  {this.name};
        return folderArray;
    }

    public boolean isEmpty(){
        return this.docs.isEmpty();
    }

    public int getFolderResource()
    {
        return this.resource;
    }

    public void setFolderResource(int colour)
    {
        switch(colour)
        {
            case 0:
                this.resource = R.drawable.folderred;
                break;
            case 1:
                this.resource = R.drawable.folderorange;
                break;
            case 2:
                this.resource = R.drawable.folderyellow;
                break;
            case 3:
                this.resource = R.drawable.foldergreen;
                break;
            case 4:
                this.resource = R.drawable.folderpink;
                break;
            default:
                this.resource = R.drawable.folderblue;
                break;
        }
    }

}
