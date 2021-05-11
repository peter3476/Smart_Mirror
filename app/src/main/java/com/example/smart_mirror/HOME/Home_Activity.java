package com.example.smart_mirror.HOME;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_mirror.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class Home_Activity extends AppCompatActivity {

    Intent intent;
    String intent_id;

    Button Hair_Loss;
    Button Smile;
    Button Clinic;
    Button FreeBoard;
    Button MyPage;

    ImageView iv_Img1;
    ImageView iv_Img2;
    ImageView iv_Img3;

    BottomNavigationView bottomNavigationView;

    Home_Fragment home_Fragment;
    MyPage_Fragment myPage_fragment;
    Board_Fragment board_fragment;

    ViewFlipper viewFlipper;

    private Boolean isPermission = true;

    private long BackKeyPressedTime = 0;

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        tedPermission();

        intent = getIntent();
        intent_id = intent.getStringExtra("id");

//        viewFlipper = (ViewFlipper) findViewById(R.id.viewF);

        home_Fragment = new Home_Fragment();
        myPage_fragment = new MyPage_Fragment();

        board_fragment = new Board_Fragment();

//        iv_Img1 = (ImageView) findViewById(R.id.iv_img1);
//        iv_Img2 = (ImageView) findViewById(R.id.iv_img2);
//        iv_Img3 = (ImageView) findViewById(R.id.iv_img3);
//
//        Hair_Loss = (Button) findViewById(R.id.HairLoss);
//        Smile = (Button) findViewById(R.id.Smile);
//        Clinic = (Button) findViewById(R.id.Hospital);
//        FreeBoard = (Button) findViewById(R.id.freeBoard);
//        MyPage = (Button) findViewById(R.id.Home_MyPage);

        Bundle bundle = new Bundle();
        bundle.putString("id", intent_id);
        home_Fragment.setArguments(bundle);
        myPage_fragment.setArguments(bundle);
        board_fragment.setArguments(bundle);

        // 처음 Home_Activity 들어가자마자 대체되는 화면
        getSupportFragmentManager().beginTransaction().replace(R.id.container_Fragment, home_Fragment).commitAllowingStateLoss();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_Fragment, home_Fragment).commit();

                        return true;

                    case R.id.action_board:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_Fragment, board_fragment).commit();

                        return true;

                    case R.id.action_mypage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_Fragment, myPage_fragment).commit();

                        return true;

                    default:
                        return false;
                }

            }
        });
    }

    /**
     *  권한 설정
     */
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    // 뒤로가기 2번 입력 시 앱 종료.
    @Override
    public void onBackPressed() {
        LayoutInflater inflater = getLayoutInflater();
        View toastDesign = inflater.inflate(R.layout.toast_design_text, (ViewGroup)findViewById(R.id.toast_design_root));

        TextView text = toastDesign.findViewById(R.id.TextView_toast_design);
        text.setText("뒤로가기 버튼을 한 번 더 누르시면 종료됩니다!");

        if (System.currentTimeMillis() > BackKeyPressedTime + 2500) {
            BackKeyPressedTime  = System.currentTimeMillis();
            toast               = new Toast(Home_Activity.this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastDesign);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= BackKeyPressedTime + 2500) {
            toast.cancel();
            toast = Toast.makeText(Home_Activity.this, "이용해주셔서 감사합니다!", Toast.LENGTH_SHORT);
            toast.show();

            finish();
        }

    }
}