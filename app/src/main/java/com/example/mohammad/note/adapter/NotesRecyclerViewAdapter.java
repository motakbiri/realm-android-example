package com.example.mohammad.note.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mohammad.note.R;
import com.example.mohammad.note.activity.NoteActivity;
import com.example.mohammad.note.helper.DbHelper;
import com.example.mohammad.note.model.Note;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class NotesRecyclerViewAdapter extends RealmRecyclerViewAdapter<Note,NotesRecyclerViewAdapter.NoteViewHolder>{
    private  Context context;
    private OrderedRealmCollection<Note> notes;
    private DbHelper dbHelper;
    private Realm realm;

    public NotesRecyclerViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Note> notes) {
        super(context, notes, true);
        this.context = context;
        this.notes = notes;
        this.realm = Realm.getDefaultInstance();
        this.dbHelper = new DbHelper(context,realm);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_card, parent, false);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, final int position) {
        final Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(note.getType().equals(context.getString(R.string.drawing_type))){
                    Toast.makeText(context, "Drawing not Supported yet!", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(context, NoteActivity.class);
                    intent.putExtra(context.getString(R.string.note_activity_type),context.getString(R.string.edit_text_type));
                    intent.putExtra("note",note.getId());
                    context.startActivity(intent);
                }
            }
        });
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow,position);
            }
        });
    }
    private void showPopupMenu(View view, final int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_note_item, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        dbHelper.deleteNote(notes.get(position));
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content;
        public ImageView overflow;

        public NoteViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text_view_title);
            content = (TextView) view.findViewById(R.id.text_view_content);
            overflow = (ImageView) view.findViewById(R.id.image_view_overflow);
        }
    }
    @Override
    public int getItemCount() {
        return notes.size();
    }

}
