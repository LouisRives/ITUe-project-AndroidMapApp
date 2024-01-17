package com.example.petproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private long id;
    private String text;

    // Getter and setter for id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Comment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    protected Comment(Parcel in) {
        id = in.readLong();
        text = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(text);
    }
}
