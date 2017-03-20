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
    private boolean[] selectedFolders = new boolean[folders.size()];
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

    public boolean[] setTrue(int i)
    {
        selectedFolders[i] = true;
        boolean[] b = selectedFolders;
        return b;
    }

    public boolean[] getSelectedFolders()
    {
        return selectedFolders;
    }

    public void clearSelection()
    {
        for(int i=0;i<selectedFolders.length;i++)
        {
            selectedFolders[i] = false;
        }
    }

    public void addToQueries(String query)
    {
        this.queries.add(query);
    }


}
