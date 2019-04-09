package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.IDCardBean;

/**
 * Created by tonyjia on 2018/9/7.
 * 读者身份验证
 */

public interface ReaderAuthenticationContract {

    interface View extends BaseContract.BaseView {
        void checkResult(String readerId);

        void showLoading(String msg);

        void dismissLoading();

        void showDialogScanLoginFailed(String msg);

        void showDialogScanLoginFailed(int msgId);

        void showAutoProcessDepositPenaltyFailed(int msgId);

        void noPermissionPrompt(int msgId);

        void showDialogForFirstScanToCheckInfo(String tel);

        void showDialogForFirstScanToRegister();

        void showDialogForFinish(int msgId);

        void showDialogTips(int msgId);

        void setReaderAuthenticationInfo(IDCardBean icCard, boolean editReaderEnable);

        void needRegisterReader(boolean needRegister);

        void turnToDealPenalty(String readerId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 检查读者身份信息
         *
         * @param idCardBean 身份信息
         */
        void checkReaderAuthenticationInfo(IDCardBean idCardBean);

        /**
         * 登录
         *
         */
        void login(String readerId, boolean dealPenalty);

        /**
         * 更新读者信息
         *
         * @param idCardId     身份ID
         * @param idCardNumber 身份证号码
         * @param readerName   读者名称
         * @param readerSex    读者性别
         * @param nation       读者民族
         */
        void updateReaderInfo(String idCardId, String idCardNumber, String readerName, String readerSex, String nation, boolean dealPenalty);
    }
}
