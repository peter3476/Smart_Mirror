package com.example.smart_mirror.RESULT;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.example.smart_mirror.MAP.MyDBHelper;
import com.example.smart_mirror.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HairLoss_Result extends AppCompatActivity {

    PieChart pieChart;
    MyDBHelper myDBHelper;
    private final static int DATABASE_VERSION = 1;

    static String HairLoss_Rate, NoneHairLoss_Rate;

    // 서버에서 DB로, DB에서 안드로이드 스튜디오로 진단 결과에 대한 데이터를 받아와야하고
    // 그 데이터들을 분류해서 각 변수에 잘 담은 후에, pieChart의 잘 적용시킨다. (탈모 , 비탈모)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView Dan_Grade = (TextView) findViewById(R.id.Dan_grade);

        // 데이터베이스를 사용하기 위한 초기화
        myDBHelper = new MyDBHelper(this, DATABASE_VERSION);
        SQLiteDatabase DB;
        DB = myDBHelper.getWritableDatabase();
        Cursor cursor;

        // 데이터베이스에서 select해서 정보를 cursor에 담는다.
        cursor = DB.rawQuery("select hairloss, nonehairloss from hairlossRate", null);
        HairLoss_Rate = "";
        NoneHairLoss_Rate = "";

        // 데이터베이스를 순회하면서 탈모 / 비탈모의 값을 저장한다.
        while (cursor.moveToNext()) {
            String hairLoss_R = cursor.getString(0);
            String NonehairLoss_R = cursor.getString(1);
            HairLoss_Rate += hairLoss_R;
            NoneHairLoss_Rate += NonehairLoss_R;
        }

        // 데이터베이스에서 불러온 탈모 / 비탈모 비율 값으로 탈모 위험도를 지정한다.
        if (Integer.parseInt(HairLoss_Rate) >= 0 && Integer.parseInt(NoneHairLoss_Rate) <= 19) {
            Dan_Grade.setText("안심");
        } else if (Integer.parseInt(HairLoss_Rate) >= 20 && Integer.parseInt(NoneHairLoss_Rate) <= 39){
            Dan_Grade.setText("양호");
        } else if (Integer.parseInt(HairLoss_Rate) >=40 && Integer.parseInt(NoneHairLoss_Rate) <= 59){
            Dan_Grade.setText("보통");
        } else if (Integer.parseInt(HairLoss_Rate) >= 60 && Integer.parseInt(NoneHairLoss_Rate) <= 79){
            Dan_Grade.setText("경고");
        } else if (Integer.parseInt(HairLoss_Rate) >= 80 && Integer.parseInt(NoneHairLoss_Rate) <= 89){
            Dan_Grade.setText("심각");
        } else if (Integer.parseInt(HairLoss_Rate) >= 90 && Integer.parseInt(NoneHairLoss_Rate) <= 97){
            Dan_Grade.setText("위험");
        } else if (Integer.parseInt(HairLoss_Rate) >= 98 && Integer.parseInt(NoneHairLoss_Rate) <= 100){
            Dan_Grade.setText("고위험");
        }

        pieChart = (PieChart) findViewById(R.id.HairLoss_piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        Float HR = Float.parseFloat(HairLoss_Rate);

        // 데이터베이스에서 탈모 비율 값을 받아와서 pieChart에 적용시킨다.
        yValues.add(new PieEntry(Float.parseFloat(HairLoss_Rate), "탈모"));
        yValues.add(new PieEntry(Float.parseFloat(NoneHairLoss_Rate), "비탈모"));

        System.out.println(HR.getClass().getName());

        Description description = new Description();
        description.setText("탈모 비율");
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(yValues, "hair loss rate");
        dataSet.setSliceSpace(1.5f);    // 나뉘는 부분 사이 조절
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);     // pieChart에 보여지는 색상을 JOYFUL_COLORS로 안드로이드 스튜디오에서 제공되는 색상을 사용한다.

        PieData data = new PieData((dataSet));
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.parseColor("#6361e8"));

        pieChart.setData(data);
    }
}