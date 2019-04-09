package com.tzpt.cloundlibrary.manager.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tzpt.cloundlibrary.manager.R;

/**
 * 开发时间设置
 * Created by Administrator on 2017/7/16.
 */

public class VerifyPasswordDialogFragment extends DialogFragment {

    private EditText editTextPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_vefify_password, null);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        Button buttonConfirm = (Button) view.findViewById(R.id.buttonConfirm);
        Button buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
        builder.setView(view);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    String password = editTextPassword.getText().toString().trim();
                    mListener.onConfirmClickComplete(password);
                    dismiss();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }

    private VerifyPasswordListener mListener;

    public void setVerifyPasswordListener(VerifyPasswordListener listener) {
        mListener = listener;
    }

    public interface VerifyPasswordListener {
        void onConfirmClickComplete(String password);
    }

}
