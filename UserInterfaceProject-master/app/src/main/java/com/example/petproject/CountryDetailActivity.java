package com.example.petproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.List;
public class CountryDetailActivity extends AppCompatActivity {
    private String selectedCountry;
    private EditText editTextComment;
    private ImageView imageView;
    private Button buttonSave;
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail);

        selectedCountry = getIntent().getStringExtra("country");
        if (selectedCountry != null) {
            getSupportActionBar().setTitle(selectedCountry);
        }

        editTextComment = findViewById(R.id.editTextComment);
        buttonSave = findViewById(R.id.buttonSave);
        recyclerView = findViewById(R.id.recyclerViewComments);
        commentsAdapter = new CommentsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(commentsAdapter);

        Button buttonShowAllComments = findViewById(R.id.buttonShowAllComments);
        buttonShowAllComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllComments(selectedCountry);
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = editTextComment.getText().toString();
                saveCommentToDatabase(selectedCountry, comment);
                updateCommentsList(selectedCountry);
                editTextComment.setText("");
            }
        });

        updateCommentsList(selectedCountry);
    }

    private void saveCommentToDatabase(String selectedCountry, String comment) {
        if (comment.trim().isEmpty()) {
            Toast.makeText(CountryDetailActivity.this, "Comment can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseHelper dbHelper = new DatabaseHelper(CountryDetailActivity.this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COUNTRY, selectedCountry);
        values.put(DatabaseHelper.COLUMN_COMMENT, comment);
        ;
        long newRowId = database.insert(DatabaseHelper.TABLE_COMMENTS, null, values);

        dbHelper.close();

        Toast.makeText(CountryDetailActivity.this, "Comment saved to database", Toast.LENGTH_SHORT).show();
    }

    private void updateCommentsList(String selectedCountry) {
        DatabaseHelper dbHelper = new DatabaseHelper(CountryDetailActivity.this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseHelper.COLUMN_COMMENT};
        String selection = DatabaseHelper.COLUMN_COUNTRY + " = ?";
        String[] selectionArgs = {selectedCountry};

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_COMMENTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<Comment> commentList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String commentText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT));
            Comment comment = new Comment(commentText);
            commentList.add(comment);
        }

        cursor.close();
        dbHelper.close();

        commentsAdapter.setData(commentList);

        // See All button control
        Button buttonShowAllComments = findViewById(R.id.buttonShowAllComments);
        if (commentList.size() > 0) {
            buttonShowAllComments.setVisibility(View.VISIBLE);
            buttonShowAllComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAllComments(selectedCountry);
                }
            });
        } else {
            buttonShowAllComments.setVisibility(View.GONE);
        }
    }


    private void showAllComments(String selectedCountry) {
        Intent intent = new Intent(CountryDetailActivity.this, AllCommentsActivity.class);
        intent.putExtra("country", selectedCountry);

        // Assuming getAllComments returns a List<Comment>
        List<Comment> allComments = getAllComments(selectedCountry);

        intent.putParcelableArrayListExtra("allComments", new ArrayList<>(allComments));
        startActivity(intent);
    }


    private List<Comment> getAllComments(String selectedCountry) {
        DatabaseHelper dbHelper = new DatabaseHelper(CountryDetailActivity.this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseHelper.COLUMN_COMMENT};
        String selection = DatabaseHelper.COLUMN_COUNTRY + " = ?";
        String[] selectionArgs = {selectedCountry};

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_COMMENTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<Comment> commentList = new ArrayList<>();
        int counter = 1;
        while (cursor.moveToNext()) {
            String commentText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT));
            Comment comment = new Comment(commentText);
            commentList.add(comment);
        }

        cursor.close();
        dbHelper.close();

        return commentList;
    }
}