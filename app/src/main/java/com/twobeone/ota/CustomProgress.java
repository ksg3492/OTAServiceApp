package com.twobeone.ota;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class CustomProgress extends LinearLayout {
    public CustomProgress(Context context) {
        super(context);

        View v = inflate(context, R.layout.layout_progress, this);

//        ProgressBar pb = (ProgressBar) v.findViewById(R.id.pb);
//        pb.setMax(100);
//        pb.setProgress(50);
    }
}
