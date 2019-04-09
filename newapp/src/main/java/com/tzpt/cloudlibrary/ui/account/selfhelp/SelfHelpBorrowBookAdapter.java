package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookInfoBean;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 借书列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class SelfHelpBorrowBookAdapter extends RecyclerArrayAdapter<SelfHelpBookInfoBean> {

    private View.OnClickListener mDelClickListener;
    private boolean mEditStatus = true;
    private int mNormalColor;
    private int mErrorColor;

    public SelfHelpBorrowBookAdapter(Context context, View.OnClickListener delClickListener) {
        super(context);
        this.mDelClickListener = delClickListener;
        mNormalColor = ContextCompat.getColor(context, R.color.color_444444);
        mErrorColor = ContextCompat.getColor(context, R.color.color_999999);
    }

    //设置当前是否编辑状态
    public void setEditStatus(boolean editStatus) {
        mEditStatus = editStatus;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SelfHelpBookInfoBean>(parent, R.layout.view_item_borrow_books) {
            @Override
            public void setData(SelfHelpBookInfoBean item) {
                holder.setText(R.id.borrow_library_number, item.mBelongLibraryHallCode)
                        .setText(R.id.borrow_code_number, item.mBook.mBarNumber)
                        .setText(R.id.borrow_book_name, item.mBook.mName);
                //自助借书
                holder.setText(R.id.borrow_book_price, ((item.mDeposit == 0) ? "" : "押") + MoneyUtils.formatMoney(item.mBook.mPrice));
                holder.setText(R.id.borrow_book_attach_price, "溢" + MoneyUtils.formatMoney(item.mAttachPrice));
                holder.setVisible(R.id.borrow_book_attach_price, item.mAttachPrice > 0 ? View.VISIBLE : View.GONE);
                if (mEditStatus) {          //当前为编辑状态
                    setImageDrawableRes(R.id.borrow_del_img, R.mipmap.ic_book_delete);
                    setTag(R.id.borrow_del_img, getAdapterPosition());
                    setOnClickListener(R.id.borrow_del_img, mDelClickListener);
                    setVisible(R.id.borrow_book_reason, View.GONE);
                    //set text color
                    setBookItemTextColor(mNormalColor);
                } else {
                    if (item.mStatusSuccess) { //当前是否借书成功
                        setImageDrawableRes(R.id.borrow_del_img, R.mipmap.ic_book_correct);
                        setOnClickListener(R.id.borrow_del_img, null);
                        setVisible(R.id.borrow_book_reason, View.GONE);
                        //set text color
                        setBookItemTextColor(mNormalColor);
                    } else {
                        setImageDrawableRes(R.id.borrow_del_img, R.mipmap.ic_book_error);
                        setOnClickListener(R.id.borrow_del_img, null);
                        setVisible(R.id.borrow_book_reason, View.VISIBLE);
                        setText(R.id.borrow_book_reason, item.mStatusDesc);
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
            }
        };
    }


}
