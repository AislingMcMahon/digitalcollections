package com.aisling.digitalcollections;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    public String[] folderNames;
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
        if(!folders.isEmpty()){
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String query = "SELECT folder_name FROM FOLDERS WHERE user_id=?";
            Cursor c = db.rawQuery(query, new String[] {userName});
            if(c.moveToFirst())
            {
                int i =0;
                do{
                    folderNames[i] = c.getString(i);
                    i++;
                }while(c.moveToNext());
            }
            else
            {
                folderNames[0] = "";
            }
        /*if(folders.isEmpty())
        {
            String[] empty = new String[1];
            empty[0] = "";
            return empty;
        }
        folderNames = new String[this.folders.size()];
        for(int i=0;i<this.folders.size();i++)
        {
            folderNames[i] = folders.get(i).getFolderName();
        }*/
            return folderNames;
        }
        else
        {
            String[] empty = new String[1];
            empty[0] = "";
            return empty;
        }

    }

    public void addToCollection(Folder f)
    {
        folders.add(f);
    }


}
