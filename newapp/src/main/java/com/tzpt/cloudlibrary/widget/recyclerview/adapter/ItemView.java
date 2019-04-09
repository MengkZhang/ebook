package com.tzpt.cloudlibrary.widget.recyclerview.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/11/7.
 */

public interface ItemView {
    View onCreateView(ViewGroup parent);

    void onBindView(View headerView);
}
