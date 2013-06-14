package com.hister.mydictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;

import java.util.ArrayList;

public class MainActivity extends Activity {
    public SharedPreferences Words;
    public SharedPreferences Meanings;
    public SharedPreferences Rotate;
    SharedPreferences.Editor editorWords;
    SharedPreferences.Editor editorMeanings;
    SharedPreferences.Editor editorRotate;

    public String[] wordsA;
    public String[] meaningsA;

    public String newWord;
    public String newMeaning;
    public EditText etNewWord;
    public EditText etNewMeaning;
    public EditText etSearch;
    public ListView items;
    public int count = 0;
    public boolean fromSearch;

    ArrayList<String> arrayWords;
    ArrayList<String> arrayMeaning;
    public ArrayAdapter adapterWords;



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        setElementsId();
        setStringAllValue();
        setElementsValue();


        items.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if keyboard was up puts it down !!
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                dialogMeaning(fromSearch, position);
//                fromSearch = false;
            }
        });


//        etSearch.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on key press
//                    dialogMeaning(2);
//                    return true;
//                }
//                return false;
//            }
//        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchByKey(etSearch.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etSearch.getText().length() == 0) {
                    fromSearch = false;
                    setElementsValue();
                }
                else {
                    searchByKey(etSearch.getText().toString());
                }
            }
        });

    }

    void dialogMeaning(boolean fromSearch, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (fromSearch) {
            builder.setMessage(arrayMeaning.get(position));
        }
        else {
            builder.setMessage(meaningsA[position]);
        }
        builder.setIcon(android.R.drawable.ic_dialog_info);

        AlertDialog alert = builder.create();
        alert.show();

        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(28);
    }

    public void setElementsId() {
        Words = getSharedPreferences("Words", 0);
        Meanings = getSharedPreferences("Meanings", 0);
        Rotate = getSharedPreferences("Rotate", 0);
        editorWords = Words.edit();
        editorMeanings = Meanings.edit();
        editorRotate = Rotate.edit();

        items = (ListView) findViewById(R.id.listView);
        etSearch = (EditText) findViewById(R.id.etSearch);

        arrayWords = new ArrayList<String>();
        arrayMeaning = new ArrayList<String>();
        adapterWords = new ArrayAdapter(MainActivity.this, R.layout.listview_row, arrayWords);

        wordsA = new String[1000];
        meaningsA = new String[1000];

        String countStr = Words.getString("count", "0");
        count = Integer.parseInt(countStr);
    }


    public void setElementsValue() {
        if (count > 0) {
            arrayWords.clear();
            arrayMeaning.clear();
            for (int i = 0; i < count; i++) {
                arrayWords.add(wordsA[i]);
                arrayMeaning.add(meaningsA[i]);
           }
        }
        adapterWords.notifyDataSetChanged();
        items.setAdapter(adapterWords);
    }

    //btn add new word
    public void AddNew(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(inflater.inflate(R.layout.dialog_addnew, null))
                .setPositiveButton(R.string.save,
                        new Dialog.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {

                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create();
        d.show();
        Button theButton = d.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListener(d));

    }

    class CustomListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            if (isReady(dialog)) {
                etNewWord = (EditText) dialog.findViewById(R.id.word);
                etNewMeaning = (EditText) dialog.findViewById(R.id.meaning);
                newWord = etNewWord.getText().toString();
                newMeaning = etNewMeaning.getText().toString();
                saveNewWords();
                setElementsValue();
                dialog.dismiss();
            }
        }
    }

    public boolean isReady(Dialog dialog) {
        etNewWord = (EditText) dialog.findViewById(R.id.word);
        etNewMeaning = (EditText) dialog.findViewById(R.id.meaning);

        if (etNewWord.getText().toString().equals("")) {
            Toast.makeText(this, "The Word's Name is missing.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etNewMeaning.getText().toString().equals("")) {
            Toast.makeText(this, "The Word's Meaning is missing.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void setStringAllValue() {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                wordsA[i] = i + 1 + ".  " + Words.getString("word" + Integer.toString(i), "word" + Integer.toString(i));
                meaningsA[i] = Meanings.getString("meaning" + Integer.toString(i), "meaning" + Integer.toString(i));
            }
        }
    }

    void saveNewWords() {
        editorWords.putString("word" + Integer.toString(count), newWord);
        editorMeanings.putString("meaning" + Integer.toString(count), newMeaning);
        editorWords.putString("count", Integer.toString(count + 1));

        editorWords.apply();
        editorMeanings.apply();

        count++;
        setStringAllValue();
    }


    public void searchByKey(String key) {

        int found = 0;
        if (count > 0) {
            arrayWords.clear();
            arrayMeaning.clear();
            for (int i = 0, j = 0; i < count; i++) {
                if (wordsA[i].contains(key) || meaningsA[i].contains(key)) {
                    arrayWords.add(wordsA[i]);
                    arrayMeaning.add(meaningsA[i]);
                    found++;
                    j++;
                }
            }
            if (found > 0) {
                adapterWords.notifyDataSetChanged();
                items.setAdapter(adapterWords);
            }
        }

        fromSearch = true;

    }





    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

}
