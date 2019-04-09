package com.tzpt.cloudlibrary.widget;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.utils.DpPxUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频电子书二级分类选择控件
 * Created by Administrator on 2017/6/6.
 */

public class ClassifyTwoLevelSelectLayout extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private View mAnchorView;
    private View mChooseDriver;
    private ImageView mArrowIv;
    private TextView mTitleTv;
    private LinearLayout mBookTypeLl;
    private OnSelectListener listener;
    private List<ClassifyTwoLevelBean> mClassifyInfoData = new ArrayList<>();
    private List<ClassifyTwoLevelBean> mSubClassifyInfoData = new ArrayList<>();
    private CustomPopupWindow mListPopupWindow;
    private ClassifyAdapter mAdapter;
    private SubClassifyAdapter mSubAdapter;
    private boolean isOpen = false;
    Animation operatingAnim1;
    Animation operatingAnim2;
    LinearInterpolator lin1 = new LinearInterpolator();
    LinearInterpolator lin2 = new LinearInterpolator();
    private int mParentPosition = 0;
    private int mSelectParentPosition = 0;
    private int mSubPosition = 0;
    private boolean mCutClassifyNameLength = false;
    private String mGradeName = null;

    public ClassifyTwoLevelSelectLayout(Context context) {
        this(context, null);
    }

    public ClassifyTwoLevelSelectLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassifyTwoLevelSelectLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        operatingAnim1 = AnimationUtils.loadAnimation(context, R.anim.roate_0_180);
        operatingAnim2 = AnimationUtils.loadAnimation(context, R.anim.roate_180_360);
        mAnchorView = LayoutInflater.from(getContext()).inflate(R.layout.view_two_level_classify, this);
        initView();
    }

    private void initView() {
        mArrowIv = (ImageView) findViewById(R.id.classify_icon_iv);
        mArrowIv.setScaleType(ImageView.ScaleType.MATRIX);
        mTitleTv = (TextView) findViewById(R.id.classify_title_tv);
        mChooseDriver = findViewById(R.id.choose_driver);
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

    public void setDriverVisibility(boolean show) {
        mChooseDriver.setVisibility(show ? VISIBLE : GONE);
    }

    public void cutClassifyNameLength(boolean cutClassifyNameLength) {
        this.mCutClassifyNameLength = cutClassifyNameLength;
    }

    public boolean ClassifyInfoListIsEmpty() {
        return mClassifyInfoData == null;
    }

    public void setData(List<ClassifyTwoLevelBean> list) {
        if (list != null && !list.isEmpty()) {
            mClassifyInfoData.addAll(list);
            if (null != mClassifyInfoData.get(0) && null != mClassifyInfoData.get(0).mSubList
                    && mClassifyInfoData.get(0).mSubList.size() > 0) {
                mSubClassifyInfoData.clear();
                mSubClassifyInfoData.addAll(mClassifyInfoData.get(0).mSubList);
            }
        }
    }

    public boolean classifyInfoListIsEmpty() {
        return mClassifyInfoData.isEmpty();
    }

    public void openPopupWindow() {
        if (mListPopupWindow == null) {
            createPopupWindow();
        }
        mListPopupWindow.showAsDropDown(mAnchorView);
        setSelectPosition();
    }

    //设置指定数据
    private void setSelectPosition() {
        if (mSelectParentPosition < mClassifyInfoData.size()) {
            mAdapter.setSelPosition(mSelectParentPosition);
        }
        //设置为指定下标数据
        mSubClassifyInfoData.clear();
        mSubClassifyInfoData.addAll(mClassifyInfoData.get(mSelectParentPosition).mSubList);
        mSubAdapter.notifyDataSetChanged();
        if (mSubPosition < mSubClassifyInfoData.size()) {
            mSubAdapter.setSelPosition(mSubPosition);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        return super.onTouchEvent(event);
    }

    private void createPopupWindow() {
        mListPopupWindow = new CustomPopupWindow(mContext);
        mAdapter = new ClassifyAdapter(mContext);
        mSubAdapter = new SubClassifyAdapter(mContext);

        View view = View.inflate(mContext, R.layout.ppw_two_level_classify_list, null);
        view.setOnClickListener(this);
        ListView lv = (ListView) view.findViewById(R.id.classify_lv);
        ListView subLv = (ListView) view.findViewById(R.id.classify_sub_lv);
        lv.setAdapter(mAdapter);
        subLv.setAdapter(mSubAdapter);

        lv.setOnItemClickListener(mItemClickListener);
        subLv.setOnItemClickListener(mSubItemClickListener);
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
            default:
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

    /**
     * 设置分类名称
     *
     * @param gradeName
     */
    public void setGradeName(String gradeName) {
        this.mGradeName = gradeName;
    }

    //parent item click
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mAdapter.setSelPosition(position);
            mParentPosition = position;
            //设置子类列表
            if (null != mClassifyInfoData.get(position).mSubList) {
                mSubClassifyInfoData.clear();
                mSubClassifyInfoData.addAll(mClassifyInfoData.get(position).mSubList);
                mAdapter.notifyDataSetChanged();
                if (position == mSelectParentPosition) {
                    //设置选中下标
                    if (mSubPosition < mSubClassifyInfoData.size()) {
                        mSubAdapter.setSelPosition(mSubPosition);
                    }
                } else {
                    mSubAdapter.setSelPosition(-1);
                }
            }
        }
    };
    //sub item click
    private AdapterView.OnItemClickListener mSubItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null != mSubClassifyInfoData && mSubClassifyInfoData.size() > 0) {
                mSelectParentPosition = mParentPosition;
                mSubPosition = position;
                mSubAdapter.setSelPosition(position);
                //控制分类名称显示
                //获取一级分类的ID
                int parentClassifyId = mClassifyInfoData.get(mParentPosition).mId;
                //获取二级分类的ID
                int subClassifyId = mSubClassifyInfoData.get(position).mId;
                String classifyName;
                if (parentClassifyId == 0 && subClassifyId == 0 && !TextUtils.isEmpty(mGradeName)) {
                    classifyName = mGradeName;
                } else if (subClassifyId == 0) {
                    //如果没有二级分类ID则设置名称为一级分类名称（即全部）
                    classifyName = mClassifyInfoData.get(mParentPosition).mName;
                } else {
                    classifyName = mSubClassifyInfoData.get(position).mName;
                }
                classifyName = (null == classifyName ? "" : classifyName);
                if (mCutClassifyNameLength) {
                    if (classifyName.length() > 4) {
                        if (classifyName.length() == 5) {
                            classifyName = classifyName.substring(0, 5);
                        } else {
                            classifyName = classifyName.substring(0, 4) + "...";
                        }
                    }
                }
                mTitleTv.setText(classifyName);

                mListPopupWindow.dismiss();
                if (listener != null) {
                    listener.onSelect(position, parentClassifyId, subClassifyId);
                }
            }
        }
    };

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
        public ClassifyTwoLevelBean getItem(int i) {
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
            holder.mSelItemTitleTv.setText(mClassifyInfoData.get(i).mName);
            if (selPosition == i) {
                holder.mSelItemTitleTv.setTextColor(mContext.getResources().getColor(R.color.color_8a623d));
                holder.mSelItemTitleTv.setBackgroundResource(R.drawable.drawable_ffffff);
            } else {
                holder.mSelItemTitleTv.setTextColor(mContext.getResources().getColor(R.color.color_333333));
                holder.mSelItemTitleTv.setBackgroundResource(R.drawable.drawable_f4f4f4);
            }
            return view;
        }
    }

    static class ViewHolder {
        TextView mSelItemTitleTv;
    }

    private class SubClassifyAdapter extends BaseAdapter {
        private Context mContext;
        int selPosition = 0;


        SubClassifyAdapter(Context context) {
            mContext = context;
        }

        void setSelPosition(int position) {
            selPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (null != mSubClassifyInfoData) {
                return mSubClassifyInfoData.size();
            }
            return 0;
        }

        @Override
        public ClassifyTwoLevelBean getItem(int i) {
            return mSubClassifyInfoData.get(i);
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
                holder.mSelItemTitleTv.setPadding(DpPxUtils.dp2px(60f), 0, 0, 0);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.mSelItemTitleTv.setText(mSubClassifyInfoData.get(i).mName);
            holder.mSelItemTitleTv.setBackgroundResource(R.drawable.drawable_ffffff);
            if (selPosition == i) {
                holder.mSelItemTitleTv.setTextColor(mContext.getResources().getColor(R.color.color_8a623d));
            } else {
                holder.mSelItemTitleTv.setTextColor(mContext.getResources().getColor(R.color.color_333333));
            }
            return view;
        }
    }

    public interface OnSelectListener {
        void onSelect(int position, int parentClassifyId, int childClassifyId);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public void releaseClassify() {
        if (null != mClassifyInfoData) {
            mClassifyInfoData.clear();
        }
        if (null != mSubClassifyInfoData) {
            mSubClassifyInfoData.clear();
        }
    }
}
