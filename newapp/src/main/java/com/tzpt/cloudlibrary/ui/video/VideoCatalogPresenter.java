package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.modle.VideoRepository;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 视频目录
 * Created by tonyjia on 2018/6/27.
 */
public class VideoCatalogPresenter extends RxPresenter<VideoCatalogContract.View> implements
        VideoCatalogContract.Presenter {


    public VideoCatalogPresenter(VideoCatalogContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    private List<VideoTOCTree> mVideoTOCTrees;
    private long mChooseSectionId;

    /**
     * 获取视频目录列表
     */
    @Override
    public void getVideoCatalogList() {
        if (null != mView) {
            if (null != mVideoTOCTrees && mVideoTOCTrees.size() > 0) {
                mView.setVideoCatalogList(mVideoTOCTrees, mVideoTOCTrees.get(0).subtrees().get(0).getId());
            } else {
                mView.setVideoCatalogEmptyList();
            }
        }
    }

    @Override
    public void setVideosCatalogList(List<VideoTOCTree> videoTOCTrees) {
        this.mVideoTOCTrees = videoTOCTrees;
    }

    /**
     * 更新选中视频下标
     *
     * @param sectionId 视频ID
     */
    @Override
    public void updateCatalogChooseSectionId(long sectionId) {
        this.mChooseSectionId = sectionId;
        checkCatalogChooseSectionInfo();
    }

    /**
     * 检查视频下标更新信息
     */
    @Override
    public void checkCatalogChooseSectionInfo() {
        if (null != mView && mChooseSectionId > 0) {
            mView.updateCatalogChooseInfo(mChooseSectionId);
        }
    }
}
