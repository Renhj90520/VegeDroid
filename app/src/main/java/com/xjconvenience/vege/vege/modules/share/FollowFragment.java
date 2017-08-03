package com.xjconvenience.vege.vege.modules.share;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xjconvenience.vege.vege.R;

/**
 * Created by Ren Haojie on 2017/8/3.
 */

public class FollowFragment extends Fragment {
    private static final String TYPEKEY = "page";
    private static final String SRCKEY = "src";
    private String src;
    private int type;

    public static FollowFragment newInstance(int type, String src) {
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putInt(TYPEKEY, type);
        args.putString(SRCKEY, src);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(TYPEKEY, 0);
        if (type != 0) {
            src = getArguments().getString(SRCKEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follow_fragment, container, false);
        ImageView qrImage = (ImageView) view.findViewById(R.id.qr_src);
        if (type == 0) {
            qrImage.setImageResource(R.drawable.qr_follow);
        } else {
            Glide.with(getContext())
                    .load(src)
                    .into(qrImage);
        }
        return view;
    }
}
