package com.aisling.digitalcollections;

import java.util.ArrayList;

/**
 * Created by Ais on 15/02/2017.
 */

public class User {

    private int id;
    private String userName;
    private String password;
    private ArrayList<Folder> folders = new ArrayList<Folder>();
    public boolean loggedIn;

    public User(String name,String password)
    {
        this.userName=name;
        this.password=password;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public String getPassword()
    {
        return this.password;
    }

    public boolean setPassword(String password)
    {
        if(password!= this.password)
        {
            this.password = password;
            return true;
        }
        return false;
    }

    public boolean setId(int id)
    {
        this.id = id;
        return true;
    }

    public String[] getFolderNames()
    {
        String[] folderNames = new String[10]; // dummy value
        for(int i=0;i<this.folders.size();i++)
        {
            folderNames[i] = folders.get(i).getFolderName();
        }
        return folderNames;
    }

    public void addToCollection(Folder f)
    {
        folders.add(f);
    }


}
