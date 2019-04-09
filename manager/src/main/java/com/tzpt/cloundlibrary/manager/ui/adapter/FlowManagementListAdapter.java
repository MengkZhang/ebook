package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 流出列表适配器
 * Created by ZhiqiangJia on 2017-07-10.
 */
public class FlowManagementListAdapter extends RecyclerArrayAdapter<FlowManageListBean> {

    private final LibraryInfo mLibraryInfo;

    public FlowManagementListAdapter(Context context) {
        super(context);
        mLibraryInfo = SharedPreferencesUtil.getInstance().getObject("LibraryInfo", LibraryInfo.class);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<FlowManageListBean>(parent, R.layout.view_flow_manage_ment_item) {
            @Override
            public void setData(final FlowManageListBean item) {
                if (null != item) {
                    //set data
                    holder.setText(R.id.outflow_lib_name_tv, item.name)
                            .setText(R.id.outflow_date_tv, DateUtils.formatToDate2(item.outDate))
                            .setText(R.id.outflow_lib_code_tv, item.inHallCode)
                            .setText(R.id.outflow_lib_name_tv, item.name)
                            .setText(R.id.outflow_operator_tv, item.userName)
                            .setText(R.id.outflow_data_tv, item.totalSum + "/" + StringUtils.doubleToString(item.totalPrice));
                    //设置状态-1未发送，2已发送，3.被否决 4.已批准5.被拒绝6.已完成 -1通还
                    switch (item.outState) {
                        case 1://未发送
                            setText(R.id.outflow_status_tv, "未发送");
                            if (item.onlyRead) {//如果是只读状态
                                setVisible(R.id.outflow_delete_tv, false);
                                setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                                setText(R.id.outflow_change_tv, "详情");
                                setText(R.id.outflow_delete_tv, "");
                            } else {
                                setVisible(R.id.outflow_delete_tv, true);
                                setText(R.id.outflow_change_tv, "修改");
                                setText(R.id.outflow_delete_tv, "删单");
                            }
                            break;
                        case 2://已发送
                            setText(R.id.outflow_status_tv, "已发送");
                            if (item.onlyRead) {
                                setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                                setText(R.id.outflow_change_tv, "详情");
                                setText(R.id.outflow_delete_tv, "");
                            } else {
                                setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                                setText(R.id.outflow_change_tv, "撤回");
                                setText(R.id.outflow_delete_tv, "");
                            }
                            break;
                        case 4://已批准
                            setText(R.id.outflow_status_tv, "已批准");
                            setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                            setText(R.id.outflow_change_tv, "详情");
                            setText(R.id.outflow_delete_tv, "");

                            break;
                        case 3://被否决
                            setText(R.id.outflow_status_tv, "被否决");
                            if (item.onlyRead) {
                                setVisible(R.id.outflow_delete_tv, false);
                                setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                                setText(R.id.outflow_change_tv, "详情");
                                setText(R.id.outflow_delete_tv, "");
                            } else {
                                setVisible(R.id.outflow_delete_tv, true);
                                setText(R.id.outflow_change_tv, "修改");
                                setText(R.id.outflow_delete_tv, "删单");
                            }
                            break;
                        case 5://被拒收
                            setText(R.id.outflow_status_tv, "被拒收");
                            if (item.onlyRead) {
                                setVisible(R.id.outflow_delete_tv, false);
                                setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                                setText(R.id.outflow_change_tv, "详情");
                                setText(R.id.outflow_delete_tv, "");
                            } else {
                                setVisible(R.id.outflow_delete_tv, true);
                                setText(R.id.outflow_change_tv, "修改");
                                setText(R.id.outflow_delete_tv, "删单");
                            }
                            break;
                        case 6://已完成
                            setVisible(R.id.outflow_delete_tv, View.INVISIBLE);
                            setText(R.id.outflow_status_tv, "已完成");
                            setText(R.id.outflow_change_tv, "详情");
                            setText(R.id.outflow_delete_tv, "");
                            break;
                        case -1://通还
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
                    if (null != mLibraryInfo && null != mLibraryInfo.mOperaterName) {
                        setTextColor(R.id.outflow_change_tv, Color.parseColor("#3F51B5"));
                        setTextColor(R.id.outflow_delete_tv, Color.parseColor("#ff0000"));
                        //修改，详情，撤回
                        setOnClickListener(R.id.outflow_change_tv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != mCallback) {
                                    mCallback.callbackChangeFlowManage(item);
                                }
                            }
                        });
                        //删除监听
                        setOnClickListener(R.id.outflow_delete_tv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //删除流出管理
                                if (null != mCallback) {
                                    mCallback.callbackDeleteFlowManage(item);
                                }
                            }
                        });
                    } else {
                        setOnClickListener(R.id.outflow_delete_tv, null);
                        setOnClickListener(R.id.outflow_change_tv, null);
                    }
                }
            }
        };
    }

    private CallbackChangeFlowOperation mCallback;

    public void setFlowManagementListener(CallbackChangeFlowOperation callback) {
        this.mCallback = callback;
    }

    public interface CallbackChangeFlowOperation {
        /**
         * 修改，详情，通还，撤回
         *
         * @param item
         */
        void callbackChangeFlowManage(FlowManageListBean item);

        /**
         * 删除流出管理本单
         *
         * @param item
         */
        void callbackDeleteFlowManage(FlowManageListBean item);
    }

}
