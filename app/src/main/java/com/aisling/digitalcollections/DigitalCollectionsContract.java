package com.aisling.digitalcollections;

import android.provider.BaseColumns;

/**
 * Created by Donal O'Sullivan on 20/03/2016.
 */
public final class DigitalCollectionsContract {

    private final String TAG = DigitalCollectionsContract.class.getSimpleName();

    // Blank constructor to prevent the class being instantiated
    public DigitalCollectionsContract(){}

    // Useful reusable values
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String PRIMARY_KEY_CONSTRAINT = " PRIMARY KEY";
    private static final String COMMA_SEP = ",";
    private static final String OPENING_PAREN = " (";
    private static final String CLOSING_PAREN = " )";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String DEFAULT = " DEFAULT";
    private static final String CURRENT_TIMESTAMP = " CURRENT_TIMESTAMP";
    private static final String FOREIGN_KEY_CONSTRAINT = " FOREIGN KEY";
    private static final String REFERENCES = " REFERENCES ";

    // Inner class that defines the query table inner contents
    public static abstract class CollectionQuery implements BaseColumns {
        // Table and Column names
        public static final String TABLE_NAME = "query";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_TIME = "time";
    }

    // SQL commands for query table
    public static final String SQL_CREATE_QUERIES =
                CREATE_TABLE + CollectionQuery.TABLE_NAME + OPENING_PAREN +
                CollectionQuery._ID + INTEGER_TYPE + PRIMARY_KEY_CONSTRAINT + COMMA_SEP +
                CollectionQuery.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                CollectionQuery.COLUMN_NAME_TIME + DATETIME_TYPE + DEFAULT + CURRENT_TIMESTAMP + CLOSING_PAREN;

    public static final String SQL_DELETE_QUERIES =
            DROP_TABLE + CollectionQuery.TABLE_NAME;

    // Inner class that defines the query table inner contents
    public static abstract class CollectionBookmark implements BaseColumns {
        // Table and Column names
        public static final String TABLE_NAME = "bookmark";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_FOLDER_NUMBER = "folderNumber";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_GENRE = "genre";
        public static final String COLUMN_NAME_TIME = "time";
    }

    // SQL commands for bookmark table
    public static final String SQL_CREATE_BOOKMARKS =
                CREATE_TABLE + CollectionBookmark.TABLE_NAME + OPENING_PAREN +
                CollectionBookmark._ID + INTEGER_TYPE + PRIMARY_KEY_CONSTRAINT + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_PID + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_FOLDER_NUMBER + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_GENRE + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_TIME + DATETIME_TYPE + DEFAULT + CURRENT_TIMESTAMP + CLOSING_PAREN;

    public static final String SQL_DELETE_BOOKMARKS =
            DROP_TABLE + CollectionBookmark.TABLE_NAME;

    /**
     * Created by Aisling on 03/03/2017.
     */
    public static abstract class CollectionUsers implements BaseColumns{
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    public static final String SQL_CREATE_USERS =
            CREATE_TABLE + CollectionUsers.TABLE_NAME + OPENING_PAREN +
            CollectionUsers.COLUMN_NAME_EMAIL + TEXT_TYPE + PRIMARY_KEY_CONSTRAINT + COMMA_SEP +
            CollectionUsers.COLUMN_NAME_PASSWORD + TEXT_TYPE + CLOSING_PAREN;

    public static final String SQL_DELETE_USERS =
            DROP_TABLE + CollectionUsers.TABLE_NAME;

    public static abstract class CollectionFolders implements BaseColumns{
        public static final String TABLE_NAME = "folders";
        public static final String COLUMN_NAME_FOLDER_NAME = "folder_name";
        public static final String COLUMN_NAME_USER_ID = "user_id";
    }

    public static final String SQL_CREATE_FOLDERS =
            CREATE_TABLE + CollectionFolders.TABLE_NAME + OPENING_PAREN +
            CollectionFolders._ID + INTEGER_TYPE + PRIMARY_KEY_CONSTRAINT + COMMA_SEP +
            CollectionFolders.COLUMN_NAME_FOLDER_NAME + TEXT_TYPE + COMMA_SEP +
            CollectionFolders.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    FOREIGN_KEY_CONSTRAINT + OPENING_PAREN + CollectionFolders.COLUMN_NAME_USER_ID + CLOSING_PAREN +
                    REFERENCES + CollectionUsers.TABLE_NAME + OPENING_PAREN + CollectionUsers.COLUMN_NAME_EMAIL + CLOSING_PAREN + CLOSING_PAREN;

    public static final String SQL_DELETE_FOLDERS =
            DROP_TABLE + CollectionFolders.TABLE_NAME;

    public static abstract class CollectionContains implements BaseColumns{
        public static final String TABLE_NAME = "contains";
        public static final String COLUMN_NAME_DOC_ID = "document_id";
        public static final String COLUMN_NAME_FOLDER_ID = "folder_id";
    }

    public static final String SQL_CREATE_CONTAINS =
            CREATE_TABLE + CollectionContains.TABLE_NAME + OPENING_PAREN +
            CollectionContains.COLUMN_NAME_DOC_ID + INTEGER_TYPE + COMMA_SEP+
            CollectionContains.COLUMN_NAME_FOLDER_ID + INTEGER_TYPE + COMMA_SEP +
            FOREIGN_KEY_CONSTRAINT + OPENING_PAREN + CollectionContains.COLUMN_NAME_DOC_ID + CLOSING_PAREN +
                    REFERENCES + CollectionBookmark.TABLE_NAME + OPENING_PAREN + CollectionBookmark._ID + CLOSING_PAREN + COMMA_SEP+
            FOREIGN_KEY_CONSTRAINT + OPENING_PAREN + CollectionContains.COLUMN_NAME_FOLDER_ID + CLOSING_PAREN +
                    REFERENCES + CollectionFolders.TABLE_NAME + OPENING_PAREN + CollectionFolders._ID + CLOSING_PAREN + COMMA_SEP +
                    PRIMARY_KEY_CONSTRAINT + OPENING_PAREN + CollectionContains.COLUMN_NAME_FOLDER_ID + COMMA_SEP + CollectionContains.COLUMN_NAME_DOC_ID + CLOSING_PAREN
                    + CLOSING_PAREN;

    public static final String SQL_DELETE_CONTAINS =
            DROP_TABLE + CollectionContains.TABLE_NAME;

}
