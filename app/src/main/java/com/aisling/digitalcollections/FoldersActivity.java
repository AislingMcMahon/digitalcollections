package com.aisling.digitalcollections;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class FoldersActivity extends AppCompatActivity {

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private FolderViewAdapter adapter;
    private DigitalCollectionsDbHelper mDbHelper;
    private User u = WelcomeActivity.u;
    public static String currentFolderName;
    private ArrayList<Folder> mFolders = u.folders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        //setUpToolbar();
        mGridView = (GridView) findViewById(R.id.foldersGridView);
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
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.menu_folder_options);
                popupMenu.show();
                return true;
            }
        });

        mDbHelper = DigitalCollectionsDbHelper.getInstance(FoldersActivity.this);
        mProgressBar = (ProgressBar) findViewById(R.id.foldersProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        FoldersActivity.GetFoldersTask getFoldersTask = new FoldersActivity.GetFoldersTask();
        getFoldersTask.execute();
    }

    private void deleteFolder(int position) {
        // Delete from database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(
                DigitalCollectionsContract.CollectionFolders.TABLE_NAME,
                DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_FOLDER_NAME + " = ?",
                new String[]{mFolders.get(position).getFolderName()}
        );
        // Delete from list
        mFolders.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(AppConstants.FoldersActivityTitle);
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
        folderViewIntent.putExtra(AppConstants.folderTransferString, mFolders.get(gridPosition).toArray());
        startActivity(folderViewIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        mDbHelper.close();
    }

}
