package com.aisling.digitalcollections;

import java.util.ArrayList;

/**
 * Created by Ais on 15/02/2017.
 */

public class User {

    private String userName;
    private String password;
    public ArrayList<Folder> folders = new ArrayList<>();
    public ArrayList<String> queries = new ArrayList<>();
    public boolean loggedIn;
    public ArrayList<String> folderNames = new ArrayList<>();


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


    public String[] getFolderNames()
    {

        String[] returnFolderNames = new String[folders.size()];
        for(int i=0; i<folders.size();i++)
        {
            returnFolderNames[i] = folders.get(i).getFolderName();
        }
        return returnFolderNames;
    }

    public void addToCollection(Folder f)
    {
        this.folders.add(f);
    }



}
