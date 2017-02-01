package com.example.mohammad.note.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.mohammad.note.R;
import com.example.mohammad.note.model.Tag;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class TagsSpinnerAdapter extends RealmBaseAdapter<Tag> {
    OrderedRealmCollection<Tag> tags;
    public TagsSpinnerAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Tag> tags) {
        super(context, tags);
        this.tags = tags;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TagViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_dropdown_item,viewGroup,false);
            viewHolder = new TagViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.spinner_tag_name);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (TagViewHolder)view.getTag();
        }
        Tag tag = tags.get(i);
        viewHolder.name.setText(tag.getName());
        return view;
    }

    public class TagViewHolder{
        public TextView name;
    }
}
