package com.tzpt.cloundlibrary.manager.widget.popupwindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

/**
 * Created by Administrator on 2018/12/19.
 */

public class PenaltyDealPPW extends PopupWindow implements View.OnClickListener {

    private TextView mOperatorPanelTitleTv;
    private TextView mPenaltyMoneyTv;
    private EditText mApplyFreeChargeReasonEt;
    private EditText mPayCostPwdEt;
    private Button mConfirmBtn;

    private OnApplyFreeChargeClickListener mOnApplyFreeChargeClickListener;

    private OnChargePenaltyClickListener mOnChargePenaltyClickListener;

    public PenaltyDealPPW(Context context) {
        View views = View.inflate(context, R.layout.ppw_penalty_deal, null);

        setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);

        views.findViewById(R.id.shadow_layout_view).setOnClickListener(this);
        this.setAnimationStyle(R.style.PPWAnimation);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        setContentView(views);
//        showAtLocation(view, Gravity.BOTTOM, 0, 0);

        mOperatorPanelTitleTv = (TextView) views.findViewById(R.id.operator_panel_title_tv);
        mPenaltyMoneyTv = (TextView) views.findViewById(R.id.penalty_money_tv);
        mApplyFreeChargeReasonEt = (EditText) views.findViewById(R.id.apply_free_charge_reason_et);
        mPayCostPwdEt = (EditText) views.findViewById(R.id.pay_cost_pwd_et);
        mConfirmBtn = (Button) views.findViewById(R.id.pay_cost_confirm_btn);
        mConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shadow_layout_view:
                mOnApplyFreeChargeClickListener = null;
                mOnChargePenaltyClickListener = null;
                this.dismiss();
                break;
            case R.id.pay_cost_confirm_btn:
                if (mOnApplyFreeChargeClickListener != null) {
                    mOnApplyFreeChargeClickListener.applyFreeCharge(mApplyFreeChargeReasonEt.getText().toString().trim(),
                            mPayCostPwdEt.getText().toString().trim());
                } else if (mOnChargePenaltyClickListener != null) {
                    mOnChargePenaltyClickListener.chargePenalty(mPayCostPwdEt.getText().toString().trim());
                }
                this.dismiss();
                break;
        }
    }

    public void setOnApplyFreeChargeClickListener(OnApplyFreeChargeClickListener listener) {
        mOnApplyFreeChargeClickListener = listener;
    }

    public void setOnChargePenaltyClickListener(OnChargePenaltyClickListener listener) {
        mOnChargePenaltyClickListener = listener;
    }

    /**
     * popUpWindow 显示位置
     */
    @Override
    public void showAsDropDown(View anchorView, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchorView.getGlobalVisibleRect(rect);
            int h = anchorView.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchorView, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    public void showCashChargePenalty(double penalty) {
        mPenaltyMoneyTv.setText(MoneyUtils.formatMoney(penalty));
        mPenaltyMoneyTv.setVisibility(View.VISIBLE);
        mApplyFreeChargeReasonEt.setVisibility(View.GONE);
        mOperatorPanelTitleTv.setText("收罚金");
        mConfirmBtn.setText("确认收现金");
    }

    public void showSubstituteChargePenalty(double penalty) {
        mPenaltyMoneyTv.setText(MoneyUtils.formatMoney(penalty));
        mPenaltyMoneyTv.setVisibility(View.VISIBLE);
        mApplyFreeChargeReasonEt.setVisibility(View.GONE);
        mOperatorPanelTitleTv.setText("代收罚金");
        mConfirmBtn.setText("确认收现金");
    }

    public void showApplyFreeChargeView() {
        mPenaltyMoneyTv.setVisibility(View.GONE);
        mApplyFreeChargeReasonEt.setVisibility(View.VISIBLE);
        mOperatorPanelTitleTv.setText("申请免单");
        mConfirmBtn.setText("提交申请");
    }

    public interface OnApplyFreeChargeClickListener {
        void applyFreeCharge(String reason, String pwd);
    }

    public interface OnChargePenaltyClickListener {
        void chargePenalty(String pwd);
    }
}
