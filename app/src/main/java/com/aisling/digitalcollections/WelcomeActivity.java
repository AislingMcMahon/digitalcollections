package com.aisling.digitalcollections;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    Button btnSignIn,btnSignUp;
    DigitalCollectionsDbHelper mDbHelper;
    public static User u;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // create a instance of SQLite Database
        mDbHelper = new DigitalCollectionsDbHelper(WelcomeActivity.this);
        // Get The Refference Of Buttons
        btnSignIn=(Button)findViewById(R.id.buttonSignIN);
        btnSignUp=(Button)findViewById(R.id.buttonSignUP);

        // Set OnClick Listener on SignUp button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /// Create Intent for SignUpActivity  abd Start The Activity
                Intent intentSignUP=new Intent(getApplicationContext(),Register.class);
                startActivity(intentSignUP);
            }
        });
    }
    // Methos to handleClick Event of Sign In Button
    public void signIn(View V)
    {
        final Dialog dialog = new Dialog(WelcomeActivity.this);
        dialog.setContentView(R.layout.activity_login);
        dialog.setTitle("Login");

        final SQLiteDatabase db;
        db = mDbHelper.getReadableDatabase();
        // get the References of views
        final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
        final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);

        Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);

        // Set On ClickListener
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get The User name and Password
                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();
                String storedPassword = "";

                // fetch the Password form database for respective user name
                String query= "SELECT password FROM USERS WHERE email=?;";
                Cursor c = db.rawQuery(query, new String[] {userName});
                if(c.moveToFirst()){
                    storedPassword = c.getString(0);
                }


                u = new User(userName,password);
                initialiseUser(u); //initialise this User from the DB

                // check if the Stored password matches with  Password entered by user
                if(password.equals(storedPassword))
                {
                    Toast.makeText(WelcomeActivity.this, "Congrats: Login Successful", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    u.loggedIn=true;
                    Intent intentSignIn = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intentSignIn);
                }
                else
                {
                    Toast.makeText(WelcomeActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        mDbHelper.close();
    }

    public void initialiseUser(User u)
    {
        final SQLiteDatabase db;
        db = mDbHelper.getReadableDatabase();
        //load the folder names into this user
        String query = "SELECT folder_name FROM FOLDERS WHERE user_id=?";
        Cursor c = db.rawQuery(query, new String[] {u.getUserName()});
        if(c.moveToFirst())
        {
            do {
                u.folderNames.add(c.getString(0));
                Folder newF = new Folder(c.getString(0));
                u.folders.add(newF);
                Log.d(u.folderNames.get(0), "folder name");
            }while(c.moveToNext());
        }

        //load documents into folders
        for(Folder f : u.folders)
        {
            String query2 = "SELECT document_id FROM CONTAINS WHERE folder_id=?";
            Cursor c2 = db.rawQuery(query2, new String[]{f.getFolderName()});
            if(c2.moveToFirst()){
                do{
                    f.addToFolder(c2.getString(0));
                }while (c2.moveToNext());
            }
        }
    }
}

