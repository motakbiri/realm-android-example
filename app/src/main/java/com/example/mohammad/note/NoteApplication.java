package com.example.mohammad.note;


import android.app.Application;
import android.widget.Toast;
import com.example.mohammad.note.helper.DbHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class NoteApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("note.realm")
                .build();
        Realm.setDefaultConfiguration(config);
        firstTime();
    }

    private void firstTime(){
        DbHelper dbHelper = new DbHelper(this,Realm.getDefaultInstance());
        dbHelper.initialDb();
    }
}