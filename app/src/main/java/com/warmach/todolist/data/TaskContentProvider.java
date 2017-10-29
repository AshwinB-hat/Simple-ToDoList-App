package com.warmach.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by warmachine on 29/10/17.
 */

public class TaskContentProvider extends ContentProvider{

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher urimatcher = new UriMatcher(UriMatcher.NO_MATCH);
        urimatcher.addURI(TaskContract.AUTHORITY,TaskContract.PATH_TASKS,TASKS);
        urimatcher.addURI(TaskContract.AUTHORITY,TaskContract.PATH_TASKS+"/#",TASK_WITH_ID);
        return urimatcher;
    }

    private TaskDbHelper mTaskDbhelper;




    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbhelper=new TaskDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final SQLiteDatabase db = mTaskDbhelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch(match){
            case TASKS:
                retCursor = db.query(TaskContract.TaskEntry.TABLE_NAME,null,null,null,null,null,s1);
                break;
            case TASK_WITH_ID:
                retCursor=db.query(TaskContract.TaskEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri"+uri);
        }

        return retCursor;
    }

    @Nullable

    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db=mTaskDbhelper.getWritableDatabase();
        int match=sUriMatcher.match(uri);
        Uri returnUri;
        switch(match){
            case TASKS:
                long id=db.insert(TaskContract.TaskEntry.TABLE_NAME,null,contentValues);
                if(id>0){
                    returnUri= ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI,id);

                }else{
                    throw new android.database.SQLException("Failed to insert row into"+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mTaskDbhelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case TASK_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TaskContract.TaskEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("NOT implemented yet");
    }
}
