package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.SelfBookInfoBean;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 购书列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class SelfHelpBuyBookAdapter extends RecyclerArrayAdapter<SelfBookInfoBean> {

    private View.OnClickListener mDelClickListener;
    private boolean mEditStatus = true;
    private int mNormalColor;
    private int mErrorColor;

    public SelfHelpBuyBookAdapter(Context context, View.OnClickListener delClickListener) {
        super(context);
        this.mDelClickListener = delClickListener;
        mNormalColor = ContextCompat.getColor(context, R.color.color_444444);
        mErrorColor = ContextCompat.getColor(context, R.color.color_999999);

    }

    //设置当前是否编辑状态
    public void setEditStatus(boolean editStatus) {
        mEditStatus = editStatus;
    }

    /**
     * 购买成功状态
     */
    public void updateBuyBookSuccess() {
        for (SelfBookInfoBean item : getAllData()) {
            item.statusSuccess = true;
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SelfBookInfoBean>(parent, R.layout.view_item_borrow_books) {
            @Override
            public void setData(SelfBookInfoBean item) {
                holder.setText(R.id.borrow_library_number, item.belongLibraryHallCode)
                        .setText(R.id.borrow_code_number, item.barNumber)
                        .setText(R.id.borrow_book_name, item.properTitle);

                //自助购书
                holder.setText(R.id.borrow_book_price, MoneyUtils.formatMoney(item.price));
                holder.setText(R.id.borrow_book_attach_price, "折" + MoneyUtils.formatMoney(item.discountPrice));

                if (item.discountPrice > 0) {
                    ((TextView) holder.getView(R.id.borrow_book_price)).getPaint().setAntiAlias(true); // 抗锯齿
                    ((TextView) holder.getView(R.id.borrow_book_price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
                    holder.setVisible(R.id.borrow_book_attach_price, View.VISIBLE);
                } else {
                    ((TextView) holder.getView(R.id.borrow_book_price)).getPaint().setFlags(0); //取消设置的的划线
                    holder.setVisible(R.id.borrow_book_attach_price, View.GONE);
                }

                if (mEditStatus) {          //当前为编辑状态
                    setImageDrawableRes(R.id.borrow_del_img, R.mipmap.ic_book_delete);
                    setTag(R.id.borrow_del_img, getAdapterPosition());
                    setOnClickListener(R.id.borrow_del_img, mDelClickListener);
                    setVisible(R.id.borrow_book_reason, View.GONE);
                    //set text color
                    setBookItemTextColor(mNormalColor);
                } else {
                    if (item.statusSuccess) { //当前是否购书成功
                        setImageDrawableRes(R.id.borrow_del_img, R.mipmap.ic_book_correct);
                        setOnClickListener(R.id.borrow_del_img, null);
                        setVisible(R.id.borrow_book_reason, View.GONE);
                        //set text color
                        setBookItemTextColor(mNormalColor);
                    } else {
                        setImageDrawableRes(R.id.borrow_del_img, R.mipmap.ic_book_delete);
                        setTag(R.id.borrow_del_img, getAdapterPosition());
                        setOnClickListener(R.id.borrow_del_img, mDelClickListener);
                        setVisible(R.id.borrow_book_reason, View.VISIBLE);
                        setText(R.id.borrow_book_reason, item.statusDesc);
                        //set text color
                        setBookItemTextColor(mErrorColor);
                    }
                }
            }

            private void setBookItemTextColor(int colorResId) {
                setTextColor(R.id.borrow_library_number, colorResId);
                setTextColor(R.id.borrow_code_number, colorResId);
                setTextColor(R.id.borrow_book_name, colorResId);
                setTextColor(R.id.borrow_book_price, colorResId);
                setTextColor(R.id.borrow_book_attach_price, colorResId);
            }
        };
    }


}
