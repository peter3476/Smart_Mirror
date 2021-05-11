package com.example.smart_mirror.HOME;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smart_mirror.BOARD.FreeBoard_Activity;
import com.example.smart_mirror.FragTabLayout.HairLoss_Information;
import com.example.smart_mirror.Inconvenience_Sending;
import com.example.smart_mirror.RESULT.HairLoss_Result;
import com.example.smart_mirror.MYPAGE.MyPage_Activity;
import com.example.smart_mirror.MAP.Maps_HairLoss_Clinic_Activity;
import com.example.smart_mirror.R;
import com.example.smart_mirror.RESULT.Result_Diary;
import com.example.smart_mirror.RESULT.Smile_Activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.makeramen.roundedimageview.RoundedImageView;

public class Home_Fragment extends Fragment {
    ViewGroup viewGroup;

    String id;

//    Button Hair_Loss;
//    Button Smile;
//    Button Clinic;
//    Button FreeBoard;
//    Button MyPage;

    RoundedImageView Information, Clinic, Diary;
    Button Result, CreateQR;

    ImageView iv_Img1;
    ImageView iv_Img2;
    ImageView iv_Img3;

    BottomNavigationView bottomNavigationView;

    Home_Fragment home_Fragment;

    ViewFlipper viewFlipper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.home_fragment, container, false);

        viewFlipper = (ViewFlipper) viewGroup.findViewById(R.id.viewF);

        iv_Img1 = (ImageView) viewGroup.findViewById(R.id.iv_img1);
        iv_Img2 = (ImageView) viewGroup.findViewById(R.id.iv_img2);
        iv_Img3 = (ImageView) viewGroup.findViewById(R.id.iv_img3);

//        Hair_Loss = (Button) viewGroup.findViewById(R.id.HairLoss);
//        Smile = (Button) viewGroup.findViewById(R.id.Smile);
//        Clinic = (Button) viewGroup.findViewById(R.id.Hospital);
//        FreeBoard = (Button) viewGroup.findViewById(R.id.freeBoard);
//        MyPage = (Button) viewGroup.findViewById(R.id.Home_MyPage);

        Information = (RoundedImageView) viewGroup.findViewById(R.id.Information);
        Clinic = (RoundedImageView) viewGroup.findViewById(R.id.Clinic);
        Diary = (RoundedImageView) viewGroup.findViewById(R.id.Diary);
        Result = (Button) viewGroup.findViewById(R.id.result_Btn);
        CreateQR = (Button) viewGroup.findViewById(R.id.CreateQR_Btn);


        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(3000);

        Bundle bundle = getArguments();

        id = bundle.getString("id");


        // 홈 화면에서 각 버튼을 클릭했을 때의 동작을 작성. -> 액티비티 이동
//        Hair_Loss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), HairLoss_Result.class);
//                startActivity(intent);
//            }
//        });
//        Smile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), Smile_Activity.class);
//                startActivity(intent);
//            }
//        });

        CreateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateQR.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        Information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HairLoss_Information.class);
                startActivity(intent);
            }
        });

        Clinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.kr/maps/search/%ED%83%88%EB%AA%A8+%ED%81%B4%EB%A6%AC%EB%8B%89"));
                startActivity(intent);
            }
        });

        Diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Inconvenience_Sending.class);
                startActivity(intent);
            }
        });

        Result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Result_Diary.class);
                intent.putExtra("id", id);

                startActivity(intent);
            }
        });

//        FreeBoard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), FreeBoard_Activity.class);
//                intent.putExtra("id", id);
//                startActivity(intent);
//            }
//        });
//        MyPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MyPage_Activity.class);
//                intent.putExtra("id", id);
//                startActivity(intent);
//            }
//        });


        return viewGroup;
    }
}
