package com.example.administrator.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ContentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        LinearLayout im = (LinearLayout) view.findViewById(R.id.image);

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P.le("getOclick");
            }
        });
        return view;
    }

}  