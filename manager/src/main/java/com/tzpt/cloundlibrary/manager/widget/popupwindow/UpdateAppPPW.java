package com.tzpt.cloundlibrary.manager.widget.popupwindow;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.UpdateAppBean;
import com.tzpt.cloundlibrary.manager.modle.remote.downloadmanager.YTAppUpdateUtils;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.adapter.UpdateAppAdapter;
import com.tzpt.cloundlibrary.manager.utils.ActivityManager;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.utils.ToastUtils;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.decoration.DividerDecoration;

import rx.functions.Action1;


/**
 * 更新APP 窗口
 *
 * @author JiaZhiqiang
 */
public class UpdateAppPPW extends PopupWindow implements View.OnClickListener, PopupWindow.OnDismissListener {

    private Activity mActivity;
    private UpdateAppBean mBean;

    public UpdateAppPPW(Activity activity, View view, UpdateAppBean bean) {
        this.mActivity = activity;
        this.mBean = bean;
        View views = View.inflate(mActivity, R.layout.popwindow_app_update, null);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(false);
        setOutsideTouchable(false);
        setContentView(views);
        showAtLocation(view, Gravity.CENTER, 0, 0);

        RelativeLayout showLayoutLl = (RelativeLayout) views.findViewById(R.id.layout_show_ll);
        TextView updateTitleTv = (TextView) views.findViewById(R.id.update_title_tv);
        TextView updateContentTv = (TextView) views.findViewById(R.id.update_content_tv);
        TextView updateConfirmTv = (TextView) views.findViewById(R.id.update_confirm_tv);
        TextView updateCancelTv = (TextView) views.findViewById(R.id.update_cancel_tv);
        RecyclerView recyclerView = (RecyclerView) views.findViewById(R.id.recycler_view);
        View lineView = views.findViewById(R.id.update_center_line_v);

        updateConfirmTv.setOnClickListener(this);
        updateCancelTv.setOnClickListener(this);

        Animation slideInBottom = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_bottom);
        showLayoutLl.startAnimation(slideInBottom);
        setOnDismissListener(this);

        if (null != bean) {
            updateTitleTv.setText(TextUtils.isEmpty(bean.updateTitle) ? "新版本升级" : bean.updateTitle);
            updateContentTv.setText(TextUtils.isEmpty(bean.updateSubTitle) ? "更新内容:" : bean.updateSubTitle);
            if (bean.forceUpdate == 1) {
                updateCancelTv.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
                updateConfirmTv.setBackgroundResource(R.drawable.bg_dialog_btn_center);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(mActivity, android.R.color.white), (int) Utils.dpToPx(mActivity, 8), 0, 0);
            itemDecoration.setDrawLastItem(false);
            recyclerView.addItemDecoration(itemDecoration);
            UpdateAppAdapter adapter = new UpdateAppAdapter(mActivity);
            recyclerView.setAdapter(adapter);
            adapter.clear();
            adapter.addAll(bean.updateContentList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_cancel_tv:
                dismiss();
                break;
            case R.id.update_confirm_tv:
                initStoragePermission();
                break;
        }
    }

    //下载APP
    private void downLoadApp() {
        if (null != mBean) {
            String url = mBean.href;
            if (TextUtils.isEmpty(url)) {
                ToastUtils.showSingleToast("下载地址错误！");
                return;
            }
            if (!TextUtils.isEmpty(url) && !url.endsWith(".apk")) {
                ToastUtils.showSingleToast("下载地址错误！");
                dismiss();
                return;
            }
            download(url);
            dismiss();
            if (null != mBean && mBean.forceUpdate == 1) {
//                ActivityManager.getInstance().finishAllActivity();
            }
        }
    }

    //初始化内存存储权限
    private void initStoragePermission() {
        if (Build.VERSION.SDK_INT < 23) {
            downLoadApp();
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            downLoadApp();
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {
                            showPermissionPopUpWindow();
                        }

                    }
                });
    }

    private PermissionsDialogFragment mSetCameraDialogFragment;

    //展示设置权限弹窗
    private void showPermissionPopUpWindow() {
        if (null == mSetCameraDialogFragment) {
            mSetCameraDialogFragment = new PermissionsDialogFragment();
        }
        if (mSetCameraDialogFragment.isAdded()) {
            return;
        }
        mSetCameraDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_STORAGE);
        mSetCameraDialogFragment.show(mActivity.getFragmentManager(), "PermissionsDialogFragment");
    }

    /**
     * 开始下载
     *
     * @param url
     */
    public void download(String url) {
        if (!canDownloadState(mActivity)) {
            ToastUtils.showSingleToast("下载管理不可用！");
            return;
        }
        YTAppUpdateUtils.download(mActivity, url, mActivity.getResources().getString(R.string.app_name));
    }

    /**
     * 下载管理是否可用
     *
     * @param context
     * @return
     */
    private boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public void onDismiss() {

    }

    /**
     * popUpWindow 显示位置
     *
     * @param anchorView
     * @param xoff
     * @param yoff
     */
    @Override
    public void showAsDropDown(View anchorView, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchorView.getGlobalVisibleRect(rect);
            int h = anchorView.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchorView, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }
}
