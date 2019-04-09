package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.IntoManagementListBean;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 流出列表适配器
 * Created by ZhiqiangJia on 2017-07-10.
 */
public class IntoManagementListAdapter extends RecyclerArrayAdapter<IntoManagementListBean> {

    public IntoManagementListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<IntoManagementListBean>(parent, R.layout.view_flow_manage_ment_item) {
            @Override
            public void setData(final IntoManagementListBean item) {
                if (null == item) {
                    return;
                }
                //set data
                holder.setText(R.id.outflow_lib_name_tv, item.name)
                        .setText(R.id.outflow_date_tv, DateUtils.formatToDate2(item.outDate))
                        .setText(R.id.outflow_lib_code_tv, item.outHallCode)
                        .setText(R.id.outflow_lib_name_tv, item.name)
                        .setText(R.id.outflow_operator_tv, item.userName)
                        .setText(R.id.outflow_data_tv, item.totalSum + "/" + StringUtils.doubleToString(item.totalPrice));
                //设置状态 2未审核4已审核6已完成-1通还
                switch (item.inState) {
                    case 2:
                        setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                        setText(R.id.outflow_status_tv, "未审核");
                        setText(R.id.outflow_change_tv, "详情");
                        setText(R.id.outflow_delete_tv, "");
                        break;
                    case 4:
                        setVisible(R.id.outflow_delete_tv, View.VISIBLE);
                        setText(R.id.outflow_status_tv, "已审核");
                        setText(R.id.outflow_change_tv, "签收");
                        setText(R.id.outflow_delete_tv, "拒收");
                        break;
                    case 6:
                        setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                        setText(R.id.outflow_status_tv, "已完成");
                        setText(R.id.outflow_change_tv, "详情");
                        setText(R.id.outflow_delete_tv, "");
                        break;
                    case -1:
                        setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                        setText(R.id.outflow_status_tv, "通还");
                        setText(R.id.outflow_change_tv, "详情");
                        setText(R.id.outflow_delete_tv, "");
                        break;
                    default://默认
                        setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                        setText(R.id.outflow_status_tv, "");
                        setText(R.id.outflow_change_tv, "");
                        setText(R.id.outflow_delete_tv, "");
                        break;
                }

                //签收，详情
                setOnClickListener(R.id.outflow_change_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mCallback) {
                            mCallback.callbackChangeIntoManage(item);
                        }
                    }
                });
                //拒收
                setOnClickListener(R.id.outflow_delete_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mCallback) {
                            mCallback.callbackRefuseAcceptIntoManage(item);
                        }
                    }
                });
            }
        };
    }


    private CallbackChangeIntoOperation mCallback;

    public void setInToManagementListener(CallbackChangeIntoOperation callback) {
        this.mCallback = callback;
    }

    public interface CallbackChangeIntoOperation {
        /**
         * 签收，详情
         *
         * @param item
         */
        void callbackChangeIntoManage(IntoManagementListBean item);

        /**
         * 拒收
         *
         * @param item
         */
        void callbackRefuseAcceptIntoManage(IntoManagementListBean item);
    }

}
