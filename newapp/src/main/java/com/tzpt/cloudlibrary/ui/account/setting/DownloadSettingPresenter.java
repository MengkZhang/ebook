package com.tzpt.cloudlibrary.ui.account.setting;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.receiver.NetStatusReceiver;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DownloadSettingPresenter extends RxPresenter<DownloadSettingContract.View>
        implements DownloadSettingContract.Presenter {

    @Override
    public void checkMobileNetAble() {
        mView.setCheckBoxStatus(VideoRepository.getInstance().checkMobileNetAble());
    }

    @Override
    public void setMobileNetAble(boolean isAble) {
        VideoRepository.getInstance().setMobileNetAble(isAble);
        mView.setCheckBoxStatus(isAble);
        if (isAble){
            if (NetStatusReceiver.mIsMobileNetConnected) {
                Subscription subscription = Observable.just(VideoRepository.getInstance().getAllDownloadingVideoList())
                        .map(new Func1<List<VideoBean>, Void>() {
                            @Override
                            public Void call(List<VideoBean> videoBeans) {
                                for (VideoBean item : videoBeans) {
                                    if (item.getStatus() == DownloadStatus.ERROR
                                            || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR
                                            || item.getStatus() == DownloadStatus.NO_NET_ERROR) {
                                        VideoRepository.getInstance().startDownload(item);
                                    }
                                }
                                return null;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                addSubscrebe(subscription);
            }
        }
    }

    @Override
    public void changeMobileNetAble() {
        boolean isMobileNetAble = VideoRepository.getInstance().checkMobileNetAble();
        if (isMobileNetAble) {
            setMobileNetAble(false);
        } else {
            mView.showDialog(R.string.open_switch_download_under_mobile_net_tip,
                    R.string.open_mobile_net_switch,
                    R.string.cancel);
        }
    }


}
