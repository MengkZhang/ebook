package com.tzpt.cloudlibrary.ui.share;

import android.app.Activity;
import android.content.Intent;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.bean.ShareItemBean;

import java.util.List;

/**
 * Created by ZhiqiangJia on 2017-08-22.
 */

public class ShareAppContract {

    interface View extends BaseContract.BaseView {

        void setShareItemList(List<ShareItemBean> shareItemList);

        void clipboardCopyLink(String title, String description, String url);

        void shareSuccess();

        void shareCancel();

        void shareFailure();

        void showMsg(int msgId);

        void finishActivity();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getShareItem();

        void setShareInfo(Activity activity, ShareBean bean);

        void shareQQ();

        void shareQZone();

        void shareWX();

        void shareWXFriends();

        void shareWeiBo();

        void shareForCopy();

        void onNewIntent(Intent intent);

        void onActivityResult(int requestCode, int resultCode, Intent data);

    }
}
