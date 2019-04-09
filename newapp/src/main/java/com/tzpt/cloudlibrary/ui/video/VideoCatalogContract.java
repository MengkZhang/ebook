package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;

import java.util.List;

/**
 * 视频目录
 * Created by tonyjia on 2018/6/27.
 */
public interface VideoCatalogContract {

    interface View extends BaseContract.BaseView {

        void setPresenter(VideoCatalogContract.Presenter presenter);

        void setVideoCatalogList(List<VideoTOCTree> videoTOCTrees, long firstPlayId);

        void setVideoCatalogEmptyList();

        void updateCatalogChooseInfo(long sectionId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getVideoCatalogList();

        void setVideosCatalogList(List<VideoTOCTree> videoTOCTrees);

        void updateCatalogChooseSectionId(long sectionId);

        void checkCatalogChooseSectionInfo();
    }
}
