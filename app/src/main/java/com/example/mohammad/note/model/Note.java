package com.example.mohammad.note.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Note extends RealmObject{

    @PrimaryKey
    private int id;
    private String type; //use note types in string values
    private String title;
    private String content;
    private Tag tag;

    public Note(){

    }

    public Note(int id, String type, String title, String content, Tag tag) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
