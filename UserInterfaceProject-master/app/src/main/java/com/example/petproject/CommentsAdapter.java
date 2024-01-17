package com.example.petproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    // Add this method to remove a comment
    public void removeComment(Comment comment) {
        int position = comments.indexOf(comment);
        if (position != -1) {
            comments.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBoxComment;
        private TextView textViewComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxComment = itemView.findViewById(R.id.checkBoxComment);
            textViewComment = itemView.findViewById(R.id.textViewComment);
        }

        public void bind(String comment) {
            textViewComment.setText(comment);
        }
    }

    private List<Comment> comments = new ArrayList<>();
    private OnEditClickListener onEditClickListener;
    private OnItemClickListener onItemClickListener;
    private List<Comment> selectedComments = new ArrayList<>();
    private int selectedCommentIndex = -1; // Variable to store the selected comment index

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnEditClickListener {
        void onEditClick(Comment comment);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    // Add this method to get the selected comment index
    public int getSelectedCommentIndex() {
        return selectedCommentIndex;
    }

    // Modify this method to set the selected comment index
    public void setSelectedCommentIndex(int index) {
        selectedCommentIndex = index;
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    public void setData(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment.getText());

        // CheckBox'ın durumunu izle ve seçilen yorumları güncelle
        holder.checkBoxComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // CheckBox işaretlendiğinde, seçilen yorumları listeye ekle
                    selectedComments.add(comment);
                } else {
                    // CheckBox işareti kaldırıldığında, seçilen yorumları listeden çıkar
                    selectedComments.remove(comment);
                }

                // Log statements for debugging
                int updatedPosition = holder.getAdapterPosition();
                Log.d("CommentsAdapter", "Position: " + updatedPosition + ", isChecked: " + isChecked);

                // Update the selected index
                setSelectedCommentIndex(updatedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
