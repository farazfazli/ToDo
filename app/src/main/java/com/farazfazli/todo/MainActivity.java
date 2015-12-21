package com.farazfazli.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_FILE = "com.farazfazli.todo.preferences";
    private static final String KEY_EDITTEXT = "key_edittext";
    private static final String CURRENT_CURSOR_POSITION = "key_currentcursorposition";
    private static final String NUMBER_OF_TODOS = "key_numberoftodos";
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private EditText mTaskField;
    private ListView mListView;
    //    private HashMap<String, String> mToDoList; TODO: Implement HashMap with task & "doing"/"done"
    private ArrayList<String> mToDoList = new ArrayList<>();
    private ArrayAdapter<String> mToDoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTaskField = (EditText) findViewById(R.id.taskField);
        mListView = (ListView) findViewById(R.id.listView);

        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        mEditor = mSharedPreferences.edit();

        mToDoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, mToDoList);
        mListView.setAdapter(mToDoAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        String currentToDo = mSharedPreferences.getString(KEY_EDITTEXT, "");
        int currentCursorPosition = mSharedPreferences.getInt(CURRENT_CURSOR_POSITION, 0);
        int numberOfToDos = mSharedPreferences.getInt(NUMBER_OF_TODOS, 0);

        if (numberOfToDos > 0 && mToDoList != null && mToDoList.size() == 0) {
            Log.i("[RECOVERED TODOS]", numberOfToDos + "");
            for (int i = 0; i < numberOfToDos; i++) {
                mToDoList.add(i, mSharedPreferences.getString("TODO_" + i, ""));
                if (mSharedPreferences.getBoolean("ISCHECKED_" + i, true)) {
                    setItemChecked((TextView) mToDoAdapter.getView(i, null, mListView), i);
                    Log.i("[STRIKE]", "Task: " + i);
                }
            }
        }

        mTaskField.setText(currentToDo);

        Log.i("[RECOVERED TEXT]", currentToDo);
        Log.i("[RECOVERED CURSOR]", currentCursorPosition + "");

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // TODO: Add list saving functionality, either locally or with Parse
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView item = (TextView) view;
                setItemChecked(item, position);
                if (mToDoList.size() < 2) {
                    Toast.makeText(getBaseContext(), "Hold to delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("[LONG TAP]", "Delete");
                TextView item = (TextView) view;
                item.setPaintFlags(item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                mListView.setItemChecked(position, false);
                mToDoList.remove(position);
                mToDoAdapter.notifyDataSetChanged();
                return false;
            }
        });

        if (currentCursorPosition >= 0) {
            mTaskField.setSelection(currentCursorPosition);
        }

        mTaskField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mTaskField.getText().toString().length() > 2) {
                    mToDoList.add(mTaskField.getText().toString());
                    mTaskField.getText().clear();
                    return false;
                } else {
                    Toast.makeText(getBaseContext(), "ToDo too short!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditor.putString(KEY_EDITTEXT, mTaskField.getText().toString());
        mEditor.putInt(CURRENT_CURSOR_POSITION, mTaskField.getSelectionStart());
        mEditor.putInt(NUMBER_OF_TODOS, mToDoList.size());
        for (int i = 0; i < mToDoList.size(); i++) {
            mEditor.putString("TODO_" + i, mToDoList.get(i));
            if (mListView.isItemChecked(i)) {
                mEditor.putBoolean("ISCHECKED_" + i, true);
            } else {
                mEditor.putBoolean("ISCHECKED_" + i, false);
            }
        }
        mEditor.apply();
    }

    public void setItemChecked(TextView item, int position) {
        if ((!((item.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0))) {
            Log.i("[TAP]", "Strikethrough");
            item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mListView.setItemChecked(position, true);
        } else {
            // http://stackoverflow.com/questions/18881817/removing-strikethrough-from-textview
            Log.i("[TAP]", "Un-strike");
            item.setPaintFlags(item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            mListView.setItemChecked(position, false);
        }
    }
}
