package com.hister.mydictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

    public String newWord;
    public String newMeaning;
    public String newWordEdit;
    public String newMeaningEdit;

    public EditText etNewWord;
    public EditText etNewMeaning;
    public EditText etSearch;
    public ListView items;
    public int count = 0;
    public boolean isFromSearch;

    ArrayList<String> arrayWords;
    ArrayList<String> arrayWordsToShow;
    ArrayList<String> arrayMeaning;

    ArrayList<String> arrayWordsSearch;
    ArrayList<String> arrayMeaningSearch;

    public ArrayAdapter adapterWords;
    public AlertDialog dialogAddNew;
    public AlertDialog dialogEdit;
    public AlertDialog dialogMeaning;
    public AlertDialog dialogAskDelete;

    int listViewPosition = 0;
    boolean dialogAddNewIsOpen = false;

    boolean dialogMeaningIsOpen = false;
    String dialogMeaningText = null;
    int dialogMeaningWordPosition = 0;
    int dialogEditWordPosition = 0;
    boolean dialogEditIsOpen = false;
    boolean dialogAskDeleteIsOpen = false;


    String searchMethod;
//    String editTextPreference;
//    String ringtonePreference;
//    String secondEditTextPreference;
//    String customPref;

    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        searchMethod = prefs.getString("prefSearchMethod", "wordsAndMeanings");
//        editTextPreference = prefs.getString("editTextPref",
//                "Nothing has been entered");
//        ringtonePreference = prefs.getString("ringtonePref",
//                "DEFAULT_RINGTONE_URI");
//        secondEditTextPreference = prefs.getString("SecondEditTextPref",
//                "Nothing has been entered");
//        // Get the custom preference
//        SharedPreferences mySharedPreferences = getSharedPreferences(
//                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
//        customPref = mySharedPreferences.getString("myCusomPref", "");
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        setElementsId();
        setElementsValue();
        setElementsValue();
        getPrefs();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        if (icicle != null) {
            listViewPosition = icicle.getInt("listViewPosition");
            dialogAddNewIsOpen = icicle.getBoolean("dialogAddNewIsOpen");
            dialogMeaningIsOpen = icicle.getBoolean("dialogMeaningIsOpen");
            dialogEditIsOpen = icicle.getBoolean("dialogEditIsOpen");
            dialogAskDeleteIsOpen = icicle.getBoolean("dialogAskDeleteIsOpen");
        }
        if (dialogAddNewIsOpen) {
            dialogAddNew();
            EditText wordAddNew = (EditText) dialogAddNew.findViewById(R.id.word);
            EditText meaningAddNew = (EditText) dialogAddNew.findViewById(R.id.meaning);
            wordAddNew.setText(icicle.getString("editTextWordAddNew"));
            meaningAddNew.setText(icicle.getString("editTextMeaningAddNew"));
        }
        if (dialogMeaningIsOpen) {
            dialogMeaningText = icicle.getString("dialogMeaningText");
            dialogMeaningWordPosition = icicle.getInt("dialogMeaningWordPosition");
            isFromSearch = icicle.getBoolean("dialogMeaningIsFromSearch");
            dialogMeaning(isFromSearch, dialogMeaningWordPosition);
        }
        if (dialogEditIsOpen) {
            dialogMeaningWordPosition = icicle.getInt("dialogMeaningWordPosition");
            isFromSearch = icicle.getBoolean("dialogMeaningIsFromSearch");
            dialogEdit(isFromSearch, dialogMeaningWordPosition);
            EditText wordAddNew = (EditText) dialogEdit.findViewById(R.id.word);
            EditText meaningAddNew = (EditText) dialogEdit.findViewById(R.id.meaning);
            wordAddNew.setText(icicle.getString("dialogEditWordText"));
            meaningAddNew.setText(icicle.getString("dialogEditMeaningText"));
        }
        if (dialogAskDeleteIsOpen) {
            dialogAskDelete();
            newWordEdit = icicle.getString("dialogEditWordText");
            newMeaningEdit = icicle.getString("dialogEditMeaningText");

        }


        items.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if keyboard was up puts it down !!
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                dialogMeaning(isFromSearch, position);
            }
        });


        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(etSearch.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    isFromSearch = false;
                    setElementsValue();
                } else {
                    search(etSearch.getText().toString());
                }
            }
        });


//        class MyAdapter extends ArrayAdapter<String> {
//
//            public MyAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> objects) {
//                super(context, resource, textViewResourceId, objects);
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//
//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View row = inflater.inflate(R.layout.row_with_button, parent, false);
//
//                Button btnEdit = (Button) row.findViewById(R.id.btnEdit);
//                TextView tvText = (TextView) row.findViewById(R.id.tvText);
//
//                tvText.setText(arrayWords.get(position));
//
//
//                return row;
//            }
//        }
//        items.setAdapter(new MyAdapter(this, android.R.layout.simple_expandable_list_item_1, R.id.tvText, arrayWords));


    }


    void dialogMeaning(boolean fromSearch, int position) {
        final boolean isFromSearchForEdit = fromSearch;
        final int positionForEdit = position;
        dialogMeaningWordPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (fromSearch) {
            builder.setMessage(arrayMeaningSearch.get(position));
            dialogMeaningText = arrayMeaningSearch.get(position);
        } else {
            builder.setMessage(arrayMeaning.get(position));
            dialogMeaningText = arrayMeaning.get(position);
        }
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogEdit(isFromSearchForEdit, positionForEdit);
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialogMeaning = builder.create();
        dialogMeaning.show();

        TextView textView = (TextView) dialogMeaning.findViewById(android.R.id.message);
        textView.setTextSize(28);
    }


    void dialogEdit(boolean fromSearch, int position) {
        final int positionF = position;
        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_addnew, null);
        final AlertDialog.Builder d = new AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton(R.string.save,
                        new Dialog.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {

                            }

                        })
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogEditWordPosition = positionF;
                        EditText dialogEditWord = (EditText) dialogEdit.findViewById(R.id.word);
                        EditText dialogEditMeaning = (EditText) dialogEdit.findViewById(R.id.meaning);
                        newWordEdit = dialogEditWord.getText().toString();
                        newMeaningEdit = dialogEditMeaning.getText().toString();
                        dialogEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        dialogAskDelete();

                    }
                })
                .setNegativeButton(R.string.close, null);

        dialogEdit = d.create();
        dialogEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etNewWord = (EditText) layout.findViewById(R.id.word);
        etNewMeaning = (EditText) layout.findViewById(R.id.meaning);
        if (fromSearch) {
            etNewWord.setText(Words.getString("word" + Integer.toString(position), "word" + Integer.toString(position)));
            etNewMeaning.setText(arrayMeaning.get(position));
        } else {
            etNewWord.setText(Words.getString("word" + Integer.toString(position), "word" + Integer.toString(position)));
            etNewMeaning.setText(arrayMeaning.get(position));
        }

        dialogEdit.show();
        dialogEditWordPosition = position;


                Button theButton = dialogEdit.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListenerEdit(dialogEdit));
    }


    class CustomListenerEdit implements View.OnClickListener {
        private final Dialog dialog;

        public CustomListenerEdit(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            if (isReadyEdit(dialog)) {
                etNewWord = (EditText) dialog.findViewById(R.id.word);
                etNewMeaning = (EditText) dialog.findViewById(R.id.meaning);
                newWordEdit = etNewWord.getText().toString();
                newMeaningEdit = etNewMeaning.getText().toString();

                editorWords.putString("word" + dialogMeaningWordPosition, newWordEdit);
                editorMeanings.putString("meaning" + dialogMeaningWordPosition, newMeaningEdit);
                editorWords.commit();
                editorMeanings.commit();

                setElementsValue();
                setElementsValue();
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Successfully edited.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    void dialogAskDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ask To Delete");
        builder.setMessage("Are you sure you want to delete this word ?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editorWords.remove("word" + dialogMeaningWordPosition);
                editorMeanings.remove("meaning" + dialogMeaningWordPosition);
                editorWords.commit();
                editorMeanings.commit();
                arrayWords.remove(dialogEditWordPosition);
                arrayWordsToShow.remove(dialogEditWordPosition);
                arrayMeaning.remove(dialogEditWordPosition);
                count--;
                refreshList();
                setElementsValue();

                Toast.makeText(MainActivity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogEdit(isFromSearch, dialogEditWordPosition);
                EditText wordAddNew = (EditText) dialogEdit.findViewById(R.id.word);
                EditText meaningAddNew = (EditText) dialogEdit.findViewById(R.id.meaning);
                wordAddNew.setText(newWordEdit);
                meaningAddNew.setText(newMeaningEdit);
            }
        });
        dialogAskDelete = builder.create();
        dialogAskDelete.show();
    }


    void dialogAddNew() {
        LayoutInflater inflater = this.getLayoutInflater();
        dialogAddNew = new AlertDialog.Builder(this)
                .setView(inflater.inflate(R.layout.dialog_addnew, null))
                .setPositiveButton(R.string.save,
                        new Dialog.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {

                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialogAddNew.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialogAddNew.show();
        Button theButton = dialogAddNew.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListenerAddNew(dialogAddNew));
    }


    class CustomListenerAddNew implements View.OnClickListener {
        private final Dialog dialog;

        public CustomListenerAddNew(Dialog dialog) {
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
                Toast.makeText(MainActivity.this, "Successfully added.", Toast.LENGTH_SHORT).show();
            }
        }
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
        arrayWordsToShow = new ArrayList<String>();
        arrayMeaning = new ArrayList<String>();

        arrayWordsSearch = new ArrayList<String>();
        arrayMeaningSearch = new ArrayList<String>();

        adapterWords = new ArrayAdapter(MainActivity.this, R.layout.listview_row, arrayWordsToShow);


        String countStr = Words.getString("count", "0");
        count = Integer.parseInt(countStr);

        dialogAddNew = new AlertDialog.Builder(this).create();
        dialogEdit = new AlertDialog.Builder(this).create();
        dialogMeaning = new AlertDialog.Builder(this).create();
        dialogAskDelete = new AlertDialog.Builder(this).create();
    }


    public void setElementsValue() {
        if (count > 0) {
            arrayWords.clear();
            arrayWordsToShow.clear();
            arrayMeaning.clear();
            for (int i = 0; i < count; i++) {
                arrayWords.add(Words.getString("word" + Integer.toString(i), "word" + Integer.toString(i)));
                arrayMeaning.add(Meanings.getString("meaning" + Integer.toString(i), "meaning" + Integer.toString(i)));

                arrayWordsToShow.add(i + 1 + ".  " + Words.getString("word" + Integer.toString(i), "word" + Integer.toString(i)));
            }
        }
        adapterWords.notifyDataSetChanged();
        items.setAdapter(adapterWords);


    }


    //btn add new word
    public void AddNew(View view) {
        dialogAddNew();
    }


    public boolean isReady(Dialog dialog) {
        etNewWord = (EditText) dialog.findViewById(R.id.word);
        etNewMeaning = (EditText) dialog.findViewById(R.id.meaning);
        String newWord = etNewWord.getText().toString();
        String newMeaning = etNewMeaning.getText().toString();

        if (newWord.equals("")) {
            Toast.makeText(this, "The Word's Name is missing.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newWord.equals("")) {
            Toast.makeText(this, "The Word's Meaning is missing.", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (newWord.equals(arrayWords.get(i)) && newMeaning.equals(arrayMeaning.get(i))) {
                Toast.makeText(this, "The Word exists in the database", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }


    public boolean isReadyEdit(Dialog dialog) {
        etNewWord = (EditText) dialogEdit.findViewById(R.id.word);
        etNewMeaning = (EditText) dialogEdit.findViewById(R.id.meaning);
        String newWord = etNewWord.getText().toString();
        String newMeaning = etNewMeaning.getText().toString();

        if (newWord.equals("")) {
            Toast.makeText(this, "The Word's Name is missing.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newWord.equals("")) {
            Toast.makeText(this, "The Word's Meaning is missing.", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (newWord.equals(arrayWords.get(i)) && newMeaning.equals(arrayMeaning.get(i))) {
                Toast.makeText(this, "The Word exists in the database", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (arrayWords.get(dialogEditWordPosition).equals(newWord) && arrayMeaning.get(dialogEditWordPosition).equals(newWord)) {
            Toast.makeText(this, "every Thing's the same.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    void saveNewWords() {
        editorWords.putString("word" + Integer.toString(count), newWord);
        editorMeanings.putString("meaning" + Integer.toString(count), newMeaning);
        editorWords.putString("count", Integer.toString(count + 1));

        editorWords.commit();
        editorMeanings.commit();

        count++;
        setElementsValue();
    }


    void refreshList() {
        editorWords.clear();
        editorMeanings.clear();
        editorWords.putString("count", Integer.toString(count));

        for (int i = 0; i < count; i++) {
            editorWords.putString("word" + Integer.toString(i), arrayWords.get(i));
            editorMeanings.putString("meaning" + Integer.toString(i), arrayMeaning.get(i));
        }
        editorWords.commit();
        editorMeanings.commit();
    }


    public void search(String key) {

        int found = 0;
        if (count > 0) {
            arrayWordsSearch.clear();
            arrayWordsToShow.clear();
            arrayMeaningSearch.clear();
            for (int i = 0, j = 0; i < count; i++) {
                key = key.toUpperCase();
                String word = arrayWords.get(i);
                String meaning = arrayMeaning.get(i);
                word = word.toUpperCase();
                meaning = meaning.toUpperCase();
                if (searchMethod.equals("wordsAndMeanings") ? word.contains(key) || meaning.contains(key) :
                        searchMethod.equals("justWords") ? word.contains(key) :
                                meaning.contains(key)) {
//                    if (wordsA[i].contains(key) || meaningsA[i].contains(key)) {
                    arrayWordsSearch.add(Words.getString("word" + Integer.toString(i), "word" + Integer.toString(i)));
                    arrayWordsToShow.add(j + 1 + ".  " + Words.getString("word" + Integer.toString(i), "word" + Integer.toString(i)));
                    arrayMeaningSearch.add(Meanings.getString("meaning" + Integer.toString(i), "meaning" + Integer.toString(i)));
                    found++;
                    j++;
                }
            }
            if (found > 0) {
                adapterWords.notifyDataSetChanged();
                items.setAdapter(adapterWords);
            }
        }

        isFromSearch = true;

    }


    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        EditText wordAddNew = (EditText) dialogAddNew.findViewById(R.id.word);
        EditText meaningAddNew = (EditText) dialogAddNew.findViewById(R.id.meaning);


        icicle.putInt("listViewPosition", items.getFirstVisiblePosition());

        if (dialogAddNew.isShowing()) {
            icicle.putBoolean("dialogAddNewIsOpen", dialogAddNew.isShowing());
            icicle.putString("editTextWordAddNew", wordAddNew.getText().toString());
            icicle.putString("editTextMeaningAddNew", meaningAddNew.getText().toString());
        }
        if (dialogMeaning.isShowing()) {
            icicle.putBoolean("dialogMeaningIsOpen", dialogMeaning.isShowing());
            icicle.putString("dialogMeaningText", dialogMeaningText);
            icicle.putInt("dialogMeaningWordPosition", dialogMeaningWordPosition);
            icicle.putBoolean("dialogMeaningIsFromSearch", isFromSearch);
        }
        if (dialogEdit.isShowing()) {
            EditText dialogEditWord = (EditText) dialogEdit.findViewById(R.id.word);
            EditText dialogEditMeaning = (EditText) dialogEdit.findViewById(R.id.meaning);

            icicle.putBoolean("dialogEditIsOpen", dialogEdit.isShowing());
            icicle.putInt("dialogMeaningWordPosition", dialogMeaningWordPosition);
            icicle.putBoolean("dialogMeaningIsFromSearch", isFromSearch);
            icicle.putString("dialogEditWordText", dialogEditWord.getText().toString());
            icicle.putString("dialogEditMeaningText", dialogEditMeaning.getText().toString());
        }
        if (dialogAskDelete.isShowing()) {
            icicle.putBoolean("dialogAskDeleteIsOpen", dialogAskDelete.isShowing());

            icicle.putInt("dialogMeaningWordPosition", dialogMeaningWordPosition);
            icicle.putBoolean("dialogMeaningIsFromSearch", isFromSearch);
            icicle.putString("dialogEditWordText", newWordEdit);
            icicle.putString("dialogEditMeaningText", newMeaningEdit);
        }


        icicle.putBoolean("dialogMeaningIsOpen", dialogMeaning.isShowing());
        icicle.putBoolean("dialogEditIsOpen", dialogEdit.isShowing());

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        getPrefs();
        items.setSelectionFromTop(listViewPosition, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, "WOW", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, Preferences.class));
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
