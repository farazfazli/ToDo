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
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private EditText mTaskField;
    private ListView mListView;
    //    private HashMap<String, String> mToDoList; TODO: Implement HashMap with task & "doing"/"done"
    private ArrayList<String> mToDoList;
    private ArrayAdapter<String> mToDoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTaskField = (EditText) findViewById(R.id.taskField);
        mListView = (ListView) findViewById(R.id.listView);
        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mSharedPreferences = getSharedPreferences(CURRENT_CURSOR_POSITION, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        String currentToDo = mSharedPreferences.getString(KEY_EDITTEXT, "");
        int currentCursorPosition = mSharedPreferences.getInt(CURRENT_CURSOR_POSITION, 0);
        mTaskField.setText(currentToDo);
        Log.i("[CURSOR]", currentCursorPosition + "");
        mToDoList = new ArrayList<String>();
        mToDoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, mToDoList);
        mListView.setAdapter(mToDoAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        // TODO: Add list saving functionality, either locally or with Parse
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("[TAP]", "Strikethrough");
                TextView item = (TextView) view;
                if (!((item.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG ) > 0)){
                    item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    mListView.setItemChecked(position, true);
                } else {
                    // http://stackoverflow.com/questions/18881817/removing-strikethrough-from-textview
                    item.setPaintFlags(item.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    mListView.setItemChecked(position, false);
                    Log.i("[TAP]", "Un-strike");
                }
                if(mToDoList.size() < 2) {
                    Toast.makeText(getBaseContext(), "Hold to delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("[LONG TAP]", "Delete");
                TextView item = (TextView) view;
                item.setPaintFlags(item.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
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
                mToDoList.add(mTaskField.getText().toString());
                mTaskField.getText().clear();
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditor.putString(KEY_EDITTEXT, mTaskField.getText().toString());
        mEditor.putInt(CURRENT_CURSOR_POSITION, mTaskField.getSelectionStart());
        mEditor.apply();
    }
}
