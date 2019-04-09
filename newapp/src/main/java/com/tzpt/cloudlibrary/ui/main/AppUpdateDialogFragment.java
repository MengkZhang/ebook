package com.tzpt.cloudlibrary.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.AppVersionBean;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * 版本检查
 * Created by Administrator on 2017/7/16.
 */
public class AppUpdateDialogFragment extends DialogFragment {

    @BindView(R.id.app_update_title)
    TextView mAppUpdateTitleTv;
    @BindView(R.id.app_update_sub_title)
    TextView mAppUpdateSubTitleTv;
    @BindView(R.id.app_update_cancel_tv)
    TextView mAppUpdateCancelTv;
    @BindView(R.id.app_update_content)
    ListView mAppUpdateContent;
    Unbinder unbinder;
    private String mDownLoadUrl;

    private static final String APP_UPDATE_BEAN = "app_update_bean";
    private boolean mShowDialog = false;

    private boolean mIsFocusUpdate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT > 19) {
            builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_app_update, null);
        builder.setView(view);
        setCancelable(false);
        unbinder = ButterKnife.bind(this, view);
        AlertDialog dialog = builder.create();
//        Window window = dialog.getWindow();
//        if (null != window) {
//            window.setWindowAnimations(R.style.dialogWindowAnim);
//        }
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (mShowDialog) {
            return;
        }
        mShowDialog = true;
        super.show(manager, tag);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mShowDialog = false;
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        mShowDialog = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.app_update_cancel_tv, R.id.app_update_confirm_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.app_update_cancel_tv:
                dismiss();
                break;
            case R.id.app_update_confirm_tv:
                if (null != mDownLoadUrl) {
                    initSDCardPermission();
                }
                break;
        }
    }

    //初始化摄像头权限
    private void initSDCardPermission() {
        AppUpdateManager.getInstance().initContext(getActivity());
        if (Build.VERSION.SDK_INT < 23) {
            AppUpdateManager.getInstance().downLoadApp(mDownLoadUrl);
            if (mIsFocusUpdate) {
                getActivity().finish();
            }
            dismiss();

            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.setLogging(true);
            rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Permission>() {
                        @Override
                        public void call(Permission permission) {
                            if (permission.granted) {                                   //权限已授权
                                AppUpdateManager.getInstance().downLoadApp(mDownLoadUrl);
                                if (mIsFocusUpdate) {
                                    getActivity().finish();
                                }
                                dismiss();
                            } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                            } else {//没有权限,不能使用权限模块-去设置权限
                                showAppUpdatePermissionWindow();
                            }
                        }
                    });
        }
    }

    private void showAppUpdatePermissionWindow() {
        ToastUtils.showSingleToast("没有权限");
    }

    //设置APP更新
    public void setAppUpdateBean(AppVersionBean appUpdateBean) {
        if (null != appUpdateBean) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(APP_UPDATE_BEAN, appUpdateBean);
            setArguments(bundle);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppVersionBean appUpdateBean = (AppVersionBean) getArguments().getSerializable(APP_UPDATE_BEAN);
        if (null != appUpdateBean) {
            this.mDownLoadUrl = appUpdateBean.mHref;
            String title;
            if (TextUtils.isEmpty(appUpdateBean.mTitle)) {
                title = "新版本升级";
            } else {
                title = appUpdateBean.mTitle;
            }
            mAppUpdateTitleTv.setText(title);
            if (TextUtils.isEmpty(appUpdateBean.mSubTitle)) {
                mAppUpdateSubTitleTv.setText("更新内容：");
            } else {
                mAppUpdateSubTitleTv.setText(appUpdateBean.mSubTitle);
            }

            UpdateVersionContentAdapter adapter = new UpdateVersionContentAdapter(getActivity(), appUpdateBean.mContents);
            mAppUpdateContent.setAdapter(adapter);

            if (appUpdateBean.mForceUpdate == 1) {
                mIsFocusUpdate = true;
                mAppUpdateCancelTv.setVisibility(View.GONE);
            } else {
                mIsFocusUpdate = false;
                mAppUpdateCancelTv.setVisibility(View.VISIBLE);
            }
        }
    }
}
