package com.tzpt.cloudlibrary.app.ebook.books.model;

/**
 * 电子书--选择颜色
 * Created by ZhiqiangJia on 2017-02-17.
 */
public class ChoosedColor {

    public int defaultResourseId;//背景资源图
    public int textColorResourse;//文字颜色
    public String bgColorResourse;
    public String textColorStringResourse;
    public int alphaBgColor;
    public boolean colorChoosed = false;

    public ChoosedColor(int defaultResourseId, String bgColorResourse,
                        int textColorResourse, String textColorStringResourse,
                        int alphaBgColor, boolean colorChoosed) {
        this.defaultResourseId = defaultResourseId;
        this.bgColorResourse = bgColorResourse;
        this.textColorResourse = textColorResourse;
        this.textColorStringResourse = textColorStringResourse;
        this.alphaBgColor = alphaBgColor;
        this.colorChoosed = colorChoosed;
    }
}
