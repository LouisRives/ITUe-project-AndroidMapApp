package com.example.petproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.widget.Toast;
import android.util.Log;

public class AllCommentsResultActivity extends AppCompatActivity {

    private EditText editTextComment;
    private Comment selectedComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_comments_dialog);

        // Retrieve the selected comment from the intent
        selectedComment = getIntent().getParcelableExtra("selectedComment");

        editTextComment = findViewById(R.id.editTextUpdatedComment);

        // Set the existing comment text in the EditText
        editTextComment.setText(selectedComment.getText());

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateComment();
            }
        });
    }

    private void updateComment() {
        // Get the updated comment text from the EditText
        String updatedText = editTextComment.getText().toString();

        // Update the selected comment with the new text
        selectedComment.setText(updatedText);

        // Log the selected comment ID for debugging
        Log.d("AllCommentsResultActivity", "Selected Comment ID: " + selectedComment.getId());

        // Ensure that selectedComment is not null and has a valid ID
        if (selectedComment != null ) {
            // Update the comment in the database
            DatabaseHelper dbHelper = new DatabaseHelper(AllCommentsResultActivity.this);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_COMMENT, updatedText);

            String selection = DatabaseHelper.COLUMN_ID + " = ?";
            String[] selectionArgs = {String.valueOf(selectedComment.getId())};

            int updatedRows = database.update(
                    DatabaseHelper.TABLE_COMMENTS,
                    values,
                    selection,
                    selectionArgs
            );

            dbHelper.close();

            if (updatedRows > 0) {
                // Optional: You can set a result to indicate success or failure
                setResult(RESULT_OK);
                finish();
                Toast.makeText(this, "Comment updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update comment", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("AllCommentsResultActivity", "Selected Comment is null or has an invalid ID");
        }
    }
}