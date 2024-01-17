package com.example.petproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.sqlite.SQLiteDatabase;

import com.example.petproject.Comment;
import com.example.petproject.CommentsAdapter;
import com.example.petproject.DatabaseHelper;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

public class AllCommentsActivity extends AppCompatActivity implements CommentsAdapter.OnItemClickListener {

    private List<Comment> allComments;
    private CommentsAdapter commentsAdapter;
    private static final String SELECTED_COMMENT_KEY = "selectedComment";
    private static final int EDIT_COMMENT_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);

        String selectedCountry = getIntent().getStringExtra("country");
        if (selectedCountry != null) {
            getSupportActionBar().setTitle(selectedCountry);
        }

        allComments = getIntent().getParcelableArrayListExtra("allComments");
        commentsAdapter = new CommentsAdapter();

        RecyclerView recyclerViewComments = findViewById(R.id.recyclerViewComments);
        commentsAdapter = new CommentsAdapter();
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(commentsAdapter);

        commentsAdapter.setData(allComments);
        commentsAdapter.setOnItemClickListener(this);

        Button editButton = findViewById(R.id.buttonEditComment);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedCommentIndex = commentsAdapter.getSelectedCommentIndex();

                if (selectedCommentIndex != -1) {
                    Comment selectedComment = getSelectedComment(selectedCommentIndex);
                    Intent intent = new Intent(AllCommentsActivity.this, AllCommentsResultActivity.class);
                    intent.putExtra("selectedComment", selectedComment);

                    // Start activity using the launcher
                    startActivity(intent);
                } else {
                    Toast.makeText(AllCommentsActivity.this, "No comment selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button deleteButton = findViewById(R.id.buttonDeleteComment);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedCommentIndex = commentsAdapter.getSelectedCommentIndex();

                if (selectedCommentIndex != -1) {
                    Comment selectedComment = getSelectedComment(selectedCommentIndex);
                    deleteComment(selectedComment);
                    finish();
                    showAllComments(selectedCountry);

                } else {
                    Toast.makeText(AllCommentsActivity.this, "No comment selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void deleteComment(Comment comment) {
        DatabaseHelper dbHelper = new DatabaseHelper(AllCommentsActivity.this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            // Debugging log statements

            Log.d("DeleteComment", "Comment ID to delete: " + comment.getId());

            String selection = DatabaseHelper.COLUMN_ID + " = ?";
            String[] selectionArgs = {String.valueOf(comment.getText())};


            // Debugging log statements
            Log.d("DeleteComment", "Selection: " + selection);
            Log.d("DeleteComment", "SelectionArgs: " + Arrays.toString(selectionArgs));

            int deletedRows = database.delete(
                    DatabaseHelper.TABLE_COMMENTS,
                    selection,
                    selectionArgs
            );
            Log.d("DeleteComment", "Rows to delete:" + deletedRows);
            commentsAdapter.removeComment(comment);

            dbHelper.close();

            if (deletedRows > 0) {
                // Get the selected comment index and remove it from the adapter

                // Refresh the UI after deletion
                refreshUI();
                // Notify the user about the successful deletion
                Toast.makeText(AllCommentsActivity.this, "Comment deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AllCommentsActivity.this, "Failed to delete comment", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("DeleteComment", "Error deleting comment: " + e.getMessage());
            Toast.makeText(AllCommentsActivity.this, "Error deleting comment", Toast.LENGTH_SHORT).show();
        }

    }

    private void showAllComments(String selectedCountry) {
        // Fetch the latest comments from the database
        List<Comment> updatedComments = getAllComments(selectedCountry);

        // Update the RecyclerView adapter with the new data
        commentsAdapter.setData(updatedComments);
        commentsAdapter.notifyDataSetChanged();

        // Update the allComments list
        allComments = updatedComments;

        // Notify the user about the successful deletion
        Toast.makeText(AllCommentsActivity.this, "Comment deleted", Toast.LENGTH_SHORT).show();
    }
    private void refreshUI() {
        allComments = getAllCommentsFromDatabase();
        commentsAdapter.setData(allComments);
        commentsAdapter.notifyDataSetChanged(); // Add this line to notify the adapter about the data change

    }
    private List<Comment> getAllCommentsFromDatabase() {
        String selectedCountry = getIntent().getStringExtra("country");
        return getAllComments(selectedCountry);
    }

    private List<Comment> getAllComments(String country) {
        List<Comment> comments = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(AllCommentsActivity.this);
        //... (Existing code to fetch comments from the database)

        return comments;
    }

    private Comment getSelectedComment(int selectedCommentIndex) {
        if (selectedCommentIndex >= 0 && selectedCommentIndex < allComments.size()) {
            return allComments.get(selectedCommentIndex);
        } else {
            return null;
        }
    }

    @Override
    public void onItemClick(int position) {
        Comment selectedComment = allComments.get(position);
        showEditDialog(selectedComment);
    }

    private void showEditDialog(final Comment selectedComment) {
        Intent intent = new Intent(AllCommentsActivity.this, AllCommentsResultActivity.class);
        intent.putExtra("selectedComment", selectedComment);
        startActivityForResult(intent, EDIT_COMMENT_REQUEST);
    }





}