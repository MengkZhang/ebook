package com.tzpt.cloudlibrary.modle;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.Installation;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.business_bean.BaseResultData;
import com.tzpt.cloudlibrary.business_bean.InformationCommentDetailBean;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommentIndexVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommentInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommentReplyVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DiscussReplyVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsDiscussListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsPraiseResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PublishDiscussResultVo;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 资讯数据仓库
 * Created by tonyjia on 2019/1/4.
 */
public class InformationRepository {

    private static InformationRepository mInstance;

    public static InformationRepository getInstance() {
        if (mInstance == null) {
            mInstance = new InformationRepository();
        }
        return mInstance;
    }

    private InformationRepository() {

    }


    private static class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    private long mNewsId;

    /**
     * 设置资讯ID
     *
     * @param newsId
     */
    public void setNewsId(long newsId) {
        mNewsId = newsId;
    }

    public long getNewsId() {
        return mNewsId;
    }

    /**
     * 获取评论详情列表
     *
     * @param commentId 评论ID
     * @param pageNo    页码
     * @return 评论列表
     */
    public Observable<InformationCommentDetailBean> getCommentList(final long commentId, final long replyId, final int pageNo) {
        String readerId;
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = null;
            readerId = UserRepository.getInstance().getLoginReaderId();
        } else {
            readerId = null;
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        if (pageNo == 1) {
            if (replyId > 0) {
                return CloudLibraryApi.getInstance().replyIndex(replyId)
                        .flatMap(new Func1<BaseResultEntityVo<CommentIndexVo>, Observable<InformationCommentDetailBean>>() {
                            @Override
                            public Observable<InformationCommentDetailBean> call(BaseResultEntityVo<CommentIndexVo> commentIndexVoBaseResultEntityVo) {
                                if (commentIndexVoBaseResultEntityVo.status == 200) {
                                    if (commentIndexVoBaseResultEntityVo.data != null
                                            && commentIndexVoBaseResultEntityVo.data.index >= 0) {
                                        return getCommentList(commentId, commentIndexVoBaseResultEntityVo.data.index, pageNo);
                                    } else {
                                        return getCommentList(commentId, -1, pageNo);
                                    }
                                } else {
                                    throw new ServerException(commentIndexVoBaseResultEntityVo.data.errorCode, "");
                                }
                            }
                        }).onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            } else {
                return getCommentList(commentId, -1, pageNo)
                        .onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            }
        } else {
            return CloudLibraryApi.getInstance().replyList(commentId, readerId, 20, pageNo, identity)
                    .map(new Func1<BaseResultEntityVo<DiscussReplyVo>, InformationCommentDetailBean>() {
                        @Override
                        public InformationCommentDetailBean call(BaseResultEntityVo<DiscussReplyVo> baseResultEntityVo) {
                            if (baseResultEntityVo.status == 200) {
                                List<CommentBean> replyList = getReplyList(baseResultEntityVo.data);
                                if (replyList != null && replyList.size() > 0) {
                                    InformationCommentDetailBean informationCommentDetailBean = new InformationCommentDetailBean();
                                    informationCommentDetailBean.mReplyCommentList = replyList;
                                    informationCommentDetailBean.mTotalCount = baseResultEntityVo.data.totalCount;
                                    return informationCommentDetailBean;
                                } else {
                                    return null;
                                }
                            } else {
                                throw new ServerException(baseResultEntityVo.data.errorCode, "");
                            }
                        }
                    }).onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
        }
    }

    private Observable<InformationCommentDetailBean> getCommentList(final long commentId, final int targetIndex,
                                                                    final int pageNo) {
        String readerId;
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = null;
            readerId = UserRepository.getInstance().getLoginReaderId();
        } else {
            readerId = null;
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        int pageCount = 20;
        if (targetIndex >= 0) {
            pageCount = (targetIndex / 20 + 1) * 20;
        }

        return Observable.zip(CloudLibraryApi.getInstance().commentInfo(commentId, identity, readerId),
                CloudLibraryApi.getInstance().replyList(commentId, readerId, pageCount, pageNo, identity)
                , new Func2<BaseResultEntityVo<CommentInfoVo>, BaseResultEntityVo<DiscussReplyVo>, InformationCommentDetailBean>() {
                    @Override
                    public InformationCommentDetailBean call(BaseResultEntityVo<CommentInfoVo> baseResultEntityVo,
                                                             BaseResultEntityVo<DiscussReplyVo> discussReplyVo) {
                        if (baseResultEntityVo.status == 200 && discussReplyVo.status == 200) {
                            List<CommentBean> beanList = new ArrayList<>();
                            //添加
                            CommentBean replyBean = new CommentBean();
                            replyBean.mCommentName = !TextUtils.isEmpty(baseResultEntityVo.data.nickName)
                                    ? baseResultEntityVo.data.nickName
                                    : StringUtils.formatReaderNickName(baseResultEntityVo.data.name, baseResultEntityVo.data.gender);

                            replyBean.mId = commentId;
                            replyBean.mPublishTime = baseResultEntityVo.data.createTime;
                            replyBean.mContent = baseResultEntityVo.data.content;
                            replyBean.mCommentImage = ImageUrlUtils.getDownloadOriginalImagePath(baseResultEntityVo.data.image);
                            replyBean.mIsPraised = baseResultEntityVo.data.isPraise == 1;
                            replyBean.mIsOwn = baseResultEntityVo.data.isOwn == 1 && UserRepository.getInstance().isLogin();
                            replyBean.mPraisedCount = baseResultEntityVo.data.praiseCount;
                            replyBean.mReplyCount = discussReplyVo.data != null ? discussReplyVo.data.totalCount : 0;
                            //设置资讯ID
                            setNewsId(baseResultEntityVo.data.newsId);
                            beanList.add(replyBean);

                            List<CommentBean> replyList = getReplyList(discussReplyVo.data);
                            if (replyList != null) {
                                beanList.addAll(replyList);
                            }

                            InformationCommentDetailBean informationCommentDetailBean = new InformationCommentDetailBean();
                            informationCommentDetailBean.mReplyCommentList = beanList;
                            informationCommentDetailBean.mTotalCount = discussReplyVo.data.totalCount;
                            informationCommentDetailBean.mTargetIndex = targetIndex;
                            if (targetIndex >= 0) {
                                informationCommentDetailBean.mCurrentPage = targetIndex / 20 + 1;
                            } else {
                                informationCommentDetailBean.mCurrentPage = pageNo;
                            }

                            return informationCommentDetailBean;

                        } else if (baseResultEntityVo.status != 200) {
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        } else {
                            throw new ServerException(discussReplyVo.data.errorCode, "");
                        }
                    }
                });
    }

    /**
     * 获取回复列表数据
     *
     * @param data
     * @return
     */
    private List<CommentBean> getReplyList(DiscussReplyVo data) {
        if (null != data && null != data.resultList && data.resultList.size() > 0) {
            List<CommentBean> replyBeanList = new ArrayList<>();
            //设置评论回复列表
            for (DiscussReplyVo.DiscussReplyItemVo itemVo : data.resultList) {
                CommentBean replyBean = new CommentBean();
                replyBean.mCommentImage = ImageUrlUtils.getDownloadOriginalImagePath(itemVo.replyImage);
                replyBean.mIsMan = itemVo.replyGender == 1;
                replyBean.mIsOwn = (itemVo.isOwn == 1 && UserRepository.getInstance().isLogin());

                replyBean.mId = itemVo.id;
                replyBean.mContent = itemVo.content;
                replyBean.mPublishTime = itemVo.createTime;
                replyBean.mIsPraised = itemVo.isPraise == 1;
                replyBean.mPraisedCount = itemVo.praiseCount;
                //回复者名称
                String replyName;
                if (itemVo.replyType == 2) {
                    //2:读者的回复
                    replyName = !TextUtils.isEmpty(itemVo.replyNickName)
                            ? itemVo.replyNickName
                            : StringUtils.formatReaderNickName(itemVo.replyName, itemVo.replyGender);
                } else {
                    //1:平台的回复
                    replyName = itemVo.replyName;
                }
                replyBean.mCommentName = replyName;
                //被回复者名称
                if (itemVo.repliedType == 2) {
                    //2:被回复的是读者
                    replyBean.mRepliedName = !TextUtils.isEmpty(itemVo.repliedNickName)
                            ? itemVo.repliedNickName
                            : StringUtils.formatReaderNickName(itemVo.repliedName, itemVo.repliedGender);
                } else {
                    //1:被回复的是平台
                    replyBean.mRepliedName = itemVo.repliedName;
                }
                replyBeanList.add(replyBean);
            }
            return replyBeanList;
        } else {
            return null;
        }
    }

    /**
     * 回复读者点赞
     *
     * @param replyId
     * @return
     */
    public Observable<Boolean> replyPraise(long replyId) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().replyPraise(replyId, identity)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }

                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 评论点赞
     *
     * @param commentId 评论ID
     * @return
     */
    public Observable<Boolean> commentPraise(long commentId) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().commentPraise(commentId, identity)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }

                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 回复读者评论或者回复
     *
     * @param commentId 评论ID
     * @param content   内容
     * @param readerId  读者ID
     * @param replyId   回复ID
     * @return
     */
    public Observable<Void> replyMsg(long commentId, String content, String readerId, long replyId) {
        return CloudLibraryApi.getInstance().replyMsg(commentId, content, readerId, replyId)
                .map(new Func1<BaseResultEntityVo<CommentReplyVo>, Void>() {
                    @Override
                    public Void call(BaseResultEntityVo<CommentReplyVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status != 200) {
                            if (baseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<Void>());
    }

    /**
     * 回复读者评论或者回复
     *
     * @param commentId 评论ID
     * @param content   内容
     * @param readerId  读者ID
     * @param replyId   回复ID
     * @return
     */
    public Observable<CommentBean> replyMsgForReturnComment(long commentId, String content, String readerId, long replyId) {
        return CloudLibraryApi.getInstance().replyMsg(commentId, content, readerId, replyId)
                .map(new Func1<BaseResultEntityVo<CommentReplyVo>, CommentBean>() {
                    @Override
                    public CommentBean call(BaseResultEntityVo<CommentReplyVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status == 200) {
                            if (null != baseResultEntityVo.data) {
                                CommentBean childBean = new CommentBean();
                                CommentReplyVo replyItem = baseResultEntityVo.data;
                                childBean.mContent = replyItem.content;
                                if (replyItem.replyType == 2) {
                                    //2:读者的回复
                                    childBean.mCommentName = !TextUtils.isEmpty(replyItem.replyNickName)
                                            ? replyItem.replyNickName
                                            : StringUtils.formatReaderNickName(replyItem.replyName, replyItem.replyGender);
                                } else {
                                    //1:平台的回复
                                    childBean.mCommentName = replyItem.replyName;
                                }

                                if (replyItem.repliedType == 2) {
                                    //2:被回复的是读者
                                    childBean.mRepliedName = !TextUtils.isEmpty(replyItem.repliedNickName)
                                            ? replyItem.repliedNickName
                                            : StringUtils.formatReaderNickName(replyItem.repliedName, replyItem.repliedGender);
                                } else {
                                    //1:被回复的是平台
                                    childBean.mRepliedName = replyItem.repliedName;
                                }
                                return childBean;
                            }

                            return null;
                        } else {
                            if (baseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<CommentBean>());
    }

    /**
     * 删除回复列表中的自己的回复
     *
     * @param replyId  回复ID
     * @param readerId 读者ID
     * @return
     */
    public Observable<Boolean> delReplyReaderMsg(long replyId, String readerId) {
        return CloudLibraryApi.getInstance().delReplyReaderMsg(replyId, readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (baseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }


    /**
     * 删除自己的评论
     *
     * @param commentId 评论ID
     * @param readerId  读者ID
     * @return
     */
    public Observable<Boolean> delReaderMsg(long commentId, String readerId) {
        return CloudLibraryApi.getInstance().delReaderMsg(commentId, readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (baseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 获取评论列表
     *
     * @param commentId 评论ID
     * @param pageNum   页码
     * @param newsId    资讯ID
     * @return
     */
    public Observable<InformationCommentDetailBean> getInformationDiscussList(final long commentId, final int pageNum, final long newsId) {
        if (pageNum == 1) {
            if (commentId > -1) {
                return CloudLibraryApi.getInstance().commentIndex(commentId)
                        .flatMap(new Func1<BaseResultEntityVo<CommentIndexVo>, Observable<InformationCommentDetailBean>>() {
                            @Override
                            public Observable<InformationCommentDetailBean> call(BaseResultEntityVo<CommentIndexVo> commentIndexVo) {
                                if (commentIndexVo.status == 200) {
                                    if (commentIndexVo.data != null && commentIndexVo.data.index >= 0) {
                                        return getNewsDiscussList(pageNum, newsId, commentIndexVo.data.index);
                                    } else {
                                        return getNewsDiscussList(pageNum, newsId, -1);
                                    }
                                } else {
                                    throw new ServerException(commentIndexVo.data.errorCode, "");
                                }
                            }
                        }).onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            } else {
                return getNewsDiscussList(pageNum, newsId, -1)
                        .onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            }
        } else {
            return getNewsDiscussList(pageNum, newsId, -1)
                    .onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
        }
    }

    /**
     * 获取资讯评论列表
     *
     * @param pageNum 页码
     * @param newsId  资讯ID
     * @return
     */
    private Observable<InformationCommentDetailBean> getNewsDiscussList(final int pageNum, long newsId, final int targetIndex) {

        int pageCount = 20;
        if (targetIndex >= 0) {
            pageCount = (targetIndex / 20 + 1) * 20;
        }

        ArrayMap<String, Object> parameters = new ArrayMap<>();
        if (UserRepository.getInstance().isLogin()) {
            parameters.put("newsId", newsId);
            parameters.put("pageCount", pageCount);
            parameters.put("pageNo", pageNum);
            parameters.put("readerId", UserRepository.getInstance().getLoginReaderId());
        } else {
            parameters.put("newsId", newsId);
            parameters.put("pageCount", pageCount);
            parameters.put("pageNo", pageNum);
            parameters.put("identity", Installation.id(CloudLibraryApplication.getAppContext()));
        }
        return CloudLibraryApi.getInstance().getNewsDiscussList(parameters)
                .map(new Func1<BaseResultEntityVo<NewsDiscussListVo>, InformationCommentDetailBean>() {
                    @Override
                    public InformationCommentDetailBean call(BaseResultEntityVo<NewsDiscussListVo> newsDiscussListVoBaseResultEntityVo) {
                        if (newsDiscussListVoBaseResultEntityVo.status == 200) {
                            if (newsDiscussListVoBaseResultEntityVo.data.resultList != null
                                    && newsDiscussListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                List<CommentBean> discussList = new ArrayList<>();
                                for (NewsDiscussListVo.DiscussListItemVo item : newsDiscussListVoBaseResultEntityVo.data.resultList) {
                                    CommentBean bean = new CommentBean();
                                    bean.mCommentImage = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                    bean.mIsMan = item.gender == 1;
                                    bean.mIsOwn = (item.isOwn == 1 && UserRepository.getInstance().isLogin());
                                    if (TextUtils.isEmpty(item.name)) {
                                        bean.mCommentName = "用户名";
                                    } else {
                                        bean.mCommentName = !TextUtils.isEmpty(item.nickName)
                                                ? item.nickName
                                                : StringUtils.formatReaderNickName(item.name, item.gender);
                                    }

                                    bean.mId = item.id;
                                    bean.mContent = item.content;
                                    bean.mPublishTime = item.createTime;

                                    bean.mIsPraised = item.isPraise == 1;
                                    bean.mPraisedCount = item.praiseCount;
                                    bean.mReplyCount = item.replyCount;

                                    if (item.replies != null && item.replies.size() > 0) {
                                        List<CommentBean> replyContentList = new ArrayList<>();
                                        for (int i = 0; i < (item.replies.size() > 2 ? 2 : item.replies.size()); i++) {
                                            CommentBean childBean = new CommentBean();
                                            NewsDiscussListVo.DiscussReplyListItemVo replyItem = item.replies.get(i);
                                            childBean.mContent = replyItem.content;
                                            if (replyItem.replyType == 2) {
                                                //2:读者的回复
                                                childBean.mCommentName = !TextUtils.isEmpty(replyItem.replyNickName)
                                                        ? replyItem.replyNickName
                                                        : StringUtils.formatReaderNickName(replyItem.replyName, replyItem.replyGender);
                                            } else {
                                                //1:平台的回复
                                                childBean.mCommentName = replyItem.replyName;
                                            }

                                            if (replyItem.repliedType == 2) {
                                                //2:被回复的是读者
                                                childBean.mRepliedName = !TextUtils.isEmpty(replyItem.repliedNickName)
                                                        ? replyItem.repliedNickName
                                                        : StringUtils.formatReaderNickName(replyItem.repliedName, replyItem.repliedGender);
                                            } else {
                                                //1:被回复的是平台
                                                childBean.mRepliedName = replyItem.repliedName;
                                            }
                                            replyContentList.add(childBean);
                                        }
                                        bean.mReplyContentList = replyContentList;
                                    }

                                    discussList.add(bean);
                                }
                                InformationCommentDetailBean detailBean = new InformationCommentDetailBean();
                                detailBean.mReplyCommentList = discussList;
                                detailBean.mTotalCount = newsDiscussListVoBaseResultEntityVo.data.totalCount;

                                detailBean.mTargetIndex = targetIndex;
                                if (targetIndex >= 0) {
                                    detailBean.mCurrentPage = targetIndex / 20 + 1;
                                } else {
                                    detailBean.mCurrentPage = pageNum;
                                }
                                return detailBean;
                            } else {
                                return null;
                            }
                        } else {
                            if (newsDiscussListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(newsDiscussListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                });
    }

    /**
     * 发布资讯评论
     *
     * @param content  评论内容
     * @param readerId 读者ID
     * @param newsId   资讯ID
     * @return
     */
    public Observable<Boolean> publishNewsDiscuss(String content, int readerId, long newsId) {
        return CloudLibraryApi.getInstance().publishNewsDiscuss(content, readerId, newsId)
                .map(new Func1<BaseResultEntityVo<PublishDiscussResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<PublishDiscussResultVo> publishDiscussResultVo) {
                        if (publishDiscussResultVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(publishDiscussResultVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }


    /**
     * 获取资讯列表
     *
     * @param parameters 参数map
     * @return
     */
    public Observable<BaseResultData<List<InformationBean>>> getInformationList(ArrayMap<String, String> parameters) {
        return CloudLibraryApi.getInstance().getInformationList(parameters)
                .map(new Func1<BaseResultEntityVo<NewsListVo>, BaseResultData<List<InformationBean>>>() {
                    @Override
                    public BaseResultData<List<InformationBean>> call(BaseResultEntityVo<NewsListVo> newsListVo) {
                        if (newsListVo.status == 200) {
                            return formatInformationList(newsListVo.data);
                        } else {
                            throw new ServerException(newsListVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseResultData<List<InformationBean>>>());
    }

    /**
     * 获取图书馆资讯列表
     *
     * @param libCode    图书馆馆号
     * @param parameters 参数map
     * @return
     */
    public Observable<BaseResultData<List<InformationBean>>> getLibInformationList(String libCode, ArrayMap<String, String> parameters) {
        return CloudLibraryApi.getInstance().getLibInformationList(libCode, parameters)
                .map(new Func1<BaseResultEntityVo<NewsListVo>, BaseResultData<List<InformationBean>>>() {
                    @Override
                    public BaseResultData<List<InformationBean>> call(BaseResultEntityVo<NewsListVo> newsListVo) {
                        if (newsListVo.status == 200) {
                            return formatInformationList(newsListVo.data);
                        } else {
                            throw new ServerException(newsListVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseResultData<List<InformationBean>>>());
    }

    private BaseResultData<List<InformationBean>> formatInformationList(NewsListVo data) {
        if (data != null && data.resultList != null && data.resultList.size() > 0) {
            List<InformationBean> beanList = new ArrayList<>();
            for (NewsListVo.NewsItemVo item : data.resultList) {
                InformationBean bean = new InformationBean();
                if (null != item.createDate && item.createDate.contains("-")) {
                    bean.mCreateDate = item.createDate.replaceAll("-", "");
                } else {
                    bean.mCreateDate = item.createDate;
                }
                bean.mId = item.newsId;
                bean.mSource = item.source;
                bean.mTitle = item.title;

                bean.mImage = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                bean.mSummary = item.summary;
                bean.mVideoUrl = item.videoUrl;
                bean.mVideoDuration = item.videoDuration;
                bean.mReadCount = item.viewCount;
                bean.mShareUrl = item.htmlUrl;
                bean.mUrl = item.detailUrl;
                bean.mCreateTime = DateUtils.formatNewsTime(item.currentTime, item.createTime);

                beanList.add(bean);
            }
            BaseResultData<List<InformationBean>> baseResultData = new BaseResultData<>();
            baseResultData.resultList = beanList;
            baseResultData.mTotalCount = data.totalCount;
            return baseResultData;
        } else {
            return null;
        }
    }

    public Observable<InformationBean> getNewsDetail(long newsId, int fromSearch) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().getNewsDetail(newsId, identity, fromSearch).
                map(new Func1<BaseResultEntityVo<NewsDetailVo>, InformationBean>() {
                    @Override
                    public InformationBean call(BaseResultEntityVo<NewsDetailVo> newsDetailVoBaseResultEntityVo) {

                        if (newsDetailVoBaseResultEntityVo.status == 200) {
                            InformationBean information = new InformationBean();
                            information.mId = newsDetailVoBaseResultEntityVo.data.id;
                            information.mTitle = newsDetailVoBaseResultEntityVo.data.title;
                            information.mSource = newsDetailVoBaseResultEntityVo.data.source;
                            information.mContent = newsDetailVoBaseResultEntityVo.data.content;
                            information.mCreateDate = newsDetailVoBaseResultEntityVo.data.createDate;
                            information.mImage = ImageUrlUtils.getDownloadOriginalImagePath(newsDetailVoBaseResultEntityVo.data.image);
                            information.mIsPraise = newsDetailVoBaseResultEntityVo.data.isPraise;
                            information.mSummary = newsDetailVoBaseResultEntityVo.data.summary;
                            information.mPraiseCount = newsDetailVoBaseResultEntityVo.data.praiseCount;
                            information.mReadCount = newsDetailVoBaseResultEntityVo.data.viewCount;
                            if (!TextUtils.isEmpty(newsDetailVoBaseResultEntityVo.data.detailUrl)) {
                                information.mUrl = newsDetailVoBaseResultEntityVo.data.detailUrl;
                            }
                            if (!TextUtils.isEmpty(newsDetailVoBaseResultEntityVo.data.url)) {
                                information.mShareUrl = newsDetailVoBaseResultEntityVo.data.url;
                            }
                            if (!TextUtils.isEmpty(information.mUrl)) {
                                return information;
                            } else {
                                throw new ServerException(newsDetailVoBaseResultEntityVo.data.errorCode, "");
                            }
                        } else {
                            throw new ServerException(newsDetailVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<InformationBean>());
    }


    public Observable<Boolean> operateNewsPraise(long newsId) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().operateNewsPraise(identity, newsId)
                .map(new Func1<BaseResultEntityVo<NewsPraiseResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<NewsPraiseResultVo> newsPraiseResultVoBaseResultEntityVo) {
                        if (newsPraiseResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(newsPraiseResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //===============================资讯列表===================================
    private List<InformationBean> mInformationBeanList = new ArrayList<>();

    public void addInformationList(List<InformationBean> beanList, boolean refresh) {
        if (refresh) {
            mInformationBeanList.clear();
        }
        mInformationBeanList.addAll(beanList);
    }

    public List<InformationBean> getInformationList() {
        return mInformationBeanList;
    }

    //移除资讯临时列表
    public void removeInformationList() {
        if (null != mInformationBeanList && mInformationBeanList.size() > 0) {
            mInformationBeanList.clear();
        }
    }
}
