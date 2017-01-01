package com.povio.weatherapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector {
    private static final String DATABASE_NAME = "Database";
    private static final int DATABASE_VERSION = 1;

    // database object
    private SQLiteDatabase database;

    // database helper
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Opens the database connection
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = databaseOpenHelper.getWritableDatabase();
    }

    /**
     * Closes the database connection
     */
    public void close() {
        if (database != null) database.close(); // close the database connection
    }

    /**
     * Adds a new contact to the database
     *
     * @param name
     */
    public void insertContact(String name) {
        final ContentValues newContact = new ContentValues();
        newContact.put("name", name);

        open();
        database.insert("cities", null, newContact);
        close();
    }

    /**
     * Updates given contact in the database using provided values
     *
     * @param id
     * @param name
     */
    public void updateContact(long id, String name) {
        final ContentValues editContact = new ContentValues();
        editContact.put("name", name);

        open();
        database.update("cities", editContact, "_id=" + id, null);
        close();
    }

    /**
     * Returns a Cursor with all contact information in the database
     * <p/>
     * The database connection object has to be open before calling
     * this method and closed afterwards.
     *
     * @return
     */
    public Cursor getAllContacts() {
        return database.query("cities", new String[]{"_id", "name"},
                null, null, null, null, "name");
    }

    /**
     * Returns a Cursor containing all information about the contact specified by the given id
     * <p/>
     * The database connection object has to be open before calling
     * this method and closed afterwards.
     *
     * @param id Contact's id
     * @return
     */
    public Cursor getOneContact(long id) {
        return database.query("cities", null, "_id=" + id, null, null, null, null);
    }

    /**
     * Deletes the contact specified by the given id
     * <p/>
     * The database connection object has to be open before calling
     * this method and closed afterwards.
     *
     * @param id Contact's id
     */
    public void deleteContact(long id) {
        open();
        database.delete("cities", "_id=" + id, null);
        close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /**
         * Creates the contacts table when the database is created
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            final String createQuery = "CREATE TABLE cities" +
                    "(_id integer primary key autoincrement, " +
                    "name TEXT);";

            final String insertValues = "INSERT INTO cities " +
                    "(_ID, name) values " +
                    "(NULL, 'Ljubljana');";

            db.execSQL(createQuery);
            db.execSQL(insertValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Executed each time the DB version is changed
        }
    }
}
