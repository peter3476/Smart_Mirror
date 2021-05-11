package com.example.smart_mirror.RESULT;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_mirror.MAP.MyDBHelper;
import com.example.smart_mirror.R;


public class Smile_Activity extends AppCompatActivity {

    MyDBHelper myDBHelper;
    private final static int DATABASE_VERSION = 1;


    static String goodScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smile_solution);

        // 데이터베이스에서 인상 측정 점수를 받아와 표시해준다.
        TextView ScoreText = (TextView) findViewById(R.id.score);

        // 데이터베이스를 사용하기 위한 초기화
        myDBHelper = new MyDBHelper(this, DATABASE_VERSION);
        SQLiteDatabase DB;
        DB = myDBHelper.getWritableDatabase();
        Cursor cursor;

        // 데이터베이스에서 select해서 정보를 cursor에 담는다.
        cursor = DB.rawQuery("select goodScore from smileScore", null);
        goodScore = "";

        while (cursor.moveToNext()) {
            String Score = cursor.getString(0);
            goodScore += Score;
        }

        // 데이터베이스에서 불러온 값을 텍스트 필드에 적용한다.
        ScoreText.setText(goodScore + "점");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_smile);

        // Progress Bar에 퍼센티지를 적용하여 색을 채운다.
        Drawable draw = getResources().getDrawable(R.drawable.progressbar_progressbar1);
        progressBar.setProgress(Integer.parseInt(goodScore));
        progressBar.setProgressDrawable(draw);
    }
}
