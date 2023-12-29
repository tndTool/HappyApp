package com.example.happyapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {
    private String questionText;
    private String answerText;

    public Question(String questionText, String answer) {
        this.questionText = questionText;
        this.answerText = answer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    // Parcelable implementation
    protected Question(Parcel in) {
        questionText = in.readString();
        answerText = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionText);
        dest.writeString(answerText);
    }
}
