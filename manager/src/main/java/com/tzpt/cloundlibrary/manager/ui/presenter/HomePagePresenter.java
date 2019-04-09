package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.ui.contract.HomePageContract;

/**
 * 首页
 * Created by Administrator on 2017/6/23.
 */
public class HomePagePresenter extends RxPresenter<HomePageContract.View> implements HomePageContract.Presenter {

    @Override
    public void getLoginInfo() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (libraryInfo != null) {
            String hallCode = libraryInfo.mHallCode;
            String libraryName = libraryInfo.mName;

            if (!TextUtils.isEmpty(hallCode) && !TextUtils.isEmpty(libraryName)) {
                String operatorInfo;
                switch (libraryInfo.mLibraryStatus) {
                    //1:正常 2:停用 3:屏蔽
                    case 1:
                        operatorInfo = hallCode + "-" + libraryInfo.mOperaterName;
                        break;
                    case 2:
                        operatorInfo = hallCode + "-停用";
                        break;
                    case 3:
                        operatorInfo = hallCode + "-屏蔽";
                        break;
                    default:
                        operatorInfo = "";
                        break;
                }
                mView.setLibraryName(libraryName, operatorInfo, libraryInfo.mLibraryStatus == 1);
            } else {
                mView.setLibraryName("暂无图书馆信息", "", false);
            }
        } else {
            mView.setNoLoginPermission(R.string.operate_timeout);
            mView.setLibraryName("暂无图书馆信息", "", false);
        }
    }

    @Override
    public void delLibraryInfo() {
        DataRepository.getInstance().delLibraryInfo();
    }

    @Override
    public boolean checkPermission(int checkType) {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        switch (checkType) {
            case 0://开放时间
                return libraryInfo.mOpenTimePermission;
            case 1://借书
                return libraryInfo.mBorrowPermission;
            case 2://还书
                return libraryInfo.mReturnPermission;
            case 3://流出
                return libraryInfo.mCirculateOutPermission;
            case 4://流入
                return libraryInfo.mCirculateInPermission;
            case 5://读者
                return libraryInfo.mReaderManagePermission;
            case 6://统计
                return libraryInfo.mCountBookPermission
                        || libraryInfo.mCebitBookPermission
                        || libraryInfo.mBorrowBookPermission
                        || libraryInfo.mReturnBookPermission
                        || libraryInfo.mDompensateBookPermission
                        || libraryInfo.mSellPermission
                        || libraryInfo.mDepositPermission
                        || libraryInfo.mReaderPermission
                        || libraryInfo.mAppDepositPermission;
        }
        return false;
    }

}
