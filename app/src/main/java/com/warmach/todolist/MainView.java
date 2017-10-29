package com.warmach.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.warmach.todolist.data.TaskContract;

public class MainView extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView;
    private static final String TAG = MainView.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);


        mRecyclerView =(RecyclerView)findViewById(R.id.recyclerViewTasks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id=(int) viewHolder.itemView.getTag();

                String stringID=Integer.toString(id);
                Uri uri = TaskContract.TaskEntry.CONTENT_URI;
                uri=uri.buildUpon().appendPath(stringID).build();
                getContentResolver().delete(uri,null,null);
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID,null,MainView.this);
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskintent= new Intent(MainView.this,AddTaskActivity.class);
                startActivity(addTaskintent);
            }
        });
        getSupportLoaderManager().initLoader(TASK_LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData=null;
            @Override
            protected void onStartLoading() {
                if(mTaskData!=null){
                    deliverResult(mTaskData);
                }
                else{
                    forceLoad();
                }
            }


            @Override
            public Cursor loadInBackground() {
                try{
                    return getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,null,null,null, TaskContract.TaskEntry.COLUMN_PRIORITY);
                }catch(Exception e)
                {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
            public void deliverResult(Cursor data){
                mTaskData=data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID,null,this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
