package com.example.mohammad.note.helper;


import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.mohammad.note.R;
import com.example.mohammad.note.model.Note;
import com.example.mohammad.note.model.Tag;
import io.realm.Realm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DbHelper {
    private File EXPORT_REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);;
    private String EXPORT_REALM_FILE_NAME = "notes.realm";
    private String IMPORT_REALM_FILE_NAME = "note.realm";
    private Context context;
    private Realm realm;


    public DbHelper(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    public void initialDb() {
        if (realm.where(Tag.class).count() == 0) {
            realm.beginTransaction();
            Tag tag = realm.createObject(Tag.class, getNextTagKey());
            tag.setName("Default");
            tag.setColor(Color.WHITE);
            realm.commitTransaction();
        }
    }

    public int getNextNoteKey() {
        if (realm.where(Note.class).count() > 0)
            return realm.where(Note.class).max("id").intValue() + 1;
        else
            return 0;
    }

    public int getNextTagKey() {
        if (realm.where(Tag.class).count() > 0)
            return realm.where(Tag.class).max("id").intValue() + 1;
        else
            return 0;
    }

    public void addNote(String title, String content, String type, Tag tag) {
        realm.beginTransaction();
        Note note = realm.createObject(Note.class, getNextNoteKey());
        note.setTitle(title);
        note.setContent(content);
        note.setType(type);
        note.setTag(tag);
        realm.commitTransaction();

    }

    public void addTag(String name, int color) {
        realm.beginTransaction();
        Tag tag = realm.createObject(Tag.class, getNextTagKey());
        tag.setName(name);
        tag.setColor(color);
        realm.commitTransaction();
    }

    public void deleteNote(final Note note) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                note.deleteFromRealm();
            }
        });
    }
    public void updateNote(int id, String title, String content,String type,Tag tag){
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);
        note.setType(type);
        note.setTag(tag);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }

    public void backup() {
        File exportRealmFile;
        exportRealmFile = new File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);
        exportRealmFile.delete();
        realm.writeCopyTo(exportRealmFile);
        String msg = "File exported to Path: " + EXPORT_REALM_PATH + "/" + EXPORT_REALM_FILE_NAME;
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Done")
                .content(msg)
                .positiveText("Ok")
                .show();

    }
    public void restore(String restoreFilePath){
        copyBundledRealmFile(restoreFilePath, IMPORT_REALM_FILE_NAME);
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Done")
                .content("Next time you open application, data will be updated! :)")
                .positiveText("Ok")
                .show();


    }

    private String copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File file = new File(context.getApplicationContext().getFilesDir(), outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}


