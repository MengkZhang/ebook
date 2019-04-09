package com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/6/8.
 */

public class DefaultEventDelegate implements EventDelegate {
    private RecyclerArrayAdapter adapter;
    private EventFooter footer;

    private OnLoadMoreListener onLoadMoreListener;

    private boolean isLoadingMore = false;

    private boolean hasMore = false;
    private boolean hasNoMore = false;

    private int status = STATUS_INITIAL;
    private static final int STATUS_INITIAL = 291;
    private static final int STATUS_MORE = 260;
    private static final int STATUS_NOMORE = 408;
    private static final int STATUS_ERROR = 732;

    DefaultEventDelegate(RecyclerArrayAdapter adapter) {
        this.adapter = adapter;
        footer = new EventFooter();
        adapter.addFooter(footer);
    }

    private void onMoreViewShowed() {
        if (!isLoadingMore && onLoadMoreListener != null) {
            isLoadingMore = true;
            onLoadMoreListener.onLoadMore();
        }
    }

    private void onErrorViewShowed() {
        isLoadingMore = false;
        footer.showMore();
        onMoreViewShowed();
    }

    //-------------------5个状态触发事件-------------------
    @Override
    public void addData(int length) {
        if (hasMore) {
            if (length == 0) {
                //当添加0个时，认为已结束加载到底
                if (status == STATUS_INITIAL || status == STATUS_MORE) {
                    footer.showNoMore();
                }
            } else {
                //当Error或初始时。添加数据，如果有More则还原。
                if (status == STATUS_INITIAL || status == STATUS_ERROR) {
                    footer.showMore();
                }
            }
        } else {
            if (hasNoMore) {
                footer.showNoMore();
                status = STATUS_NOMORE;
            }
        }
        isLoadingMore = false;
    }

    @Override
    public void clear() {
        status = STATUS_INITIAL;
        footer.hide();
        isLoadingMore = false;
    }

    @Override
    public void stopLoadMore() {
        footer.showNoMore();
        status = STATUS_NOMORE;
        isLoadingMore = false;
    }

    @Override
    public void pauseLoadMore() {
        footer.showError();
        status = STATUS_ERROR;
        isLoadingMore = false;
    }

//    @Override
//    public void resumeLoadMore() {
//        isLoadingMore = false;
//        footer.showMore();
//        onMoreViewShowed();
//    }

    //-------------------3种View设置-------------------

    @Override
    public void setCustomMoreView(View moreView, View noMoreView, View errorMoreView, OnLoadMoreListener listener) {
        this.footer.setMoreView(moreView);
        this.footer.setNoMoreView(noMoreView);
        this.footer.setErrorView(errorMoreView);
        this.onLoadMoreListener = listener;
        hasMore = true;
        hasNoMore = true;
    }

    @Override
    public void setNullMoreView(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
        hasMore = true;
        hasNoMore = true;
    }

    private class EventFooter implements RecyclerArrayAdapter.ItemView {
        private FrameLayout container;
        private View moreView;
        private View noMoreView;
        private View errorView;

        private int flag = Hide;
        static final int Hide = 0;
        static final int ShowMore = 1;
        static final int ShowError = 2;
        static final int ShowNoMore = 3;


        EventFooter() {
            container = new FrameLayout(adapter.getContext());
            container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            return container;
        }

        @Override
        public void onBindView(View headerView) {
            switch (flag) {
                case ShowMore:
                    onMoreViewShowed();
                    break;
                case ShowError:
                    onErrorViewShowed();
                    break;
            }
        }

        void refreshStatus() {
            if (container != null) {
                if (flag == Hide) {
                    container.setVisibility(View.GONE);
                    return;
                }
                if (container.getVisibility() != View.VISIBLE)
                    container.setVisibility(View.VISIBLE);
                View view = null;
                switch (flag) {
                    case ShowMore:
                        view = moreView;
                        break;
                    case ShowError:
                        view = errorView;
                        break;
                    case ShowNoMore:
                        view = noMoreView;
                        break;
                }
                if (view == null) {
                    container.setVisibility(View.GONE);
                    return;
                }
                if (view.getParent() == null)
                    container.addView(view);
                for (int i = 0; i < container.getChildCount(); i++) {
                    if (container.getChildAt(i) == view)
                        view.setVisibility(View.VISIBLE);
                    else
                        container.getChildAt(i).setVisibility(View.GONE);
                }
            }
        }

        public void showError() {
            flag = ShowError;
            refreshStatus();
        }

        public void showMore() {
            flag = ShowMore;
            refreshStatus();
        }

        public void showNoMore() {
            flag = ShowNoMore;
            refreshStatus();
        }

        //初始化
        public void hide() {
            flag = Hide;
            refreshStatus();
        }

        public void setMoreView(View moreView) {
            this.moreView = moreView;
        }

        public void setNoMoreView(View noMoreView) {
            this.noMoreView = noMoreView;
        }

        public void setErrorView(View errorView) {
            this.errorView = errorView;
            errorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onErrorViewShowed();
                }
            });
        }
    }

}
