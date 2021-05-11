package com.example.smart_mirror.FragTabLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smart_mirror.R;

public class FragWednesday extends Fragment {

    private View view;

    public static FragWednesday newinstance() {
        FragWednesday fragWednesday = new FragWednesday();
        return fragWednesday;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_womanhairloss_info, container, false);

        return view;

    }
}
