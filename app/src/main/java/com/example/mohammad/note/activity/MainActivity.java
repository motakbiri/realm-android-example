package com.example.mohammad.note.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.example.mohammad.note.R;
import com.example.mohammad.note.adapter.NotesRecyclerViewAdapter;
import com.example.mohammad.note.adapter.TagsSpinnerAdapter;
import com.example.mohammad.note.helper.DbHelper;
import com.example.mohammad.note.helper.MarshMallowPermission;
import com.example.mohammad.note.model.Note;
import com.example.mohammad.note.model.Tag;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import io.realm.Realm;
import io.realm.RealmResults;

import java.io.File;

public class MainActivity extends AppCompatActivity implements FileChooserDialog.FileCallback{

    private Realm realm;
    private DbHelper dbHelper;
    MarshMallowPermission permission;
    private RecyclerView notesRecyclerView;
    private NotesRecyclerViewAdapter notesRecyclerViewAdapter;


    private Spinner spinner;


    private FloatingActionButton fabText, fabDrawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        dbHelper = new DbHelper(this, realm);
        permission = new MarshMallowPermission(this);

        setupToolbar();
        setupRecyclerView();
        setupSpinner();
        setupFAB();

        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NoteActivity.class);
                intent.putExtra(getString(R.string.note_activity_type),getString(R.string.text_type));
                startActivity(intent);
            }
        });
        fabDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NoteActivity.class);
                intent.putExtra(getString(R.string.note_activity_type),getString(R.string.drawing_type));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                if(!permission.checkPermissionForExternalStorage())
                    permission.requestPermissionForExternalStorage();
                if(permission.checkPermissionForExternalStorage())
                {
                    new FileChooserDialog.Builder(this)
                            .initialPath("/sdcard/Download")
                            .extensionsFilter(".realm")
                            .tag("Select Data File")
                            .goUpLabel("Up")
                            .show();


                }

                return true;

            case R.id.action_export:
                if(!permission.checkPermissionForExternalStorage())
                    permission.requestPermissionForExternalStorage();
                if(permission.checkPermissionForExternalStorage())
                {
                    dbHelper.backup();
                }
                return true;

            case R.id.action_add_tag:
                new MaterialDialog.Builder(this)
                        .title(R.string.add_tag)
                        .content(R.string.tag_title)
                        .inputType(InputType.TYPE_CLASS_TEXT )
                        .input(getString(R.string.tag_title_hint), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                //TODO: Add color chooser for getting TAG color from user
                               dbHelper.addTag(input.toString(),0xffffff);
                            }
                        }).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appbar, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setupToolbar(){
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }
    private void setupRecyclerView(){
        RealmResults<Note> notes= realm.where(Note.class).equalTo("tag.name","Default").findAll();
        notesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_notes);
        notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(this, notes);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        notesRecyclerView.setLayoutManager(mLayoutManager);
        notesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notesRecyclerView.setAdapter(notesRecyclerViewAdapter);

    }
    private void setupFAB(){
        fabText = (FloatingActionButton) findViewById(R.id.fab_text);
        fabDrawing = (FloatingActionButton) findViewById(R.id.fab_drawing);


    }
    private void setupSpinner(){
        spinner = (Spinner) findViewById(R.id.spinner_tags);
        RealmResults<Tag> tags = realm.where(Tag.class).findAll();
        spinner.setAdapter(new TagsSpinnerAdapter(this,tags));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RealmResults<Note> notes= realm.where(Note.class).equalTo("tag.name",((Tag)spinner.getItemAtPosition(i)).getName()).findAll();
                notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(MainActivity.this, notes);
                notesRecyclerView.swapAdapter(notesRecyclerViewAdapter,false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        dbHelper.restore(file.getPath());
    }
}
