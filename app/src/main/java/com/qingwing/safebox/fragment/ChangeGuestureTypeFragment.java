package com.qingwing.safebox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.qingwing.safebox.R;

public class ChangeGuestureTypeFragment extends Fragment {
    private ExpandableListView expand_listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_guesture, null);
        ImageView calback = (ImageView) view.findViewById(R.id.calback);
        calback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }
}
