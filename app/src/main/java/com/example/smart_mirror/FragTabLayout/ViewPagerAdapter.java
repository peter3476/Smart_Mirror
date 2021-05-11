package com.example.smart_mirror.FragTabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    // 호출되면서 Frament 교체를 보여주는 처리를 구현
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return FragMonday.newinstance();
            case 1:
                return FragTuesday.newinstance();
            case 2:
                return FragWednesday.newinstance();
            default:
                return null;
        }
    }

    // 개수를 명시 해줘야함.
    @Override
    public int getCount() {
        // 월요일, 화요일, 수요일
        return 3;
    }

    // 상단의 tablayout indicator 쪽에 선언을 해주는 것.
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "휴지기 탈모";
            case 1:
                return "남성형 탈모";
            case 2:
                return "여성형 탈모";
            default:
                return null;
        }
    }
}
