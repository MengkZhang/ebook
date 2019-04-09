package com.tzpt.cloudlibrary.ui.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.bean.ShareItemBean;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享APP
 * Created by ZhiqiangJia on 2017-08-22.
 */

public class ShareAppPresenter extends RxPresenter<ShareAppContract.View> implements
        ShareAppContract.Presenter,
        WbShareCallback {
    private SsoHandler mSsoHandler;
    private WbShareHandler mWbShareHandler;
    private Tencent mTencent;
    private Activity mActivity;
    private ShareBean mShareBean;
    private Bitmap mShareBitmap;

    /**
     * 获取分享列表
     */
    @Override
    public void getShareItem() {
        if (null != mView && mShareBean != null) {
            List<ShareItemBean> itemBeanList = new ArrayList<>();
            if (mShareBean.mNeedDownload) {
                itemBeanList.add(new ShareItemBean(6, "下载", R.mipmap.ic_share_download));
            }
            if (mShareBean.mNeedCollection) {
                itemBeanList.add(new ShareItemBean(7, mShareBean.mIsCollection ? "取消收藏" : "收藏",
                        mShareBean.mIsCollection ? R.mipmap.ic_share_collectioned : R.mipmap.ic_share_collection));
            }
            itemBeanList.add(new ShareItemBean(0, "微信", R.mipmap.ic_share_wechat));
            itemBeanList.add(new ShareItemBean(1, "朋友圈", R.mipmap.ic_share_circle_friends));
            itemBeanList.add(new ShareItemBean(2, "QQ", R.mipmap.ic_share_qq));
            itemBeanList.add(new ShareItemBean(3, "QQ空间", R.mipmap.ic_share_qzone));
            itemBeanList.add(new ShareItemBean(4, "新浪微博", R.mipmap.ic_share_sina));
            if (mShareBean.mNeedCopy) {
                itemBeanList.add(new ShareItemBean(5, "复制链接", R.mipmap.ic_share_copy));
            }
            mView.setShareItemList(itemBeanList);
        }
    }

    @Override
    public void setShareInfo(Activity activity, ShareBean bean) {
        this.mActivity = activity;
        this.mShareBean = bean;
        if (mShareBean == null) {
            mView.finishActivity();
            return;
        }
        if (!TextUtils.isEmpty(mShareBean.shareImagePath)) {
            GlideApp.with(mActivity)
                    .asBitmap()
                    .load(mShareBean.shareImagePath)
                    .onlyRetrieveFromCache(true)
                    .override(72, 72)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            mShareBitmap = resource;//与图书使用同一个图片，不用做回收处理
                        }
                    });
        }
    }

    @Override
    public void shareQQ() {
        if (null != mView && !ShareConfig.isQQAvaliable(mActivity)) {
            mView.showMsg(R.string.not_installed_qq_client);
            mView.finishActivity();
            return;
        }
        mTencent = Tencent.createInstance(ShareConfig.QQ_APP_KEY, mActivity.getApplicationContext());
        if (null != mTencent) {
            Bundle params = new Bundle();
            //分享链接
            params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TITLE, mShareBean.shareTitle);
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_SUMMARY, mShareBean.shareContent);
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_URL, mShareBean.shareImagePath);
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_APP_NAME, mActivity.getString(R.string.app_name));
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TARGET_URL, mShareBean.shareUrl);
            mTencent.shareToQQ(mActivity, params, mIUiListener);
        }
    }

    private IUiListener mIUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            if (null != mView) {
                mView.shareSuccess();
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (null != mView) {
                mView.shareFailure();
            }
        }

        @Override
        public void onCancel() {
            if (null != mView) {
                mView.shareCancel();
            }
        }
    };

    @Override
    public void shareQZone() {
        if (null != mView && !ShareConfig.isQQAvaliable(mActivity)) {
            mView.showMsg(R.string.not_installed_qq_client);
            mView.finishActivity();
            return;
        }
        mTencent = Tencent.createInstance(ShareConfig.QQ_APP_KEY, mActivity.getApplicationContext());
        if (null != mTencent) {
            Bundle params = new Bundle();
            //分享链接
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);//必填
            params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, mActivity.getString(R.string.app_name));//必填
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mShareBean.shareTitle);//必填
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mShareBean.shareContent);//选填
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mShareBean.shareUrl);//必填
            ArrayList<String> imageList = new ArrayList<>();
            imageList.clear();
            imageList.add(mShareBean.shareImagePath);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageList);
            mTencent.shareToQzone(mActivity, params, mIUiListener);
        }
    }

    @Override
    public void shareWX() {
        shareToWX(0);
    }

    @Override
    public void shareWXFriends() {
        shareToWX(1);
    }

    /**
     * 分享到微信和朋友圈
     *
     * @param type 0微信1朋友圈
     */
    private void shareToWX(int type) {
        //实例化
        IWXAPI wxApi = WXAPIFactory.createWXAPI(mActivity, ShareConfig.WX_APP_KEY);
        if (null != mView && !wxApi.isWXAppInstalled()) {
            mView.showMsg(R.string.not_installed_wx_client);
            return;
        }
        wxApi.registerApp(ShareConfig.WX_APP_KEY);
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = mShareBean.shareUrlForWX;
        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = mShareBean.shareTitle;
        msg.description = mShareBean.shareContent;
        msg.messageExt = mShareBean.shareContent;
        if (null != mShareBitmap) {
            msg.setThumbImage(mShareBitmap);
        } else {
            msg.setThumbImage(BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.ic_logo));
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private boolean mIsSessionValid = false;

    @Override
    public void shareWeiBo() {
        if (null != mView && !ShareConfig.isWeiBoAvaliable(mActivity)) {
            mView.showMsg(R.string.not_installed_sina_client);
            mView.finishActivity();
            return;
        }
        mWbShareHandler = new WbShareHandler(mActivity);
        mWbShareHandler.registerApp();
        //如果token有效，则开始分享，如果token无效，则开始授权
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(mActivity);
        if (!mAccessToken.isSessionValid()) {
            // 创建微博实例
            mIsSessionValid = false;
            mSsoHandler = new SsoHandler(mActivity);
            mSsoHandler.authorizeClientSso(new WeiBoWbAuthListener(mActivity));
        } else {
            //开始分享
            mIsSessionValid = true;
            startShareWeiBo();
        }
    }

    /**
     * 复制链接
     */
    @Override
    public void shareForCopy() {
        if (mShareBean != null) {
            mView.clipboardCopyLink(mShareBean.shareTitle, mShareBean.shareContent, mShareBean.shareUrl);
        }
    }

    private void startShareWeiBo() {
        if (null != mWbShareHandler) {
            WeiboMultiMessage message = new WeiboMultiMessage();
            message.textObject = getTextObj();
            message.mediaObject = getWebpageObj();
            message.imageObject = getImageObj();
            mWbShareHandler.shareMessage(message, true);
        }
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = mShareBean.shareTitle + "\n" + mShareBean.shareContent;
        textObject.title = mShareBean.shareTitle;
        textObject.actionUrl = mShareBean.shareUrl;
        return textObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        //没有链接显示的内容
        mediaObject.title = "";
        mediaObject.description = mShareBean.shareTitle;
        if (null != mShareBitmap) {
            mediaObject.setThumbImage(mShareBitmap);
        } else {
            mediaObject.setThumbImage(BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.ic_logo));
        }
        mediaObject.actionUrl = mShareBean.shareUrl;
        mediaObject.defaultText = mShareBean.shareContent;
        return mediaObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        if (null != mShareBitmap) {
            imageObject.setThumbImage(mShareBitmap);
        } else {
            imageObject.setThumbImage(BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.ic_logo));
        }
        return imageObject;
    }

    /**
     * 微博授权监听
     */
    private class WeiBoWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {

        private Activity activity;

        public WeiBoWbAuthListener(Activity activity) {
            this.activity = activity;
        }

        /**
         * 授权成功
         *
         * @param oauth2AccessToken
         */
        @Override
        public void onSuccess(final Oauth2AccessToken oauth2AccessToken) {
            if (null != activity) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsSessionValid) {
                            if (null != mView) {
                                mView.shareSuccess();
                            }
                        } else {
                            //保存oauth2AccessToken,开启分享
                            if (oauth2AccessToken.isSessionValid()) {
                                mIsSessionValid = true;
                                // 保存 Token 到 SharedPreferences
                                AccessTokenKeeper.writeAccessToken(activity, oauth2AccessToken);
                                //开始分享
                                startShareWeiBo();
                            }
                        }
                    }
                });
            }
        }

        /**
         * 授权取消
         */
        @Override
        public void cancel() {
            if (null != mView) {
                mView.shareCancel();
            }
        }

        /**
         * 授权失败
         */
        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            if (null != mView) {
                mView.shareFailure();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (null != mWbShareHandler) {
            mWbShareHandler.doResultIntent(intent, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        if (null != mTencent) {
            mTencent.onActivityResult(requestCode, resultCode, data);
        }
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onWbShareSuccess() {
        if (null != mView) {
            mView.shareSuccess();
        }
    }

    @Override
    public void onWbShareCancel() {
        if (null != mView) {
            mView.shareCancel();
        }
    }

    @Override
    public void onWbShareFail() {
        if (null != mView) {
            mView.shareFailure();
        }
    }
}
