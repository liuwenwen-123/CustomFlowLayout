package com.example.customflowlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private int jianju = 20;

    private ArrayList<ArrayList<View>> alllineViews ; // 记录所有的行 一行一行存储
    private List<Integer> lineHeights;  // 记录所有的行 一行一行存储 行高  用于layout

    public FlowLayout(Context context) {
        super(context);

    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

       private void  init(){
           alllineViews  = new ArrayList();
           lineHeights = new ArrayList<>();
       }

    /**
     * @param widthMeasureSpec  父容器给的  测量模式
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();

//        获取view padding 的宽高
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();


//        === 获取 FlowLayout 父容器的宽高
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int parentNeedwidth = 0;  // measure 过程中 子view 要求父容器的宽（FlowLayout）
        int parentNeedHeight = 0; // measure 过程中 子view 要求父容器的高（FlowLayout）
//        ====

//        -----


        ArrayList<View> lineViews = new ArrayList();
        int lineUseWidth = 0;  // 记录样使用了 多宽
        int lineHeight = 0; // 一行的高度

//          ===========


//       1； 获取所有的子view
        int childCount = getChildCount();
        for (int a = 0; a < childCount; a++) {
            View childView = getChildAt(a);
            //       2；获取view的 布局参数
            LayoutParams childLayoutParams = childView.getLayoutParams();
//           3: 要测量view  需要先知道view的测量模式（MeasureSpec）
//            3.1  获取孩子的测量模式(getChildMeasureSpec )
//            是根据 父容器的 测量模式 和孩子本身的大小 来确定   孩子的测量工 模式 widthMeasureSpec
            int childMeasureSpecWidth = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLayoutParams.width);
            int childMeasureSpecHeight = getChildMeasureSpec(heightMeasureSpec, paddingLeft + paddingRight, childLayoutParams.width);
//          获取到view 的测量模式  才能获取view的 宽高
            //测量的参数: 宽的测量模式  widthMeasureSpec,  高的的测量 int heightMeasureSpec
            childView.measure(childMeasureSpecWidth, childMeasureSpecHeight);
//          获取 到每个view 的大小
            int childmeasuredWidth = childView.getMeasuredWidth();
            int childmeasuredHeight = childView.getMeasuredHeight();

//          ===========
//            超过父容器的 宽 来换行
            if (lineUseWidth + childmeasuredWidth > parentWidth) {
                //-------------------------  保存下 用于  onLayout
                alllineViews.add(lineViews);
                lineHeights.add(lineHeight);
                //-------------------------

//                 换行之后  FlowLayout 的高度 == 所有行的高度
                parentNeedHeight = parentNeedHeight + lineHeight + jianju;
                parentNeedwidth = Math.max(parentNeedwidth, lineUseWidth + jianju);


                lineViews = new ArrayList<>();
                lineUseWidth = 0;  //行 的宽度 == 已经使用了的  + view的宽 + 间距 20
                lineHeight = 0;

            }

//          保存每行的view
            lineViews.add(childView);
            lineUseWidth = lineUseWidth + childmeasuredWidth + jianju;  //行 的宽度 == 已经使用了的  + view的宽 + 间距 20
            lineHeight = Math.max(lineHeight, childmeasuredHeight);  //  行的高度  等于 每行view 里面的 最高的view

//            处理最后那一行的状态
            if (a == childCount - 1) {

                alllineViews.add(lineViews);
                lineHeights.add(lineHeight);


                parentNeedHeight = parentNeedHeight + lineHeight + jianju;
                parentNeedwidth = Math.max(parentNeedwidth, lineUseWidth + jianju);

            }

        }

//        测量viewgroup 的宽高  （容器的宽高 来自 容器内部view的宽高）
//        但是  我自己本社 也是个view  我也需要测测自己所能  提供给孩子的 代销
//        获取我自己的 测亮模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightode = MeasureSpec.getMode(heightMeasureSpec);

        int realWidth = (widthMode == MeasureSpec.EXACTLY ? parentWidth : parentNeedwidth);
        int realHeight = (heightode == MeasureSpec.EXACTLY ? parentHeight : parentNeedHeight);
        setMeasuredDimension(realWidth, realHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int currentPaddingLeft = getPaddingLeft(); // 布局中 第一个view 的 paddingLeft
        int currentPaddingTop = getPaddingTop();// 布局中 第一个view 的 paddingTop
        // 获取 布局 中 所有的行数  一行 一行去布局
        int viewGroupLineCount = alllineViews.size();
//        获取 每行的view集合
        for (int a = 0; a < viewGroupLineCount; a++) {
            ArrayList<View> lineViews = alllineViews.get(a);
//             获取每行的 每个view
            int lineViewSize = lineViews.size();
//            获取每行的高度
            int lineHeight = lineHeights.get(a);
            for (int j = 0; j < lineViewSize; j++) {
                View view = lineViews.get(j);
                int left = currentPaddingLeft;
                int top = currentPaddingTop;
                int right = left + view.getMeasuredWidth();
                int bottom = top + view.getMeasuredHeight();
//               这第一view 摆放完成
                view.layout(left, top, right, bottom);
//                重新开始摆放 这一行第二个view
//                第二个view 的左间距==第一个view的paddingRight+jianju
                currentPaddingLeft = right + jianju;
            }
//            开始摆放其他行
            currentPaddingLeft=getPaddingLeft();
            currentPaddingTop=currentPaddingTop+lineHeight+jianju;
        }



       /* int childCount = getChildCount();
        for (int a = 0; a <childCount ; a++) {
            View childAt = getChildAt(a);
            childAt.layout(l,t,r,b);
        }*/

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

   /* class  LayourParams extends ViewGroup.MarginLayoutParams{
      private  int mLeft,mRight,mTop,mBottom;
        public LayourParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }*/
}
