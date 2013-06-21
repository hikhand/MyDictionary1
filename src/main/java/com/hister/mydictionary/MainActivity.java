package com.hister.mydictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {
    public SharedPreferences Words;
    public SharedPreferences Meanings;
    public SharedPreferences Dates;
    public SharedPreferences Settings;
    SharedPreferences.Editor editorWords;
    SharedPreferences.Editor editorMeanings;
    SharedPreferences.Editor editorDates;
    SharedPreferences.Editor editorSettings;

    public String newWord;
    public String newMeaning;
    public String newDate;
    public String newWordEdit;
    public String newMeaningEdit;

    public EditText etNewWord;
    public EditText etNewMeaning;
    public EditText etSearch;
    public ListView items;
    public int count = 0;
    public boolean isFromSearch;

    ArrayList<Custom> arrayItems;
    ArrayList<Custom> arrayItemsToShow;
    ArrayList<String> arrayMeaning;

    ArrayList<String> arrayItemsSearch;
    ArrayList<String> arrayMeaningSearch;

    public Adapter adapterWords1;


    public AlertDialog dialogAddNew;
    public AlertDialog dialogEdit;
    public AlertDialog dialogMeaning;
    public AlertDialog dialogAskDelete;

    boolean dialogAddNewIsOpen = false;

    boolean dialogMeaningIsOpen = false;
    String dialogMeaningText = null;
    int dialogMeaningWordPosition = 0;
    int dialogEditWordPosition = 0;
    boolean dialogEditIsOpen = false;
    boolean dialogAskDeleteIsOpen = false;

    ImageView imgAdd;

    String searchMethod;
    boolean showItemNumber = true;

    SharedPreferences prefs;


    private boolean markSeveral = false;
    Parcelable listViewPosition = null;

    SparseBooleanArray arrayItemsChecked = null;
    ArrayList<Integer> checkedPositionsInt;

    boolean isMark = true;


    SimpleDateFormat simpleDateFormat;
    String currentDateAndTime;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (markSeveral) {
            adapterWords1 = new Adapter(MainActivity.this, R.layout.listview_row, arrayItemsToShow);
        }
            setElementsValue();
            markSeveral = false;

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        setElementsId();
        setElementsValue();
        getPrefs();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setImgAddVisibility();
        restore(icicle);

        items.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if keyboard was up puts it down !!
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                if (!markSeveral) {
                    dialogMeaning(isFromSearch, position);
                }
                else {
                    if (arrayItemsToShow.get(position).isChChecked()) {
                        arrayItemsToShow.get(position).setChChecked(false);
                    }
                    else {
                        arrayItemsToShow.get(position).setChChecked(true);
                    }
                    arrayItemsChecked = items.getCheckedItemPositions();
                    adapterWords1.notifyDataSetChanged();
                }
            }
        });

//        items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "Yeah", Toast.LENGTH_SHORT).show();
//
//                view.setSelected(true);
////                view.setBackgroundColor(Color.parseColor("#1aa3d2"));
//
//
//                return false;
//            }
//        });


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
    }


    void restore(Bundle icicle) {
        if (icicle != null) {
//            listViewPosition = icicle.getInt("listViewPosition");
            dialogAddNewIsOpen = icicle.getBoolean("dialogAddNewIsOpen");
            dialogMeaningIsOpen = icicle.getBoolean("dialogMeaningIsOpen");
            dialogEditIsOpen = icicle.getBoolean("dialogEditIsOpen");
            dialogAskDeleteIsOpen = icicle.getBoolean("dialogAskDeleteIsOpen");
            listViewPosition = icicle.getParcelable("listViewPosition");
            markSeveral = icicle.getBoolean("markSeveral");
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

        if (markSeveral) {
            setElementsId();
            listViewPosition = icicle.getParcelable("listViewPosition");
            checkedPositionsInt = icicle.getIntegerArrayList("checkedPositionsInt");
            setElementsValue();
        }


    }




    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        searchMethod = prefs.getString("searchMethod", "wordsAndMeanings");
        showItemNumber = prefs.getBoolean("showItemNumber", true);
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

    void setImgAddVisibility() {
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        imgAdd.setVisibility(View.GONE);
        if (count == 0) {
            imgAdd.setVisibility(View.VISIBLE);
        }
        else {
            imgAdd.setVisibility(View.GONE);
        }

    }


    public void setElementsId() {
        Words = getSharedPreferences("Words", 0);
        Meanings = getSharedPreferences("Meanings", 0);
        Dates = getSharedPreferences("Dates", 0);
        Settings = getSharedPreferences("Settings", 0);
        editorWords = Words.edit();
        editorMeanings = Meanings.edit();
        editorSettings = Settings.edit();
        editorDates = Dates.edit();

        items = (ListView) findViewById(R.id.listView);
        etSearch = (EditText) findViewById(R.id.etSearch);

        arrayItems = new ArrayList<Custom>();
        arrayItemsToShow= new ArrayList<Custom>();
        arrayMeaning = new ArrayList<String>();

        arrayItemsSearch = new ArrayList<String>();
        arrayMeaningSearch = new ArrayList<String>();

        if (markSeveral) {
            adapterWords1 = new Adapter(MainActivity.this, R.layout.listview_row,  arrayItemsToShow);

        }
        else {
            adapterWords1 = new Adapter(MainActivity.this, R.layout.listview_row, arrayItemsToShow);
        }


        String countStr = Words.getString("count", "0");
        count = Integer.parseInt(countStr);

        dialogAddNew = new AlertDialog.Builder(this).create();
        dialogEdit = new AlertDialog.Builder(this).create();
        dialogMeaning = new AlertDialog.Builder(this).create();
        dialogAskDelete = new AlertDialog.Builder(this).create();

        if (listViewPosition == null) {
            listViewPosition = items.onSaveInstanceState();
        }

        checkedPositionsInt = new ArrayList<Integer>();

        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH-mm");
        currentDateAndTime = simpleDateFormat.format(new Date());
    }

    public void setElementsValue() {
        if (count > 0) {
            arrayItems.clear();
            arrayItemsToShow.clear();
            arrayMeaning.clear();
            for (int i = 0; i < count; i++) {
                arrayItems.add(new Custom(Words.getString("word" + Integer.toString(i), "word" + Integer.toString(i)),
                        Dates.getString("date" + Integer.toString(i), "date" + Integer.toString(i)),
                        markSeveral));
                arrayMeaning.add(Meanings.getString("meaning" + Integer.toString(i), "meaning" + Integer.toString(i)));

                if (showItemNumber) {
                    arrayItemsToShow.add( new Custom(i + 1 + ".  " + Words.getString("word" + Integer.toString(i), i + 1 + ".  " + "word" + Integer.toString(i)),
                            Dates.getString("date" + Integer.toString(i), "date" + Integer.toString(i)),
                            markSeveral));
                }
                else {
                    arrayItemsToShow.add( new Custom(Words.getString("word" + Integer.toString(i), i + 1 + ".  " + "word" + Integer.toString(i)),
                            Dates.getString("date" + Integer.toString(i), "date" + Integer.toString(i)),
                            markSeveral));
                }
//                if (markSeveral) {
//                    item = arrayItems.get(i);
//                    item.setChecked(true);
//                    item.setChVisible(true);
//                }
//                else {
//                    item = arrayItems.get(i);
//                    item.setChVisible(false);
//                }
            }
        }



        adapterWords1.notifyDataSetChanged();
        items.setAdapter(adapterWords1);
        items.onRestoreInstanceState(listViewPosition);

        if (markSeveral) {
            for (int i = 0; i < items.getCount(); i++) {
                if (checkedPositionsInt.size() > 0) {
                    items.setItemChecked(i, checkedPositionsInt.get(i) == 0);
                }
            }
        }

    }





    ///////Dialogs
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
        dialogEdit.setCanceledOnTouchOutside(false);
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

                int realPosition = realPosition(dialogEditWordPosition);

                editorWords.remove("word" + realPosition);
                editorMeanings.remove("meaning" + realPosition);
                editorWords.commit();
                editorMeanings.commit();
                arrayItems.remove(realPosition);
                arrayItemsToShow.remove(dialogEditWordPosition );
                arrayMeaning.remove(realPosition);
                count--;

                setImgAddVisibility();

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
        dialogAskDelete.setCanceledOnTouchOutside(false);
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
        dialogAddNew.setCanceledOnTouchOutside(false);
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

                simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH-mm");
                currentDateAndTime = simpleDateFormat.format(new Date());
                newDate = currentDateAndTime;
                saveNewWords();
                setElementsValue();
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Successfully added.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    void dialogAskDeleteByMark() {
        int countItems = 0;
        for (int i = 0; i < items.getCount(); i++) {
            arrayItemsChecked = items.getCheckedItemPositions();

            if (arrayItemsChecked.get(i)) {
                countItems++;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission To Delete");
        builder.setMessage("Are you sure you want to delete these '" + Integer.toString(countItems) + "' words ?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedPositionsInt.clear();
                for (int i = 0; i < arrayItemsToShow.size(); i++) {
                    checkedPositionsInt.add(i, arrayItemsChecked.get(i) ? 0 : 1);
                }
                int for1 = arrayItemsToShow.size();
                int for2 = arrayItems.size();
                int left;
                boolean found = false;
                for (int i = 0; i < for1; ++i) {
                    if (found) {
                        i = 0;
                        found = false;
                    }
                    if (checkedPositionsInt.get(i) == 1) continue;
                    for (int j = 0; j < for2; ++j) {
                        if (arrayItemsToShow.get(j).equals(items.getItemAtPosition(i))) {
                            int position = realPosition(j);

                            editorWords.remove("word" + position);
                            editorMeanings.remove("meaning" + position);
                            arrayItems.remove(position);
                            arrayItemsToShow.remove(i);
                            arrayMeaning.remove(position);
                            count--;
                            checkedPositionsInt.remove(i);
                            left = checkedPositionsInt.size();
                            if (left != 1) {
                            for1 = arrayItemsToShow.size();
                            }
                            for2 = arrayItems.size();
                            found = true;
                            i = 0;
                            break;
                        }
                        else {
                            found = false;
                        }
                    }
                }
                editorWords.commit();
                editorMeanings.commit();

                refreshList();
                setElementsValue();

                Toast.makeText(MainActivity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialogAskDelete = builder.create();
        dialogAskDelete.show();
        dialogAskDelete.setCanceledOnTouchOutside(false);
    }


    int realPosition(int position) {
        int realPosition = 0;
        boolean found = false;
        for (int i = 0; i < arrayItems.size(); i++) {
            if (arrayItems.get(i).equals(arrayItemsToShow.get(position))) {
                realPosition = i;
                break;
            }
            for (int j = 0; j < arrayItems.size(); j++) {
                if ((Integer.toString(j + 1) + ".  " + arrayItems.get(i)).equals(arrayItemsToShow.get(position))) {
                    realPosition = i;
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
        return realPosition;
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
            if (newWord.equals(arrayItems.get(i)) && newMeaning.equals(arrayMeaning.get(i))) {
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
            if (newWord.equals(arrayItems.get(i)) && newMeaning.equals(arrayMeaning.get(i))) {
                Toast.makeText(this, "The Word exists in the database", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (arrayItems.get(dialogEditWordPosition).equals(newWord) && arrayMeaning.get(dialogEditWordPosition).equals(newWord)) {
            Toast.makeText(this, "every Thing's the same.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    void saveNewWords() {
        editorWords.putString("word" + Integer.toString(count), newWord);
        editorMeanings.putString("meaning" + Integer.toString(count), newMeaning);
        editorDates.putString("date" + Integer.toString(count), newDate);
        editorWords.putString("count", Integer.toString(count + 1));

        editorWords.commit();
        editorMeanings.commit();
        editorDates.commit();

        count++;
        setImgAddVisibility();
    }


    void refreshList() {
        editorWords.clear();
        editorMeanings.clear();
        editorWords.putString("count", Integer.toString(count));

        for (int i = 0; i < count; i++) {

            editorWords.putString("word" + Integer.toString(i), arrayItems.get(i).getWord());
            editorWords.putString("date" + Integer.toString(i), arrayItems.get(i).getDate());
            editorMeanings.putString("meaning" + Integer.toString(i), arrayMeaning.get(i));
        }
        editorWords.commit();
        editorMeanings.commit();
    }


    public void search(String key) {

        int found = 0;
        if (count > 0) {
            arrayItemsSearch.clear();
            arrayItemsToShow.clear();
            arrayMeaningSearch.clear();
            for (int i = 0, j = 0; i < count; i++) {
                key = key.toUpperCase();
                String word = arrayItems.get(i).getWord().toUpperCase();
                String meaning = arrayMeaning.get(i).toUpperCase();

                if (searchMethod.equals("wordsAndMeanings") ? word.contains(key) || meaning.contains(key) :
                        searchMethod.equals("justWords") ? word.contains(key) :
                                meaning.contains(key)) {
                    arrayItemsSearch.add(Words.getString("word" + Integer.toString(i), "word" + Integer.toString(i)));

                    if (showItemNumber) {
                        arrayItemsToShow.add( new Custom(i + 1 + ".  " + Words.getString("word" + Integer.toString(i), i + 1 + ".  " + "word" + Integer.toString(i)),
                                Dates.getString("date" + Integer.toString(i), "date" + Integer.toString(i)),
                                markSeveral));
                    }
                    else {
                        arrayItemsToShow.add( new Custom(Words.getString("word" + Integer.toString(i), i + 1 + ".  " + "word" + Integer.toString(i)),
                                Dates.getString("date" + Integer.toString(i), "date" + Integer.toString(i)),
                                markSeveral));
                    }
                    arrayMeaningSearch.add(Meanings.getString("meaning" + Integer.toString(i), "meaning" + Integer.toString(i)));
                    found++;
                    j++;
                }
            }
            if (found > 0) {
                adapterWords1.notifyDataSetChanged();
                items.setAdapter(adapterWords1);
            }
        }

        isFromSearch = true;

    }


    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        
        EditText wordAddNew = (EditText) dialogAddNew.findViewById(R.id.word);
        EditText meaningAddNew = (EditText) dialogAddNew.findViewById(R.id.meaning);


        icicle.putParcelable("listViewPosition", items.onSaveInstanceState());

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


        if (markSeveral) {
            icicle.putBoolean("markSeveral", markSeveral);
            arrayItemsChecked = items.getCheckedItemPositions();
            checkedPositionsInt.clear();
            for (int i = 0; i < arrayItemsToShow.size(); i++) {
                checkedPositionsInt.add(i, arrayItemsChecked.get(i) ? 0 : 1);
            }
            icicle.putIntegerArrayList("checkedPositionsInt", checkedPositionsInt);
        }



    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        getPrefs();
        setElementsValue();
//        items.setSelectionFromTop(listViewPosition, 0);
        items.onRestoreInstanceState(listViewPosition);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }


    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.clear();

        if (markSeveral) {
            getMenuInflater().inflate(R.menu.on_delete, menu);
            MenuItem itemMarkAll = menu.findItem(R.id.action_markAll);

            if (isMark) itemMarkAll.setTitle(R.string.action_markAll);
            else itemMarkAll.setTitle(R.string.action_unmarkAll);
        }
        else {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, Preferences.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;

            case R.id.action_mark:
                markSeveral = true;
                listViewPosition = items.onSaveInstanceState();
                setElementsId();
                setElementsValue();
                return true;
            
            case R.id.action_delete:
                arrayItemsChecked = items.getCheckedItemPositions();
                checkedPositionsInt.clear();
                for (int i = 0; i < arrayItemsToShow.size(); i++) {
                    checkedPositionsInt.add(i, arrayItemsChecked.get(i) ? 0 : 1);
                }
                boolean arrayItemsCheckedIsEmpty = true;
                for (int i = 0; i < checkedPositionsInt.size(); i++) {
                    if (checkedPositionsInt.get(i) == 0) {
                        arrayItemsCheckedIsEmpty = false;
                        break;
                    }
                    else {
                        arrayItemsCheckedIsEmpty = true;
                    }
                }
                if (!arrayItemsCheckedIsEmpty) {
                    dialogAskDeleteByMark();
                }
                else {
                    Toast.makeText(MainActivity.this, "You haven't selected any item.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_markAll:
                if (isMark) {
                    for (int i = 0; i < items.getCount(); i++) {
                        items.setItemChecked(i, true);
                    }
                    isMark = false;
                }
                else {
                    for (int i = 0; i < items.getCount(); i++) {
                        items.setItemChecked(i, false);
                    }
                    isMark = true;
                }
                return true;
            case R.id.action_cancel:
                markSeveral = false;
                listViewPosition = items.onSaveInstanceState();
                setElementsId();
                setElementsValue();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}





























