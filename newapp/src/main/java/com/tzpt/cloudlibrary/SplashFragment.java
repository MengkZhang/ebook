package com.tzpt.cloudlibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.utils.StringUtils;

/**
 * 闪屏fragment
 *
 * @author JiaZhiqiang
 * @date 2015 9-16
 */
public class SplashFragment extends Fragment {

    public static final String TYPE = "type";//静态参数
    private View view;
    private ImageView mImageView;//图片控件
    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_guide, null);//加载闪屏fragment的布局
        type = getArguments().getString(TYPE);//类型
        initView();//初始化布局
        init();//根据type加载不同的图片
        return view;//返回加载的闪屏fragment布局view
    }

    /**
     * 初始展示图片
     */
    public void init() {
        if (!TextUtils.isEmpty(type)) {
            updateUI(type);//加载不同图片 根据type转换成int类型
        }
    }

    /**
     * 初始化图片view控件
     */
    public void initView() {
        mImageView = (ImageView) view.findViewById(R.id.mImageView);//加载了im控件
    }

    /**
     * 根据传入的参数显示对应的图片资源
     *
     * @param result 传入的字符串
     */
    private void updateUI(String result) {
        if (null != result && StringUtils.isNumeric(result)) {
            int newType = Integer.parseInt(result);//把传入的字符串参数转换成int类型
            switch (newType) {
                case 1:
                    mImageView.setImageResource(R.mipmap.ic_tu1);
                    break;
                case 2:
                    mImageView.setImageResource(R.mipmap.ic_tu2);
                    break;
                case 3:
                    mImageView.setImageResource(R.mipmap.ic_tu3);
                    break;
            }
        }
    }

    /**
     * 创建实例:把参数存储起来 以对应显示不同的图片资源
     *
     * @param type
     * @return
     */
    public static SplashFragment newInstance(String type) {
        SplashFragment splashFragment = new SplashFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE, type);
        splashFragment.setArguments(bundle);
        return splashFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //UmengHelper.setPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        //UmengHelper.setPageEnd(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
