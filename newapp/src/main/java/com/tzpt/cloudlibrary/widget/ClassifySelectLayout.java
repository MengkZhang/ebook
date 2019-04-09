package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 书籍分类选择控件
 * Created by Administrator on 2017/6/6.
 */

public class ClassifySelectLayout extends LinearLayout implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context mContext;

    private View mAnchorView;
    private ImageView mArrowIv;
    private TextView mTitleTv;
    private LinearLayout mBookTypeLl;

    private OnSelectListener listener;
    private List<ClassifyInfo> mClassifyInfoData = new ArrayList<>();
    private CustomPopupWindow mListPopupWindow;
    private ClassifyAdapter mAdapter;

    Animation operatingAnim1;
    Animation operatingAnim2;
    LinearInterpolator lin1 = new LinearInterpolator();
    LinearInterpolator lin2 = new LinearInterpolator();

    private String mGradeName = null;
    private int mLimitWordsCount = Integer.MAX_VALUE; //设置限制文字个数
    private boolean isOpen = false;

    public ClassifySelectLayout(Context context) {
        this(context, null);
    }

    public ClassifySelectLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassifySelectLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        operatingAnim1 = AnimationUtils.loadAnimation(context, R.anim.roate_0_180);
        operatingAnim2 = AnimationUtils.loadAnimation(context, R.anim.roate_180_360);

        mAnchorView = LayoutInflater.from(getContext()).inflate(R.layout.view_book_classify, this);
        initView();
    }

    private void initView() {
        mArrowIv = (ImageView) findViewById(R.id.classify_icon_iv);
        mArrowIv.setScaleType(ImageView.ScaleType.MATRIX);
        mTitleTv = (TextView) findViewById(R.id.classify_title_tv);

        mBookTypeLl = (LinearLayout) findViewById(R.id.book_type_ll);
        mBookTypeLl.setOnClickListener(this);
        operatingAnim1.setInterpolator(lin1);
        operatingAnim1.setFillAfter(true);
        operatingAnim2.setInterpolator(lin2);
        operatingAnim2.setFillAfter(true);
    }

    //设置分类布局居右
    public void setRightTextGravityEND() {
        mBookTypeLl.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mArrowIv.getLayoutParams());
        lp.setMargins((int) DpPxUtils.dipToPx(mContext, 2), 0, (int) DpPxUtils.dipToPx(mContext, 14), 0);
        mArrowIv.setLayoutParams(lp);
    }

    //设置分类文字
    public void setRightText(int resId) {
        mTitleTv.setText(resId);
    }

    //设置分类文字颜色
    public void setRightTextColor(int colorId) {
        mTitleTv.setTextColor(colorId);
    }

    //设置分类文字箭头资源
    public void setRightArrowResId(int resId) {
        mArrowIv.setImageResource(resId);
    }

    /**
     * 设置分类名称
     *
     * @param gradeName
     */
    public void setGradeName(String gradeName) {
        this.mGradeName = gradeName;
    }

    /**
     * 设置限制文字个数
     *
     * @param limitWordsCount 文字个数
     */
    public void setLimitWordsCount(int limitWordsCount) {
        this.mLimitWordsCount = limitWordsCount;
    }

    public void setData(List<ClassifyInfo> list) {
        if (list != null && !list.isEmpty()) {
            mClassifyInfoData.addAll(list);
            mTitleTv.setText(list.get(0).name);
        }
    }

    public boolean ClassifyInfoListIsEmpty() {
        return mClassifyInfoData.isEmpty();
    }

    public void openPopupWindow() {
        if (mListPopupWindow == null) {
            createPopupWindow();
        }
        mListPopupWindow.showAsDropDown(mAnchorView);
    }

    private void createPopupWindow() {
        mListPopupWindow = new CustomPopupWindow(mContext);
        mAdapter = new ClassifyAdapter(mContext);
        View view = View.inflate(mContext, R.layout.ppw_classify_list, null);
        view.setOnClickListener(this);
        ListView lv = (ListView) view.findViewById(R.id.classify_lv);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
        mListPopupWindow.setContentView(view);
        mListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mListPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mListPopupWindow.setFocusable(true);
        mListPopupWindow.setOutsideTouchable(true);
        mListPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mArrowIv.startAnimation(operatingAnim2);
                isOpen = false;
            }
        });
    }

    public void closePopWindow() {
        if (mListPopupWindow != null && mListPopupWindow.isShowing()) {
            mListPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_type_ll:
                //数据为空，不弹出window
                if (mClassifyInfoData.isEmpty()) {
                    return;
                }
                if (isOpen) {
                    mArrowIv.startAnimation(operatingAnim2);
                    closePopWindow();
                    isOpen = false;
                } else {
                    mArrowIv.startAnimation(operatingAnim1);
                    openPopupWindow();
                    isOpen = true;
                }
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.setSelPosition(position);
        String classifyName = mClassifyInfoData.get(position).name;
        if (mClassifyInfoData.get(position).id == 0 && !TextUtils.isEmpty(mGradeName)) {
            classifyName = mGradeName;
        }
        if (mLimitWordsCount != Integer.MAX_VALUE) {
            if (classifyName.length() > mLimitWordsCount) {
                if (classifyName.length() == mLimitWordsCount + 1) {
                    classifyName = classifyName.substring(0, mLimitWordsCount + 1);
                } else {
                    classifyName = classifyName.substring(0, mLimitWordsCount) + "...";
                }
            }
        }
        mTitleTv.setText(classifyName);
        if (listener != null) {
            listener.onSelect(position, mClassifyInfoData.get(position));
        }
        mListPopupWindow.dismiss();
    }

    private class ClassifyAdapter extends BaseAdapter {
        private Context mContext;
        int selPosition = 0;


        ClassifyAdapter(Context context) {
            mContext = context;
        }

        void setSelPosition(int position) {
            selPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mClassifyInfoData.size();
        }

        @Override
        public Object getItem(int i) {
            return mClassifyInfoData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.view_classify_list_item, null);
                holder.mSelItemTitleTv = (TextView) view.findViewById(R.id.sel_item_title_tv);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.mSelItemTitleTv.setText(mClassifyInfoData.get(i).name);
            if (selPosition == i) {
                holder.mSelItemTitleTv.setTextColor(mContext.getResources().getColor(R.color.color_8a623d));
                holder.mSelItemTitleTv.setBackgroundResource(R.drawable.bg_item_pressed);
            } else {
                holder.mSelItemTitleTv.setTextColor(mContext.getResources().getColor(R.color.color_333333));
                holder.mSelItemTitleTv.setBackgroundResource(R.drawable.bg_item_common);
            }
            return view;
        }
    }

    static class ViewHolder {
        TextView mSelItemTitleTv;
    }

    public interface OnSelectListener {
        void onSelect(int position, ClassifyInfo classify);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public void releaseClassify() {
        if (null != mClassifyInfoData) {
            mClassifyInfoData.clear();
        }
    }
}
