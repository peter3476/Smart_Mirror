package com.example.smart_mirror.MAP;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_mirror.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Maps_HairLoss_Clinic_Activity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    static String Clinic_Name, Clinic_Latitude, Clinic_Longitude;

    MyDBHelper myDBHelper;
    private final static int DATABASE_VERSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_hairloss);

        // 액티비티 화면에 프라그먼트로 지도를 보여준다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_hairloss_clinic);
        mapFragment.getMapAsync(this);

        // 데이터베이스를 사용하기 위한 초기화
        myDBHelper = new MyDBHelper(this, DATABASE_VERSION);
        SQLiteDatabase DB;
        DB = myDBHelper.getWritableDatabase();
        Cursor cursor;


        final ListView ListView = (ListView) findViewById(R.id.HairLoss_Clinic_List);

        final List<String> List = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, List);

        // 데이터베이스에서 select해서 정보를 cursor에 담는다.
        cursor = DB.rawQuery("select name, latitude, longitude from hairlossclinic", null);
        Clinic_Name = "";
        Clinic_Latitude = "";
        Clinic_Longitude = "";

        // select해서 cursor에 담은 정보를 순회하면서 각 정보들을 리스트에 추가한다.
        while (cursor.moveToNext()) {
            String Name = cursor.getString(0);
            String Latitude = cursor.getString(1);
            String Longitude = cursor.getString(2);
            Clinic_Name += Name;
            Clinic_Latitude += Latitude;
            Clinic_Longitude += Longitude;
            List.add(Clinic_Name);
            Clinic_Name = "";
        }
        ListView.setAdapter(adapter);       // 추가된 리스트를 리스트뷰에 적용시킨다.
    }


    // 구글 맵을 이용하기 위한 메소드.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Google API를 활용하기 위한 객체에 DB에서 불러온 위도 / 경도 값을 지정한다.
        LatLng MAP_Location = new LatLng(Double.parseDouble(Clinic_Latitude), Double.parseDouble(Clinic_Longitude));

        // 해당 위도 경도에 해당하는 위치에 마커로 표시한다.
        MarkerOptions markerOptions_Clinic = new MarkerOptions();
        markerOptions_Clinic.position(MAP_Location);
        mMap.addMarker(markerOptions_Clinic);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(MAP_Location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }
}

