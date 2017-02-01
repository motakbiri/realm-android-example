package com.example.mohammad.note.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.example.mohammad.note.R;
import com.example.mohammad.note.adapter.TagsSpinnerAdapter;
import com.example.mohammad.note.helper.DbHelper;
import com.example.mohammad.note.model.Note;
import com.example.mohammad.note.model.Tag;
import com.example.mohammad.note.view.DrawingView;
import io.realm.Realm;
import io.realm.RealmResults;


//TODO:better bold italic support(bold specific part of text, save last state)

public class NoteActivity extends AppCompatActivity {
    private Realm realm;
    private Spinner spinner;
    private AppCompatEditText titleText;
    private String type;
    private Note note;
    private DbHelper dbHelper;
    private DrawingView drawingView;
    private AppCompatEditText contnetText;
    private ImageButton boldButton,italicButton;
    private boolean bold = false;
    private boolean italic = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initial();
        setupToolbar();
        setupSpinner();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_save:
                if(type.equals(getString(R.string.drawing_type)) ){
                    dbHelper.addNote(titleText.getText().toString(),"Drawing not supported yet!",type,
                            (Tag) spinner.getSelectedItem());
                } else if(type.equals(getString(R.string.edit_text_type))){

                    dbHelper.updateNote(note.getId(),titleText.getText().toString(),contnetText.getText().toString(),
                            type,(Tag) spinner.getSelectedItem());
                }
                else{
                    dbHelper.addNote(titleText.getText().toString(),contnetText.getText().toString(),type,
                            (Tag) spinner.getSelectedItem());
                }

                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_activity, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void initial(){
        realm = Realm.getDefaultInstance();
        dbHelper = new DbHelper(this, realm);
        type = getIntent().getStringExtra(getString(R.string.note_activity_type));

        titleText = (AppCompatEditText) findViewById(R.id.edit_text_note_title);
        contnetText = (AppCompatEditText) findViewById(R.id.edit_text_note_content);
        drawingView = (DrawingView) findViewById(R.id.drawing_view);
        boldButton = (ImageButton) findViewById(R.id.image_button_note_bold);
        italicButton = (ImageButton) findViewById(R.id.image_button_note_italic);

        if(type.equals(getString(R.string.drawing_type)) ){
            drawingView.setVisibility(View.VISIBLE);
            findViewById(R.id.editor_layout).setVisibility(View.GONE);
        }else if(type.equals(getString(R.string.edit_text_type))){
            contnetText.setVisibility(View.VISIBLE);
            note = realm.where(Note.class).equalTo("id",getIntent().getIntExtra("note",0)).findFirst();
            titleText.setText(note.getTitle());
            contnetText.setText(note.getContent());
        }else{
            contnetText.setVisibility(View.VISIBLE);
        }

        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBold();
            }
        });

        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchItalic();
            }
        });
    }

    private void setupToolbar(){
        Toolbar noteToolbar = (Toolbar) findViewById(R.id.note_toolbar);
        setSupportActionBar(noteToolbar);
        getSupportActionBar().setTitle(type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

    }

    private void setupSpinner(){
        spinner = (Spinner) findViewById(R.id.spinner_select_tag);
        RealmResults<Tag> tags = realm.where(Tag.class).findAll();
        spinner.setAdapter(new TagsSpinnerAdapter(this,tags));

    }

    private void switchBold(){
        if(bold){
            bold = !bold;
            boldButton.setImageResource(R.drawable.ic_format_bold_gray_24dp);
            if(italic){
                contnetText.setTypeface(null, Typeface.ITALIC);
            }
            else{
                contnetText.setTypeface(null, Typeface.NORMAL);
            }
        }else{
            bold = !bold;
            boldButton.setImageResource(R.drawable.ic_format_bold_black_24dp);
            if(italic){
                contnetText.setTypeface(null, Typeface.BOLD_ITALIC);
            }
            else{
                contnetText.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    private void switchItalic(){
        if(italic){
            italic = !italic;
            italicButton.setImageResource(R.drawable.ic_format_italic_gray_24dp);
            if(bold){
                contnetText.setTypeface(null, Typeface.BOLD);
            }
            else{
                contnetText.setTypeface(null, Typeface.NORMAL);
            }
        }else{
            italic = !italic;
            italicButton.setImageResource(R.drawable.ic_format_italic_black_24dp);
            if(bold){
                contnetText.setTypeface(null, Typeface.BOLD_ITALIC);
            }
            else{
                contnetText.setTypeface(null, Typeface.ITALIC);
            }
        }
    }
}
