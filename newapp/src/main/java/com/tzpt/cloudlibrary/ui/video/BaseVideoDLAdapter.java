package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.util.SparseBooleanArray;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by Administrator on 2018/7/2.
 */

public abstract class BaseVideoDLAdapter extends RecyclerArrayAdapter<VideoBean> {
    private static final int INVALID_POS = -1;
    boolean mIsEditMode = false;
    protected SparseBooleanArray mSparseItemChecked = new SparseBooleanArray();
    protected final RxBus mRxBus;

    BaseVideoDLAdapter(Context context) {
        super(context);
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    boolean isEditMode() {
        return mIsEditMode;
    }

    void setEditMode(boolean editMode) {
        mIsEditMode = editMode;
        if (!mIsEditMode) {
            mSparseItemChecked.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 处理选中事件
     */
    void handleCheckedChanged() {
        int checkedCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkedCount++;
            }
        }
        mRxBus.post(new VideoEvent(checkedCount));
    }

    public void checkAllOrNone(boolean isChecked) {
        for (int i = 0; i < getCount(); i++) {
            mSparseItemChecked.put(i, isChecked);
        }
        notifyDataSetChanged();
        handleCheckedChanged();
    }

    void removeChecked(int position) {
        mSparseItemChecked.delete(position);
        int checkedCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkedCount++;
            }
        }
        mRxBus.post(new VideoEvent(checkedCount));
    }

    boolean isAllChecked() {
        int checkedCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkedCount++;
            }
        }
        return checkedCount == getCount();
    }
}
