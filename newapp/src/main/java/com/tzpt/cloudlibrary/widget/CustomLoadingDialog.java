package com.tzpt.cloudlibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * Created by Administrator on 2017/6/21.
 */

public class CustomLoadingDialog extends Dialog {

    public CustomLoadingDialog(Context context) {
        this(context, 0);
    }

    private CustomLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    public static CustomLoadingDialog instance(Context context, String tip) {
        View v = View.inflate(context, R.layout.view_loading_progress, null);
        if (!TextUtils.isEmpty(tip)) {
            TextView tipTv = (TextView) v.findViewById(R.id.loading_tips_tv);
            tipTv.setText(tip);
        }
        CustomLoadingDialog dialog = new CustomLoadingDialog(context, R.style.loading_dialog);
        dialog.setContentView(v,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        return dialog;
    }


}
