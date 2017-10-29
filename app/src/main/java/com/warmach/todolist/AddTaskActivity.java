package com.warmach.todolist;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.warmach.todolist.data.TaskContract;


public class AddTaskActivity extends AppCompatActivity {
    private int mPriority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ((RadioButton)findViewById(R.id.radButton1)).setChecked(true);
        mPriority=1;
    }
    public void onClickAddTask(View view) {
        String input = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        if (input.length() == 0) {
            return;
        }

        ContentValues contentvalues = new ContentValues();
        contentvalues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        contentvalues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);

        Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentvalues);
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

        }
        finish();
    }

        public void onPrioritySelected(View view) {
            if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
                mPriority = 1;
            } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
                mPriority = 2;
            } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
                mPriority = 3;
            }
        }


}
