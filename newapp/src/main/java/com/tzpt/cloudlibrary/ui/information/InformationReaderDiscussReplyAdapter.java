package com.tzpt.cloudlibrary.ui.information;

import android.content.Context;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.abslistview.EasyLVAdapter;
import com.tzpt.cloudlibrary.base.adapter.abslistview.EasyLVHolder;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.utils.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/1/22.
 */

public class InformationReaderDiscussReplyAdapter extends EasyLVAdapter<CommentBean> {

    public InformationReaderDiscussReplyAdapter(Context context, List<CommentBean> list) {
        super(context, list, R.layout.view_discuss_reply_item);
    }

    @Override
    public void convert(EasyLVHolder holder, int position, CommentBean childBean) {
        holder.setText(R.id.discuss_reply_content_tv, StringUtils.formatCommentChildContent(childBean.mCommentName, childBean.mRepliedName, childBean.mContent));
    }
}
