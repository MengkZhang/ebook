package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;

/**
 * 登录信息
 * Created by Administrator on 2017/6/27.
 */

public class LibraryInfo implements Serializable {

//    public int mAgreement;                              //是否有协议 0：否 1：是
    public int mDepositPriority;                         //押金使用优先级1:优先使用共享押金,2:优先使用馆押金
    public int mAgreementLevel;                          //协议等级
    public String mAreaAddress;                          //地址
    public int mBorrowNum;                               //本馆最大借阅数
    public int mDeposit;                                 //0 非押金模式  1 押金模式
    public String mHallCode;                             //馆号
    public String mLibraryLev;                           //图书馆等级
    public String mName;                                 //馆名
    public String mOperaterName;                         //操作员名称
    public String mOperaterId;                           //操作员id
    public int mLibraryStatus;                           // 1:正常 2:停用 3:屏蔽
    public int mReaderLimit;                             //当前是否限制馆0 非限制馆 1 限制馆
    public String mCustomerId;                           //图书馆id
    public boolean mHaveRefundAccount;                   //是否绑定了退款账户
    //config permission
    public boolean mOpenTimePermission = false;          //开放时间设置权限
    public boolean mPasswordManagePermission = false;    //操作员密码管理权限
    public boolean mBorrowPermission = false;            //借书管理权限
    public boolean mReturnPermission = false;            //还书管理权限
    public boolean mReaderManagePermission = false;      //读者管理权限
    public boolean mCirculateOutPermission = false;      //流出管理权限
    public boolean mCirculateInPermission = false;       //流入管理权限
    public boolean mRefundDepositPermission = false;     //退押金权限
    public boolean mChargeDepositPermission = false;     //交押金权限
    public boolean mPenaltyFreePermission = false;       //罚金免单权限

    //配置统计分析
    public boolean mCountBookPermission = false;         //藏书统计权限
    public boolean mCebitBookPermission = false;         //在借统计权限
    public boolean mBorrowBookPermission = false;        //借书统计权限
    public boolean mReturnBookPermission = false;        //还书统计权限
    public boolean mDompensateBookPermission = false;    //赔书统计权限
    public boolean mSellPermission = false;              //销售统计权限
    public boolean mDepositPermission = false;           //收款统计权限
    public boolean mReaderPermission = false;            //读者统计权限
    public boolean mPenaltyFreeStatisticPermission = false;       //免单统计权限
    public boolean mAppDepositPermission = false;        //押金权限

}
