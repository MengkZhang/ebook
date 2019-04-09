package com.tzpt.cloundlibrary.manager.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;

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
        TextView tipTv = (TextView) v.findViewById(R.id.loading_tips_tv);
        if (!TextUtils.isEmpty(tip)) {
            tipTv.setText(tip);
        } else {
            tipTv.setVisibility(View.GONE);
        }
        CustomLoadingDialog dialog = new CustomLoadingDialog(context, R.style.loading_dialog);
        dialog.setContentView(v,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        return dialog;
    }


}
