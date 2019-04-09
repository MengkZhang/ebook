package com.tzpt.cloudlibrary.widget.menupager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.OnRvItemClickListener;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.ui.main.LibMenuModelListAdapter;
import com.tzpt.cloudlibrary.ui.main.MenuModelListAdapter;
import com.tzpt.cloudlibrary.widget.HomeGridManager;
import com.tzpt.cloudlibrary.widget.bannerview.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书馆模块菜单控件
 * Created by tonyjia on 2018/11/22.
 */
public class ModelMenuLayout extends LinearLayout {
    public ModelMenuLayout(Context context) {
        this(context, null);
    }

    public ModelMenuLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModelMenuLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViews(context);
    }

    private Context mContext;
    private ViewPager mHomeVp;
    private CircleIndicator mCircleIndicator;
    private int mLimitLineCount = 10;

    private void initViews(Context context) {
        this.mContext = context;
        inflate(context, R.layout.view_model_menu, this);
        mHomeVp = (ViewPager) findViewById(R.id.home_vp);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.home_indicator);
    }

    public void initPagerAdapter(List<ModelMenu> modelMenuList, OnRvItemClickListener<ModelMenu> itemClickListener) {
        //获取页数
        int pageCount = (int) Math.ceil(modelMenuList.size() * 1.0 / mLimitLineCount);
        SparseArray<View> viewSparseArray = new SparseArray<>();
        for (int i = 0; i < pageCount; i++) {
            viewSparseArray.put(i, getListView(modelMenuList, i, itemClickListener));
        }
        CLMenuPagerAdapter adapter = new CLMenuPagerAdapter(viewSparseArray);
        mHomeVp.setAdapter(adapter);
        if (pageCount > 1) {
            mCircleIndicator.setVisibility(View.VISIBLE);
            mCircleIndicator.setViewPager(mHomeVp);
        }
    }

    private View getListView(List<ModelMenu> modelMenuList, int pageIndex, OnRvItemClickListener<ModelMenu> itemClickListener) {
        List<ModelMenu> subMenuList = new ArrayList<>();
        int i = pageIndex * mLimitLineCount;
        int end = i + mLimitLineCount;
        while ((i < modelMenuList.size()) && (i < end)) {
            subMenuList.add(modelMenuList.get(i));
            i++;
        }
        View menuView = LayoutInflater.from(mContext).inflate(R.layout.view_model_menu_item, null);
        RecyclerView recyclerView = (RecyclerView) menuView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new HomeGridManager(mContext, 5));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        LibMenuModelListAdapter adapter = new LibMenuModelListAdapter(mContext);
        recyclerView.setAdapter(adapter);
        adapter.clear();
        adapter.addAll(subMenuList);
        adapter.setOnItemClickListener(itemClickListener);
        return menuView;
    }
}
