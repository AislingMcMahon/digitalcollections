package com.aisling.digitalcollections;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Ais on 14/03/2017.
 */

public class FolderView extends AppCompatActivity {

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ArrayList<Document> docs;
    private SearchResultsAdapter adapter;
    private DigitalCollectionsDbHelper mDbHelper;
    private User u = WelcomeActivity.u;
    private String folderName = FoldersActivity.currentFolderName;
    private Integer folderId = FoldersActivity.currentFolderId;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_folder_view);
        setUpToolbar();
        docs = new ArrayList<>();
        mGridView = (GridView) findViewById(R.id.folderGridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                goToDocumentView(position);
            }
        });
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(FolderView.this, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete_bookmark:
                                deleteBookmark(position);
                                Toast.makeText(FolderView.this, " Document removed from this folder", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.menu_bookmark_options);
                popupMenu.show();
                return true;
            }
        });

        mDbHelper = DigitalCollectionsDbHelper.getInstance(FolderView.this);
        mProgressBar = (ProgressBar) findViewById(R.id.foldersProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        FolderView.GetDocumentsTask task = new FolderView.GetDocumentsTask();
        task.execute();
    }

    private void deleteBookmark(int position) {
        // Delete from database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(
                DigitalCollectionsContract.CollectionContains.TABLE_NAME,
                DigitalCollectionsContract.CollectionContains.COLUMN_NAME_DOC_ID + " = ?" + " AND "
                        + DigitalCollectionsContract.CollectionContains.COLUMN_NAME_FOLDER_ID + " = ?",
                new String[]{docs.get(position).getPid(),folderId.toString()}
        );
        // Delete from list
        docs.remove(position);
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
        getSupportActionBar().setTitle(capitalize(folderName));
    }

    private class GetDocumentsTask extends AsyncTask<Void, Void, ArrayList<Document>> {

        @Override
        protected ArrayList<Document> doInBackground(Void... params) {
            return getDocuments();
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Document> bookmarks){
            mProgressBar.setVisibility(View.INVISIBLE);
            // Add bookmarks to listView
            setListToRetrievedDocuments(bookmarks, false);
        }
    }

    private ArrayList<Document> getDocuments(){
        // Get bookmarks
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String query = "SELECT pid, folderNumber, title, genre FROM" +
                " bookmark, contains WHERE document_id=pid AND folder_id=?;";
        Cursor c = db.rawQuery(query, new String[]{folderId.toString()});

        int indexPid = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID);
        int indexFolder = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_FOLDER_NUMBER);
        int indexTitle = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_TITLE);
        int indexGenre = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_GENRE);

        ArrayList<Document> bookmarks = new ArrayList<Document>();
        while (c.moveToNext()){
            String pid = c.getString(indexPid);
            String folder = c.getString(indexFolder);
            String title = c.getString(indexTitle);
            String genre = c.getString(indexGenre);
            bookmarks.add(new Document(pid, folder, title, genre));
        }

        c.close();

        return bookmarks;
    }

    private void setListToRetrievedDocuments(ArrayList<Document> docs, boolean appendToList) {
        // if this is the first search
        if (docs.size() == 0 && adapter != null){
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        // If this is a call to append to the list and the queryId is the same as when we sent the request, we append the
        // data to the list
        else if(appendToList == true){
            //Log.d(TAG, "Will append to the list");
            this.docs.addAll(docs);
            adapter.notifyDataSetChanged();
        }
        else if(docs.size() != 0) {
            this.docs = docs;
            adapter = new SearchResultsAdapter(FolderView.this, docs, R.layout.item_search_result, 1, AppConstants.backgroundDark);
            mGridView.setAdapter(adapter);
        }
    }

    private void goToDocumentView(int listPosition){
        Intent documentViewIntent = new Intent(FolderView.this, DocumentView.class);
        documentViewIntent.putExtra(AppConstants.documentTransferString, docs.get(listPosition).toArray());
        startActivity(documentViewIntent);
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        mDbHelper.close();
    }


}
