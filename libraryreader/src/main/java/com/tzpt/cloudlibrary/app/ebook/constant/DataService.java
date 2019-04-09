package com.tzpt.cloudlibrary.app.ebook.constant;

import android.graphics.Color;

import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.books.model.ChoosedColor;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读器数据配置
 * Created by ZhiqiangJia on 2017-02-17.
 */
public class DataService {

    /**
     * 获取选择阅读器背景颜色
     *
     * @return
     */
    public static List<ChoosedColor> getChoosedColorList() {
        List<ChoosedColor> list = new ArrayList<>();
        list.clear();
        //int resourseId,String bgColorResourse, int textColorResourse,  String textColorStringResourse, int alphaBgColor, boolean colorChoosed
        list.add(new ChoosedColor(R.drawable.ic_drawable_font_color1_def, "#FFD6C8A9", R.color.color_choose_bg_text1, "#ff444444", Color.argb(30, 253, 251, 184), true));//1
        list.add(new ChoosedColor(R.drawable.ic_drawable_font_color3_def, "#ffc1ece6", R.color.color_choose_bg_text3, "#FF093934", Color.argb(30, 193, 236, 230), false));//3
        list.add(new ChoosedColor(R.drawable.ic_drawable_font_color5_def, "#FFffffff", R.color.color_choose_bg_text5, "#FF454545", Color.argb(30, 255, 255, 255), false));//5
        list.add(new ChoosedColor(R.drawable.ic_drawable_font_color8_def, "#ff094139", R.color.color_choose_bg_text8, "#FFc2ede7", Color.argb(30, 9, 65, 57), false));//8
        list.add(new ChoosedColor(R.drawable.ic_drawable_font_color10_def, "#FF000000", R.color.color_choose_bg_text10, "#FFe5e5e5", Color.argb(30, 0, 0, 0), false));//9
        return list;
    }
}
