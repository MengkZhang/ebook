package com.tzpt.cloundlibrary.manager.widget.popupwindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.DeviderItemDerocation;
import com.tzpt.cloundlibrary.manager.bean.OptionBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.FlowIntoSearchTypeAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 流出流入管理选项弹出框
 */
public class FlowIntoSearchTypePPW extends PopupWindow {

    private final LinearLayoutManager mLinearLayoutManager;
    private FlowIntoSearchTypeAdapter mAdapter;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<StatisticsConditionBean> mDatas;
    private CallbackOptionBean mCallback;

    public FlowIntoSearchTypePPW(Context context, CallbackOptionBean callback) {
        super(context);
        this.mContext = context;
        this.mCallback = callback;
        View views = View.inflate(mContext, R.layout.ppw_flow_into_search_type, null);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setContentView(views);

        mRecyclerView = (RecyclerView) views.findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(false);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DeviderItemDerocation(mContext, DeviderItemDerocation.VERTICAL_LIST));

        mDatas = new ArrayList<>();
        mAdapter = new FlowIntoSearchTypeAdapter(mContext, mDatas, mOnClickListener);
        mRecyclerView.setAdapter(mAdapter);
        startAnination();
    }

    //回调选中选项数据
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (null != mDatas && mDatas.size() > 0 && null != mCallback) {
                StatisticsConditionBean bean = mDatas.get(position);
                mCallback.callbackOptionBean(bean);
                dismiss();
            }
        }
    };

    /**
     * 添加数据
     *
     * @param datas
     */
    public void addDatas(List<StatisticsConditionBean> datas) {
        if (null != mDatas) {
            mDatas.clear();
            mDatas.addAll(datas);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void showAsDropDown(View anchorView, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchorView.getGlobalVisibleRect(rect);
            int h = anchorView.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchorView, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    /**
     * 1.执行动画
     */
    private void startAnination() {
        if (null != mLinearLayoutManager) {
            mLinearLayoutManager.scrollToPosition(0);
        }
    }

    public interface CallbackOptionBean {
        void callbackOptionBean(StatisticsConditionBean bean);
    }
}
