package com.tzpt.cloudlibrary.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.widget.CustomLoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基类Fragment
 * Created by Administrator on 2017/5/22.
 */

public abstract class BaseFragment extends Fragment {
    protected FragmentActivity activity;
    protected LayoutInflater inflater;
    protected Context mContext;
    private CustomLoadingDialog mLoadingDialog;//进度条
    Unbinder unbinder;
    public boolean mIsVisible;
    
    public abstract int getLayoutResId();

    public abstract void initDatas();

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public abstract void configViews();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getLayoutResId(), container, false);
        activity = getSupportActivity();
        mContext = activity;
        this.inflater = inflater;
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initDatas();
        configViews();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public FragmentActivity getSupportActivity() {
        return super.getActivity();
    }


    // mLoadingDialog
    private CustomLoadingDialog getDialog(String tips) {
        if (mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.instance(getActivity(), tips);
            mLoadingDialog.setCancelable(false);
        }
        return mLoadingDialog;
    }

    public void hideDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.hide();
    }

    public void showDialog(String tips) {
        if (!getDialog(tips).isShowing()) {
            getDialog(tips).show();
        }
    }

    public void dismissDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            lazyLoad();
        } else {
            mIsVisible = false;
        }
    }

    protected abstract void lazyLoad();

}
