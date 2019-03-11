package com.example.myNestScroll;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.example.TopUtilKt;

// 类似 SwipeRefreshLayout但时横向的. Listener待完善.
public class PullRefreshLayout extends ViewGroup {
    private static final String TAG = "PullRefreshLayout";
    private static final int MAX_SCROLL = 300;  // mLeft 滑动做大距离
    private static final int STOP_SCROLL = 200; // mLeft 大于此值,认为开始刷新..且此处有动画.
    private final NestedScrollingParentHelper mParentHelper;
    private int mLeftScroll = 0;    //mTarget的left到parent/本ViewGroup的Left的距离
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private View mLeft; // left loading View
    private View mTarget;// RecyclerView or ListView

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mParentHelper = new NestedScrollingParentHelper(this);
//        mLeft = new CircleImageView(getContext(), Color.RED);
//        ((CircleImageView) mLeft).setImageDrawable(new ColorDrawable(Color.BLUE));
//        ((CircleImageView) mLeft).setImageResource(android.R.drawable.ic_menu_rotate);
//        addView(mLeft);
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (i == 0) mLeft = child;
                else mTarget = child;
//                if (!child.equals(mLeft)) {
//                    mTarget = child;
//                    break;
//            }
            }
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mTarget == null) {
            ensureTarget();
        }

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final View child = mTarget;
        final int childLeft = getPaddingLeft() + Math.max(0, mLeftScroll);
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

//        int circleWidth = mLeft.getMeasuredWidth();
//        int circleHeight = mLeft.getMeasuredHeight();
        if (mLeftScroll <= 0) {
            return;
        }
        mLeft.layout(0, childTop,//(height / 2 - circleHeight / 2),
                mLeftScroll, childTop + childHeight);//(height / 2 + circleHeight / 2));
        log("layout left t: " + childTop + ", r:" + mLeftScroll + ",b:" + (childTop + childHeight));
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() //&& !mReturningToStart && !mRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        boolean scrollLeft = dx < 0 && !target.canScrollHorizontally(-1);
        if (scrollLeft) { //手指从左到右.
            boolean handle = moveSpinner(-dx + mLeftScroll);
            consumed[0] = handle ? dx : 0;
        }
        if (dx > 0) {   //手指从右到左
            boolean handle = moveSpinner(-dx + mLeftScroll);
            consumed[0] = handle ? dx : 0;
        }
//        Log.d(TAG, "onNestedPreScroll: dx=" + dx + "...consumed = " + consumed[0]);
    }

    private boolean moveSpinner(float newOffset) {
        int offset = newOffset > MAX_SCROLL ? MAX_SCROLL : (int) newOffset;
        offset = offset < 0 ? 0 : offset;
        if (mLeftScroll == offset) return false;
        mLeft.offsetLeftAndRight(offset - mLeftScroll);
        mTarget.offsetLeftAndRight(offset - mLeftScroll);
        mLeftScroll = mTarget.getLeft();
        requestLayout();
        return true;
    }

    // 下面是动画--滑动完成后,left view的动画.
    @Keep
    public void setLeftScroll(int leftScroll) {
        log("setLeftScroll " + leftScroll);
        if (mLeftScroll != leftScroll) {
            moveSpinner(leftScroll);
        }
        mLeftScroll = leftScroll;
    }

    @Keep
    public int getLeftScroll() {
        return mLeftScroll;
    }

    private boolean mRefreshing;

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            ensureTarget();
            mRefreshing = refreshing;
            if (notify && mRefreshListener != null) {
                mRefreshListener.onRefresh();
            }
        }
    }

    @Nullable
    private Animator animator;

    private void start2OriginPosition() {
        if (mLeftScroll == 0) return;
        if (animator != null) {
            animator.cancel();
        }
        // left scroll < distance.
        final int scroll = mLeftScroll;
        boolean refresh = scroll > STOP_SCROLL;
        log("start animator.. scroll = " + scroll);
        ObjectAnimator oa = ObjectAnimator.ofInt(this, "leftScroll",
                scroll, refresh ? STOP_SCROLL : 0);
        oa.setInterpolator(null);//will be Linear.
        if (refresh) {
            AnimatorSet as = new AnimatorSet();
            ObjectAnimator circle = ObjectAnimator.ofFloat(mLeft, "rotation", 0, 360);
            circle.setInterpolator(null);
//            circle.setRepeatCount(ValueAnimator.INFINITE);
            circle.setDuration(2000);
            ObjectAnimator oa2 = ObjectAnimator.ofInt(this, "leftScroll",
                    STOP_SCROLL, 0);
            as.playSequentially(oa, circle, oa2);
            animator = as;
            setRefreshing(true, true);
        } else {
            setRefreshing(false, false);
            animator = oa;
        }
        animator.start();
        //left scroll > distance.
    }

    //下面的一串是 ParentHelper的.....在api>=21上,可以写不写都性.
    //---helper.parent
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
        log("onNestedScrollAccepted 179");
    }

    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    public void onStopNestedScroll(@NonNull View target) {
        log("onStopNestedScroll 193");
        mParentHelper.onStopNestedScroll(target);
        start2OriginPosition();
    }

    //------helper. parent. -------------end
    private void log(String msg) {
        TopUtilKt.log(msg, TAG);
    }
}