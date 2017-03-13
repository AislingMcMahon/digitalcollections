package com.aisling.digitalcollections;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Ais on 15/02/2017.
 */

public class User extends AppCompatActivity {

    private int id;
    private String userName;
    private String password;
    public ArrayList<Folder> folders = new ArrayList<Folder>();
    public boolean[] selectedFolders = new boolean[folders.size()];
    public boolean loggedIn;
    public ArrayList<String> folderNames = new ArrayList<>();
    DigitalCollectionsDbHelper mDbHelper = new DigitalCollectionsDbHelper(User.this);

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
        boolean[] tempSelectedFolders = new boolean[this.folders.size()];
        for(int i=0;i<selectedFolders.length;i++)
        {
            tempSelectedFolders[i] = selectedFolders[i];
        }
        selectedFolders = tempSelectedFolders;
    }


}
