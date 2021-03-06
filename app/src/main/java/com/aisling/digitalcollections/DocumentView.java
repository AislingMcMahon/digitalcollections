package com.aisling.digitalcollections;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DocumentView extends AppCompatActivity {

    private final String TAG = DocumentView.class.getSimpleName();

    private String[] docInfo; // Pid, DrisFolderNumber, Text, Genre, Lang, TypeOfResource
    ArrayList<String> docPageIds;
    ArrayList<CharSequence> documentMetadata; // title, origin_place, publisher, date, language, abstract, access_condition
    private PhotoViewAttacher mAttacher;
    private ImageView mImageView;
    private QueryManager mQueryManager = new QueryManager();
    private DigitalCollectionsDbHelper mDbHelper;
    private ProgressBar mProgressBar;
    private AlertDialog.Builder builder;
    private TextView mTextView;
    private TextView mTitleTextView;
    private Button mPrevButton;
    private Button mNextButton;
    private SeekBar mSeekbar;
    private TextView mSeekBarTextView;
    private int currentPageIndex;
    boolean navBarVisible = true;
    public User u = WelcomeActivity.u;
    //public ArrayList<Boolean> selectedFolders = new ArrayList<>();
    public ArrayList<Integer> mSelectedItems = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        //Retrieves doc info passed from previous activity
        docInfo = getIntent().getStringArrayExtra(AppConstants.documentTransferString);

        // Set up toolbar
        setUpToolbar();

        //Add alert dialogue builder
        builder = new AlertDialog.Builder(DocumentView.this);

        // Add progress bar to XML views and then call to make visible
        mProgressBar = (ProgressBar) findViewById(R.id.documentViewProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);


        //Get text view
        mTextView = (TextView) findViewById(R.id.docViewTextView);
        mSeekBarTextView = (TextView) findViewById((R.id.seekBarTextView));
        mTitleTextView = (TextView) findViewById(R.id.docViewTitleTextView);

        mDbHelper = DigitalCollectionsDbHelper.getInstance(DocumentView.this);

        initializeDocImage();

        // Get image by passing pid and dris folder number
        GetDocumentImage getImage = new GetDocumentImage();
        getImage.execute(docInfo[0], docInfo[1]);

        // Get image by passing dris folder number and pid
        GetCollectionPIds getCollectionPIds = new GetCollectionPIds();
        getCollectionPIds.execute(docInfo[1]);

        // Get metadata for document
        GetDocumentMetadata getDocumentMetadata = new GetDocumentMetadata();
        getDocumentMetadata.execute(docInfo[0]);


    }

    private void initializeDocImage() {
        mImageView = (ImageView) findViewById(R.id.documentViewImageView);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                toggleNavBarVisibility();
            }
        });
    }

    private void toggleNavBarVisibility() {
        if (mNextButton != null
            && mPrevButton!= null
            && mTextView!= null
            && mSeekbar!= null
            && mSeekBarTextView!= null) {

            if (navBarVisible == true) {
                mNextButton.setVisibility(View.INVISIBLE);
                mPrevButton.setVisibility(View.INVISIBLE);
                mTextView.setVisibility(View.INVISIBLE);
                mSeekbar.setVisibility(View.INVISIBLE);
                mSeekBarTextView.setVisibility(View.INVISIBLE);
                mTitleTextView.setVisibility(View.INVISIBLE);
                navBarVisible = false;
            } else {
                mNextButton.setVisibility(View.VISIBLE);
                mPrevButton.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mSeekbar.setVisibility(View.VISIBLE);
                mSeekBarTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setVisibility(View.VISIBLE);
                navBarVisible = true;
            }
        }
    }

    private void setUpNavigationButtons() {
        // Get buttons
        mNextButton = (Button) findViewById(R.id.docViewNextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 0 moves page forward
                mNextButton.setBackgroundColor(Color.parseColor(AppConstants.navigationButtonHighlightColor));
                changeDocumentPage(0);
            }
        });

        mPrevButton = (Button) findViewById(R.id.docViewPreviousButton);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1 movespage back
                mPrevButton.setBackgroundColor(Color.parseColor(AppConstants.navigationButtonHighlightColor));
                changeDocumentPage(1);
            }
        });

        // Remove Drawables for buttons if at end or start of doc
        if (currentPageIndex == 0){
            mPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Do nothing
                }
            });
            mPrevButton.setCompoundDrawables(null, null, null, null);
        }

        else if (currentPageIndex >= docPageIds.size() - 1){
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Do nothing
                }
            });
            mNextButton.setCompoundDrawables(null, null, null, null);
        }
    }

    private void setUpSeekbar(int length){
        mSeekbar = (SeekBar) findViewById(R.id.docViewSeekBar);
        mSeekbar.setBottom(0);
        mSeekbar.setMax(length - 1);
        mSeekbar.setProgress(currentPageIndex);
        mSeekBarTextView.setText(String.valueOf(currentPageIndex + 1));

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int progressVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressVal = progress;
                mSeekBarTextView.setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                goToPageWithIndex(progressVal);
            }

        });
    }

    private void goToPageWithIndex(int index) {
        String newPid = docPageIds.get(index);
        //Log.d(TAG, "New page number is " + String.valueOf(newPid));

        // Copy docInfo string array and change pid to new pid. Now this can be passed to the new docView
        String[] newDocInfo = docInfo;
        newDocInfo[0] = newPid;

        Intent newDocumentViewIntent = new Intent(this, DocumentView.class);
        newDocumentViewIntent.putExtra(AppConstants.documentTransferString, newDocInfo);

        startActivity(newDocumentViewIntent);
        finish();
    }

    private void changeDocumentPage(int movVal){


        String newPid = "";
        boolean changePage = false;

        //block until docPageIds is initialized
        while(docPageIds == null){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // move forward
        if (movVal == 0 &&
                currentPageIndex < (docPageIds.size() - 1)) {
            newPid = docPageIds.get(currentPageIndex + 1);
            changePage = true;
        } else if (currentPageIndex > 0) {
            newPid = docPageIds.get(currentPageIndex - 1);
            changePage = true;
        }

        if (changePage) {

            //Log.d(TAG, "New page number is " + String.valueOf(newPid));

            // Copy docInfo string array and change pid to new pid. Now this can be passed to the new docView
            String[] newDocInfo = docInfo;
            newDocInfo[0] = newPid;

            Intent newDocumentViewIntent = new Intent(this, DocumentView.class);
            newDocumentViewIntent.putExtra(AppConstants.documentTransferString, newDocInfo);

            startActivity(newDocumentViewIntent);
            finish();

        }

    }

    private void setUpToolbar() {
        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    // Creates an asynchronous task that gets the image for the document view
    private class GetDocumentImage extends AsyncTask<String, Integer, Bitmap> {

        protected Bitmap doInBackground(String... docInfo){
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                return mQueryManager.getImageResource(docInfo[1], docInfo[0]);
            } catch(RuntimeException e){
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPreExecute() {
            //Turn Progress indicator off
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute (Bitmap result) {
            //Turn Progress indicator off
            if(android.os.Debug.isDebuggerConnected()){
                android.os.Debug.waitForDebugger();
            }

            mProgressBar.setVisibility(View.INVISIBLE);

            // If result has been retrieved
            if (result != null) {
                mImageView.setImageBitmap(result);
                mAttacher.update();
            }

            // If no result retrieved
            else {
                builder.setMessage(R.string.network_error_message)
                        .setTitle(R.string.network_error_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                final AlertDialog networkErrorDialog = builder.create();
                networkErrorDialog.show();
            }
        }
    }

    // Creates an asynchronous task that gets the data for the document
    private class GetCollectionPIds extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... info){
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                String listQuery = mQueryManager.constructListOfObjectsInCollectionQuery(info[0]);
                //Log.d(TAG, listQuery);
                InputStream responseStream = mQueryManager.queryUrlForDataStream(listQuery);
                String response = null;
                try {
                    response = mQueryManager.readStringFromInputStream(responseStream);
                } catch (java.io.IOException e){
                    e.printStackTrace();
                }
                return response;
            } catch(RuntimeException e){
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPreExecute() {
            //Turn Progress indicator off
        }

        protected void onPostExecute (String result) {
            //Turn Progress indicator off
            if(android.os.Debug.isDebuggerConnected()){
                android.os.Debug.waitForDebugger();
            }

            // If result has been retrieved
            if (result != null) {
                //Log.d(TAG, result);
                String [] data = result.split(AppConstants.listOfObjectsInDocumentDelimeter);

                ArrayList<String> resultData = new ArrayList<String>(Arrays.asList(data));
                resultData.remove(0);

                docPageIds = resultData;

                for (int i = 0; i < docPageIds.size(); i++){
                    if (docInfo[0].equals(docPageIds.get(i))){
                        currentPageIndex = i;
                    }
                }

                //Log.d(TAG, "Current page index: " + String.valueOf(currentPageIndex));
                mTextView.setText("Page " + String.valueOf(currentPageIndex + 1) + " of " + String.valueOf(docPageIds.size()));

                setUpNavigationButtons();
                setUpSeekbar(docPageIds.size());

            }

            /*// If no result retrieved
            else {
                builder.setMessage(R.string.network_error_message)
                        .setTitle(R.string.network_error_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                final AlertDialog networkErrorDialog = builder.create();
                networkErrorDialog.show();
            }*/
        }
    }

    private class GetDocumentMetadata extends AsyncTask<String, Integer, ArrayList<CharSequence>>{

        @Override
        protected ArrayList<CharSequence> doInBackground(String... params) {
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                String metadataQuery = mQueryManager.constructDocMetadataQuery(params[0]);
                //Log.d(TAG, metadataQuery);
                InputStream responseStream = mQueryManager.queryUrlForDataStream(metadataQuery);

                // title, origin_place, publisher, date, language, abstract, access_condition
                ArrayList<CharSequence> response = null;
                try {
                    // Get response and response as string (for debugging)
                    ResponseJSONParser responseJSONParser = new ResponseJSONParser();
                    response = responseJSONParser.parseMetadata(responseStream);
                    documentMetadata = response;
                    //Add Pid to documentMetadata it will be later transfered to the detail view activity
                    documentMetadata.add(docInfo[0]);

                    /*
                    for(String s: response) {
                        Log.d(TAG, s);
                    }
                    */
                } catch (java.io.IOException e){
                    e.printStackTrace();
                }
                return response;
            } catch(RuntimeException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<CharSequence> result){

            if (result != null){

                // title, origin_place, publisher, date, language, abstract, access_condition
                mTitleTextView.setText(result.get(0));

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_document_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_detail_view:
                startDetailViewActivity();
                return true;
            case R.id.action_bookmark:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DocumentView.this);
                LayoutInflater inflater = DocumentView.this.getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.collection_dialog,null));
                if(!u.folders.isEmpty()){
                    builder.setTitle(R.string.dialog_add)
                            .setMultiChoiceItems(u.getFolderNames(),null, new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    if(isChecked)
                                    {
                                        mSelectedItems.add(which);
                                    }

                                }
                            })
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog,int id){
                                    addToFolders();
                                    //BookmarkDocTask bookmarkDocTask = new BookmarkDocTask();
                                    //bookmarkDocTask.execute();
                                    mSelectedItems.clear();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    mSelectedItems.clear();


                                }
                            });

                }
                else
                {
                    builder.setTitle(R.string.dialog_add)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    mSelectedItems.clear();


                                }
                            });
                }
                final Dialog d = builder.create();
                d.show();
                Button addBtn = (Button) d.findViewById(R.id.addButton);
                addBtn.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        d.dismiss();
                        final Dialog dialog = new Dialog(DocumentView.this);
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
                                mSelectedItems.add(u.folders.size()-1);
                                addToFolders();
                                //BookmarkDocTask bookmarkDocTask = new BookmarkDocTask();
                                //bookmarkDocTask.execute();
                                mSelectedItems.clear();
                                dialog.dismiss();
                            }
                        });


                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startDetailViewActivity(){
        if(documentMetadata != null) {
            // go to to detail activity
            Intent documentDetailViewIntent = new Intent(this, DocumentDetailViewActivity.class);
            documentDetailViewIntent.putCharSequenceArrayListExtra(AppConstants.documentDetailTransferString, documentMetadata);
            startActivity(documentDetailViewIntent);
        }
    }

    private class BookmarkDocTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            return addToFolders();
        }

        @Override
        protected void onPostExecute(Boolean bookmarked){
            if(bookmarked){
                Toast.makeText(DocumentView.this, "Document saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DocumentView.this, "Document already in selected folders", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*private boolean bookmarkDoc(){

        while (documentMetadata ==  null){}

        SQLiteDatabase db;// Read out database
        db = mDbHelper.getReadableDatabase();

        String[] projection = {DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID};
        String selection = DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID;
        String[] selectionVals = {docInfo[0]};
        String sortOrder = DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_TIME + " DESC";
        String query = "SELECT pid FROM bookmark WHERE pid=\"" + docInfo[0] + "\" ORDER BY time DESC";

        Cursor c = db.rawQuery(query, null);

        int index = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID);

        if(c.moveToNext()){
            return false;
        }

        else {
            db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID, docInfo[0]);
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_FOLDER_NUMBER, docInfo[1]);
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_TITLE, (String) documentMetadata.get(0));
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_GENRE, docInfo[3]);
            long newRowId = db.insert(DigitalCollectionsContract.CollectionBookmark.TABLE_NAME, null, values);
            return true;
        }
    }*/

    private boolean addToFolders()
    {
        ArrayList<Folder> folders = u.folders;
        String doc = docInfo[0];
        for(int i: mSelectedItems){
            Folder f = folders.get(i);
            f.addToFolder(doc);
            addToDatabase(doc,f);
        }
        Toast.makeText(DocumentView.this, "Document saved!", Toast.LENGTH_SHORT).show();
        return true;

    }

    private void addToDatabase(String doc, Folder f)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if(!inBookmarks(doc))
        {
            ContentValues values = new ContentValues();
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID, docInfo[0]);
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_FOLDER_NUMBER, docInfo[1]);
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_TITLE, (String) documentMetadata.get(0));
            values.put(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_GENRE, docInfo[3]);
            db.insert(DigitalCollectionsContract.CollectionBookmark.TABLE_NAME, null, values);
        }
        ContentValues values = new ContentValues();
        values.put(DigitalCollectionsContract.CollectionContains.COLUMN_NAME_DOC_ID, doc);
        values.put(DigitalCollectionsContract.CollectionContains.COLUMN_NAME_FOLDER_ID, f.getId());
        db.insert(DigitalCollectionsContract.CollectionContains.TABLE_NAME,null,values);

    }

    private  boolean inBookmarks(String doc)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID};
        String selection = DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID;
        String[] selectionVals = {docInfo[0]};
        String sortOrder = DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_TIME + " DESC";
        String query = "SELECT pid FROM bookmark WHERE pid=\"" + doc + "\" ORDER BY time DESC";

        Cursor c = db.rawQuery(query, null);

        int index = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID);

        if(c.moveToNext()){
            return true;
        }
        return false;
    }

    private void addFolderToDatabase(Folder f)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_FOLDER_NAME, f.getFolderName());
        values.put(DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_USER_ID, u.getUserName());
        values.put(DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_FOLDER_ID, f.getId());
        values.put(DigitalCollectionsContract.CollectionFolders.COLUMN_NAME_FOLDER_COLOUR,R.drawable.folderblue);
        db.insert(DigitalCollectionsContract.CollectionFolders.TABLE_NAME,null,values);
    }
}

