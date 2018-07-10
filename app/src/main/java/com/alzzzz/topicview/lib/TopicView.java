package com.alzzzz.topicview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alzzzz.topicview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Discription:话题view
 * Created by sz on 2018/7/10.
 */

public class TopicView extends ViewGroup {
    private int mLineMargin;
    private int mWordMargin;
    private int mMaxLineNum = 10;
    private Context mContext;
    private List<String> mTopics = new ArrayList<>();
    private OnTopicClickListener onTopicClickListener;

    public TopicView(Context context) {
        this(context, null);
    }

    public TopicView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.topics_view);
            mMaxLineNum = mTypedArray.getInteger(R.styleable.topics_view_maxLine, Integer.MAX_VALUE);
            mLineMargin = mTypedArray.getDimensionPixelOffset(R.styleable.topics_view_lineMargin, 0);
            mWordMargin = mTypedArray.getDimensionPixelOffset(R.styleable.topics_view_topicMargin, 0);
            mTypedArray.recycle();
        }
    }

    /**
     * 设置标签列表
     * 如果是初始设置标签，需记录已选中的标签，取消标签时弹窗提示
     *
     *
     *  @param topics 话题
     */
    public synchronized void setTopics(List<String> topics) {
        //清空原有的标签
        removeAllViews();
        mTopics.clear();

        if (topics != null) {
            mTopics.addAll(topics);
            int size = mTopics.size();
            for (int i = 0; i < size; i++) {
                addLabel(mTopics.get(i), i);
            }
        }
    }

    private void addLabel(final String mTopic, int position) {
        final View topicView = LayoutInflater.from(mContext).inflate(R.layout.topic_content_layout, this, false);

        TextView tvTopic = topicView.findViewById(R.id.tv_topic_content);
        ImageView ivDel = topicView.findViewById(R.id.iv_topic_del);
        tvTopic.setText(mTopic);
        //label通过tag保存自己的位置(position)
        ivDel.setTag(position);
        mTopics.add(mTopic);

        ivDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTopicClickListener != null){
                    onTopicClickListener.onDelTopic(mTopic, (Integer) v.getTag());
                }
                mTopics.remove(mTopic);
                removeView(topicView);
            }
        });

        addView(topicView);
    }

    @Nullable
    public List<String> getTopics(){
        return mTopics;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

        int contentHeight = 0; //记录内容的高度
        int lineWidth = 0; //记录行的宽度
        int maxLineWidth = 0; //记录最宽的行宽
        int maxItemHeight = 0; //记录一行中item高度最大的高度

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);

            if (maxWidth < lineWidth + view.getMeasuredWidth()) {
                contentHeight += mLineMargin;
                contentHeight += maxItemHeight;
                maxItemHeight = 0;
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
                lineWidth = 0;
            }
            maxItemHeight = Math.max(maxItemHeight, view.getMeasuredHeight());
            lineWidth += (mWordMargin+view.getMeasuredWidth());
        }

        contentHeight += maxItemHeight;
        maxLineWidth = Math.max(maxLineWidth, lineWidth);

        setMeasuredDimension(measureWidth(widthMeasureSpec, maxLineWidth),
                measureHeight(heightMeasureSpec, contentHeight));
    }

    private int measureWidth(int measureSpec, int contentWidth) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = contentWidth + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumWidth());
        return result;
    }

    private int measureHeight(int measureSpec, int contentHeight) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = contentHeight + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumHeight());
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int lineNum = 1;

        int x = getPaddingLeft();
        int y = getPaddingTop();

        int contentWidth = right - left;
        int maxItemHeight = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (contentWidth < x + view.getMeasuredWidth() + getPaddingRight()) {
                lineNum++;
                if (lineNum > mMaxLineNum) {
                    break;
                }
                x = getPaddingLeft();
                y += mLineMargin;
                y += maxItemHeight;
                maxItemHeight = 0;
            }
            view.layout(x, y, x + view.getMeasuredWidth(), y + view.getMeasuredHeight());
            x += view.getMeasuredWidth();
            x += mWordMargin;
            maxItemHeight = Math.max(maxItemHeight, view.getMeasuredHeight());
        }
    }

    /**
     * 设置话题点击监听
     *
     * @param onTopicClickListener
     */
    public void setOnTopicClickListener(OnTopicClickListener onTopicClickListener){
        this.onTopicClickListener = onTopicClickListener;
    }

    public interface OnTopicClickListener{
        void onDelTopic(String topic, int position);
    }
}
