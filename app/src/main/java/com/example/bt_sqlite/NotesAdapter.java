package com.example.bt_sqlite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NotesAdapter extends BaseAdapter {
    private final MainActivity context;
    private final int layout;
    private final List<NotesModel> noteList;

    // Tạo constructor
    public NotesAdapter(MainActivity context, int layout, List<NotesModel> noteList) {
        this.context = context;
        this.layout = layout;
        this.noteList = noteList;
    }

    @Override
    public int getCount() {
        return noteList == null ? 0 : noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList == null ? null : noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (noteList == null) return position;
        NotesModel note = noteList.get(position);
        return note == null ? position : note.getIdNote();
    }

    private static class ViewHolder {
        TextView textViewNote;
        ImageView imageViewEdit;
        ImageView imageViewDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
            viewHolder.textViewNote = convertView.findViewById(R.id.textViewNameNote);
            viewHolder.imageViewDelete = convertView.findViewById(R.id.imageViewDelete);
            viewHolder.imageViewEdit = convertView.findViewById(R.id.imageViewEdit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Defensive checks to avoid crashes
        if (noteList == null || position < 0 || position >= noteList.size()) {
            return convertView;
        }

        final NotesModel notes = noteList.get(position);
        if (notes == null) return convertView;

        if (viewHolder.textViewNote != null) {
            try {
                viewHolder.textViewNote.setText(notes.getNameNote());
            } catch (Exception e) {
                // avoid crashing when setting text (very defensive)
                e.printStackTrace();
            }
        }

        // Bắt sự kiện nút cập nhật
        if (viewHolder.imageViewEdit != null) {
            viewHolder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Cập nhật " + notes.getNameNote(), Toast.LENGTH_SHORT).show();
                    // Gọi Dialog trong MainActivity.java
                    context.DialogCapNhatNotes(notes.getNameNote(), notes.getIdNote());
                }
            });
        }

        // Bắt sự kiện nút xóa -> gọi DialogDelete trong MainActivity
        if (viewHolder.imageViewDelete != null) {
            viewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.DialogDelete(notes.getNameNote(), notes.getIdNote());
                }
            });
        }

        return convertView;
    }
}
