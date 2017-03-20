package com.aisling.digitalcollections;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FoldersActivity extends AppCompatActivity {

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private FolderViewAdapter adapter;
    private DigitalCollectionsDbHelper mDbHelper;
    private User u = WelcomeActivity.u;
    public static String currentFolderName;
    public static Integer currentFolderId;
    private ArrayList<Folder> mFolders = u.folders;
    private Context mContext;
    private TextView mTextView;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        mContext = getApplicationContext();
        setUpToolbar();
        adapter = new FolderViewAdapter(FoldersActivity.this, u.folders, R.layout.item_search_result, 1, AppConstants.backgroundDark);
        mGridView = (GridView) findViewById(R.id.foldersGridView);
        btn = (Button) findViewById(R.id.newFolderButton);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                goToFolderView(position);
            }
        });
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(FoldersActivity.this, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete_folder:
                                deleteFolder(position);
                                Toast.makeText(FoldersActivity.this, "Folder deleted", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.action_change_color:
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FoldersActivity.this);
                                final ArrayList<Integer> mSelectedItems= new ArrayList<>();
                                LayoutInflater inflater = FoldersActivity.this.getLayoutInflater();
                                builder.setTitle("Pick a colour")
                                        .setItems(R.array.colours, new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which){
                                                changeColour(which,position);
                                                adapter.notifyDataSetChanged();
                                            }

                                        });
                                final Dialog d =builder.create();
                                d.show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.menu_folder_options);
                popupMenu.show();
                return true;
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                final Dialog dialog = new Dialog(FoldersActivity.this);
                dialog.setContentView(R.layout.add_dialog);
                dialog.setTitle("New Folder");
                dialog.show();
                Button btnOk = (Button) dialog.findViewById(R.id.dialog_ok);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edit = (EditText)dialog.findViewById(R.id.dialog_edit);
                        String folderName = edit.getText().toString();
                        Folder newFolder = new Folder(folderName);
                        u.addToCollection(newFolder);
                        addFolderToDatabase(newFolder);
                        dialog.dismiss();
                    }
                });
            }
        });

        mDbHelper = DigitalCollectionsDbHelper.getInstance(FoldersActivity.this);
        mProgressBar = (ProgressBar) findViewById(R.id.foldersProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        FoldersActivity.GetFoldersTask getFoldersTask = new FoldersActivity.GetFoldersTask();
        mTextView = (TextView) findViewById(R.id.suggestedTextView);
        mTextView.setVisibility(View.INVISIBLE);
        getFoldersTask.execute();
    }

    private void deleteFolder(int position) {
        // Delete from database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(
                DigitalCollectionsContract.CollectionContains.TABLE_NAME,
                DigitalCollectionsContract.CollectionContains.COLUMN_NAME_FOLDER_ID + " = ?",
                new String[]{mFolders.get(position).getId().toString()}
        );
        db.delete(
                DigitalCollectionsContract.CollectionFolders.TABLE_NAME,
                DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_FOLDER_ID + " = ?",
                new String[]{mFolders.get(position).getId().toString()}
        );
        // Delete from list
        mFolders.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = null;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            title = (TextView) f.get(toolbar);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(),"OpenSans-Light.ttf");
            title.setTypeface(font);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(AppConstants.FoldersActivityTitle);
    }

    public void changeColour(int colour, int position)
    {
        mFolders.get(position).setFolderResource(colour);
    }

    private class GetFoldersTask extends AsyncTask<Void, Void, ArrayList<Folder>> {

        @Override
        protected ArrayList<Folder> doInBackground(Void... params) {
            return getFolders();
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Folder> folders){
            mProgressBar.setVisibility(View.INVISIBLE);
            if(u.folders.isEmpty()){mTextView.setVisibility(View.VISIBLE);}
            // Add bookmarks to listView
            setListToRetrievedFolders(folders, false);
        }
    }

    private ArrayList<Folder> getFolders(){
        // Get bookmarks
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String query = "SELECT folder_name FROM folders WHERE user_id=?;";
        Cursor c = db.rawQuery(query, new String[]{u.getUserName()});


        ArrayList<Folder> folders = new ArrayList<>();
        while (c.moveToNext()){
            String folderName = c.getString(0);
            folders.add(new Folder(folderName));
        }

        c.close();

        return mFolders;
    }

    private void setListToRetrievedFolders(ArrayList<Folder> folders, boolean appendToList) {
        // if this is the first search
        if (folders.size() == 0 && adapter != null){
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        // If this is a call to append to the list and the queryId is the same as when we sent the request, we append the
        // data to the list
        else if(appendToList){
            //Log.d(TAG, "Will append to the list");
            this.mFolders.addAll(folders);
            adapter.notifyDataSetChanged();
        }
        else if(folders.size() != 0) {
            this.mFolders = folders;
            adapter = new FolderViewAdapter(FoldersActivity.this, folders, R.layout.item_search_result, 1, AppConstants.backgroundDark);
            mGridView.setAdapter(adapter);
        }
    }

    private void goToFolderView(int gridPosition){
        Intent folderViewIntent = new Intent(FoldersActivity.this, FolderView.class);
        currentFolderName = mFolders.get(gridPosition).getFolderName();
        currentFolderId = mFolders.get(gridPosition).getId();
        folderViewIntent.putExtra(AppConstants.folderTransferString, mFolders.get(gridPosition).toArray());
        startActivity(folderViewIntent);
    }


    private void addFolderToDatabase(Folder f)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_FOLDER_NAME, f.getFolderName());
        Log.d("folder name", f.getFolderName());
        values.put(DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_USER_ID, u.getUserName());
        values.put(DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_FOLDER_ID, f.getId());
        Log.d("folder id", f.getId().toString());
        db.insert(DigitalCollectionsContract.CollectionFolders.TABLE_NAME,null,values);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        mDbHelper.close();
    }

}
