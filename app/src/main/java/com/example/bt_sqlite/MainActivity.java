package com.example.bt_sqlite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    ListView listView;
    ArrayList<NotesModel> arrayList;
    NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gọi hàm khởi tạo và thao tác với database
        InitDatabaseSQLite();

        // Ánh xạ ListView và gọi Adapter with listener
        listView = findViewById(R.id.listView1);
        arrayList = new ArrayList<>();
        adapter = new NotesAdapter(this, R.layout.row_notes, arrayList);
        listView.setAdapter(adapter);

        try {
            if (databaseHandler != null && isNotesTableEmpty()) {
                databaseHandler.QueryData("INSERT INTO Notes VALUES(null, 'Ví dụ SQLite 1')");
                databaseHandler.QueryData("INSERT INTO Notes VALUES(null, 'Ví dụ SQLite 2')");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi thêm mẫu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        databaseSQLite();

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogThem();
                }
            });
        }
    }

    private boolean isNotesTableEmpty() {
        if (databaseHandler == null) return true;
        Cursor c = null;
        try {
            c = databaseHandler.GetData("SELECT COUNT(*) FROM Notes");
            if (c != null && c.moveToFirst()) {
                int count = c.getInt(0);
                return count == 0;
            }
        } catch (Exception e) {
            return true;
        } finally {
            if (c != null) c.close();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //bắt sự kiện cho menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuAddNotes) {
            DialogThem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Wrapper method to match DialogThem() name
    private void DialogThem() {
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_note);

        // Ánh xạ trong dialog
        final EditText editText = dialog.findViewById(R.id.editTextName);
        Button buttonAdd = dialog.findViewById(R.id.buttonThem);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);

        // Bắt sự kiện cho nút Thêm và Hủy
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString().trim();
                if (name.equals("")) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên Notes", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        if (databaseHandler != null)
                            databaseHandler.QueryData("INSERT INTO Notes VALUES(null, '" + name.replace("'","''") + "')");
                        Toast.makeText(MainActivity.this, "Đã thêm Notes", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        databaseSQLite(); // Gọi hàm load lại dữ liệu
                    } catch (Exception ex) {
                        Toast.makeText(MainActivity.this, "Lỗi khi thêm: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showUpdateDialog(final NotesModel note) {
        // Use a Dialog to show update UI and map views with dialog.findViewById
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        // fixed: use edit layout
        dialog.setContentView(R.layout.dialog_edit_notes);

        // Ánh xạ các thành phần trong dialog cập nhật
        final EditText editText = dialog.findViewById(R.id.editTextName);
        Button buttonEdit = dialog.findViewById(R.id.buttonEdit);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);

        // Prefill current name
        editText.setText(note.getNameNote());

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editText.getText().toString().trim();
                if (newName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên note", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (databaseHandler != null)
                        databaseHandler.QueryData("UPDATE Notes SET NameNotes='" + newName.replace("'","''") + "' WHERE Id=" + note.getIdNote());
                    databaseSQLite();
                    Toast.makeText(MainActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, "Lỗi khi cập nhật: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Hàm dialog cập nhật Notes
    public void DialogCapNhatNotes(String name, int id) {
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_notes);

        // Ánh xạ
        final EditText editText = dialog.findViewById(R.id.editTextName);
        Button buttonEdit = dialog.findViewById(R.id.buttonEdit);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);
        editText.setText(name);

        // Bắt sự kiện
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editText.getText().toString().trim();
                if (newName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên Notes", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Escape single quotes
                String escaped = newName.replace("'","''");
                try {
                    if (databaseHandler != null)
                        databaseHandler.QueryData("UPDATE Notes SET NameNotes = '" + escaped + "' WHERE Id = " + id);
                    Toast.makeText(MainActivity.this, "Đã cập nhật Notes thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    databaseSQLite(); // Gọi hàm load lại dữ liệu
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Dialog xác nhận xóa (updated per request)
    public void DialogDelete(String name, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xóa Notes " + name + " này không ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (databaseHandler != null)
                        databaseHandler.QueryData("DELETE FROM Notes WHERE Id = '" + id + "'");
                    Toast.makeText(MainActivity.this, "Đã xóa Notes " + name + " thành công", Toast.LENGTH_SHORT).show();
                    databaseSQLite(); // Gọi hàm load lại dữ liệu
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Không làm gì, chỉ đóng dialog
            }
        });
        builder.show();
    }

    private void InitDatabaseSQLite() {
        // Khởi tạo database
        databaseHandler = new DatabaseHandler(this, "notes.sqlite", null, 1);
        // Tạo bảng Notes
        try {
            if (databaseHandler != null)
                databaseHandler.QueryData("CREATE TABLE IF NOT EXISTS Notes(Id INTEGER PRIMARY KEY AUTOINCREMENT, NameNotes VARCHAR(200))");
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tạo bảng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void databaseSQLite(){
        // Lấy dữ liệu
        Cursor cursor = null;
        try {
            cursor = databaseHandler.GetData("SELECT * FROM Notes");

            // Xoá dữ liệu cũ trong danh sách
            arrayList.clear();

            while (cursor.moveToNext()){
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                arrayList.add(new NotesModel(id, name));
                //Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi đọc dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        adapter.notifyDataSetChanged();
    }
}
