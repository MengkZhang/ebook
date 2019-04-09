package com.tzpt.cloundlibrary.manager.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;

import com.tzpt.cloundlibrary.manager.utils.AllInputFilter;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/10.
 */

public class StatisticsConditionBean{
    @NonNull
    private final String mTitle;
    @NonNull
    private final ConditionType mConditionType;

    private final int mKeyboardType;
    @Nullable
    private final InputFilter[] mInputFilters;
    private final SingleSelectionCondition mSingleSelectionCondition;

    private final boolean mIsAutoCompletion;
    private final AutoCompletionResult mAutoCompletionResult;

    private final boolean mIsCompare;
    private final Compare mCompare;

    private String mSingleConditionKey;
    private String mStartConditionKey;
    private String mEndConditionKey;
    private String mSingleValue;
    private String mStartValue;
    private String mEndValue;

    StatisticsConditionBean(@NonNull String title, @NonNull ConditionType conditionType, @Nullable String singleConditionKey,
                            @Nullable String startConditionKey, @Nullable String endConditionKey, int keyBordType,
                            @Nullable InputFilter[] inputFilters, SingleSelectionCondition singleSelectionCondition,
                            boolean isAutoCompletion, AutoCompletionResult autoCompletionResult,
                            boolean isCompare, Compare compare) {
        mTitle = title;
        mConditionType = conditionType;
        mSingleConditionKey = singleConditionKey;
        mStartConditionKey = startConditionKey;
        mEndConditionKey = endConditionKey;
        mKeyboardType = keyBordType;
        mInputFilters = inputFilters;
        mSingleSelectionCondition = singleSelectionCondition;
        mIsAutoCompletion = isAutoCompletion;
        mAutoCompletionResult = autoCompletionResult;

        mIsCompare = isCompare;
        mCompare = compare;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public ConditionType getConditionType() {
        return mConditionType;
    }

    public void setSingleConditionKey(String key) {
        mSingleConditionKey = key;
    }

    public String getSingleConditionKey() {
        return mSingleConditionKey;
    }

    public void setStartConditionKey(String key) {
        mStartConditionKey = key;
    }

    public String getStartConditionKey() {
        return mStartConditionKey;
    }

    public void setEndConditionKey(String key) {
        mEndConditionKey = key;
    }

    public String getEndConditionKey() {
        return mEndConditionKey;
    }


    public String getSingleValue() {
        return mSingleValue;
    }

    public void setSingleValue(String mSingleValue) {
        this.mSingleValue = mSingleValue;
    }

    public String getStartValue() {
        return mStartValue;
    }

    public void setStartValue(String mStartValue) {
        this.mStartValue = mStartValue;
    }

    public String getEndValue() {
        return mEndValue;
    }

    public void setEndValue(String mEndValue) {
        this.mEndValue = mEndValue;
    }

    public int getKeyboardType() {
        return mKeyboardType;
    }

    @Nullable
    public InputFilter[] getInputFilters() {
        return mInputFilters;
    }

    public boolean isNeedCompare() {
        return mIsCompare;
    }

    public boolean compare() {
        return mCompare.compare(mStartValue, mEndValue);
    }

    public void getSingleSelectionCondition() {
        mSingleSelectionCondition.getData();
    }

    public void getAutoCompleteResult() {
        if (mConditionType == ConditionType.DateSelection
                || mConditionType == ConditionType.DoubleInput) {
            mAutoCompletionResult.autoCompletion(mStartValue, mEndValue);
        } else {
            mAutoCompletionResult.autoCompletion(mSingleValue);
        }
    }

    public boolean isAutoCompletion() {
        return mIsAutoCompletion;
    }


    public static class Builder {
        @NonNull
        private final String mTitle;
        @NonNull
        private final ConditionType mConditionType;

        private String mSingleConditionKey;
        private String mStartConditionKey;
        private String mEndConditionKey;
        private int mKeyboardType;
        private InputFilter[] mInputFilters = new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(100)};
        private SingleSelectionCondition mSingleSelectionCondition;
        private boolean mIsAutoCompletion = false;
        private AutoCompletionResult mAutoCompletionResult;

        private boolean mIsCompare = false;
        private Compare mCompare;

        public Builder(@NonNull String title, @NonNull ConditionType conditionType) {
            mTitle = title;
            mConditionType = conditionType;
        }

        public Builder setSingleConditionKey(String singleConditionKey) {
            mSingleConditionKey = singleConditionKey;
            return this;
        }

        public Builder setStartConditionKey(String startConditionKey) {
            mStartConditionKey = startConditionKey;
            return this;
        }

        public Builder setEndConditionKey(String endConditionKey) {
            mEndConditionKey = endConditionKey;
            return this;
        }

        public Builder setKeyboardType(int keyboardType) {
            mKeyboardType = keyboardType;
            return this;
        }

        public Builder setInputFilter(InputFilter[] inputFilter) {
            mInputFilters = inputFilter;
            return this;
        }

        public Builder setSingleSelectionCondition(SingleSelectionCondition singleSelectionCondition) {
            mSingleSelectionCondition = singleSelectionCondition;
            return this;
        }

        public Builder setAutoCompletion(AutoCompletionResult autoCompletion) {
            mIsAutoCompletion = true;
            mAutoCompletionResult = autoCompletion;
            return this;
        }

        public Builder setCompare(Compare compare) {
            mIsCompare = true;
            mCompare = compare;
            return this;
        }

        public StatisticsConditionBean build() {
            return new StatisticsConditionBean(mTitle, mConditionType, mSingleConditionKey,
                    mStartConditionKey, mEndConditionKey, mKeyboardType, mInputFilters,
                    mSingleSelectionCondition, mIsAutoCompletion, mAutoCompletionResult,
                    mIsCompare, mCompare);
        }
    }

    public enum ConditionType {
        SingleInput,
        DoubleInput,
        DateSelection,
        SingleSelection
    }

    public interface SingleSelectionCondition {
        void getData();
    }

    public interface AutoCompletionResult {
        void autoCompletion(String... arg);
    }

    public interface Compare {
        boolean compare(String arg0, String arg1);
    }
}
