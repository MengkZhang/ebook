package com.tzpt.cloudlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 引导界面
 */
public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.imageView1)
    ImageView mImageView1;
    @BindView(R.id.imageView2)
    ImageView mImageView2;
    @BindView(R.id.imageView3)
    ImageView mImageView3;
    @BindView(R.id.buttonCenter)
    Button mButtonCenter;
    private List<Fragment> mFragmentList = new ArrayList<>();//fragment的的集合
    private Unbinder unbinder;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        unbinder = ButterKnife.bind(this);
        initAdapter();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 1.点击进入按钮监听:
     * 2.第三个小圆点长按的监听
     */
    private void initListener() {
        buttonCenterListener();//点击进入按钮的 监听:
    }

    /**
     * 点击进入按钮的 监听
     */
    private void buttonCenterListener() {
        mButtonCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.startActivity(GuideActivity.this);
                finish();
            }
        });
    }

    /**
     * 初始化适配器<br/>
     */
    public void initAdapter() {
        FragmentManager fragmentManager = getSupportFragmentManager();//fragment管理器
        MyAdapter adapter = new MyAdapter(fragmentManager);//创建adapter实例
        mFragmentList.clear();//清空存储fragment的集合
        for (int i = 0; i < 3; i++) {
            SplashFragment splashFragment = SplashFragment.newInstance(getResources().getStringArray(R.array.titles)[i]);//创建SplashFragment实例,并传入1,2,3作为参数,方便后面根据此参数显示不同的图片资源
            mFragmentList.add(splashFragment);
        }
        adapter.mList.clear();//adapter实例里面的 存储fragment的集合先清空
        adapter.mList.addAll(mFragmentList);//随后把本类中的存储fragment的集合添加进适配器的集合中
        mViewPager.setAdapter(adapter);//把数据设置给mViewPager
        mViewPagerListener();//mViewPager的页面改变监听
    }

    /**
     * mViewPager显示的页面的小圆点设置为实心
     */
    private void mViewPagerListener() {
        mImageView1.setImageResource(R.mipmap.ty1);
        mImageView2.setImageResource(R.mipmap.ty0);
        mImageView3.setImageResource(R.mipmap.ty0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mImageView1.setImageResource(R.mipmap.ty1);
                    mImageView2.setImageResource(R.mipmap.ty0);
                    mImageView3.setImageResource(R.mipmap.ty0);
                } else if (position == 1) {
                    mImageView1.setImageResource(R.mipmap.ty0);
                    mImageView2.setImageResource(R.mipmap.ty1);
                    mImageView3.setImageResource(R.mipmap.ty0);
                } else if (position == 2) {
                    mImageView1.setImageResource(R.mipmap.ty0);
                    mImageView2.setImageResource(R.mipmap.ty0);
                    mImageView3.setImageResource(R.mipmap.ty1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * ViewPager适配器
     */
    public class MyAdapter extends FragmentPagerAdapter {
        List<Fragment> mList = new ArrayList<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
