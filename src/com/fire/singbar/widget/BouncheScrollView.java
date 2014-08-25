package com.fire.singbar.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fire.singbar.widget.pulllistview.PullListView.AnimatorListenerNullImpl;

public class BouncheScrollView extends ScrollView {
	
	private View bounchView;
	
	private int orginalHeight;
	private float downY = -1;
	
	private boolean bounch;

	public BouncheScrollView(Context context) {
		super(context);
	}

	public BouncheScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addBounchView(View bounchView, int height) {
		if (getChildCount() <= 0 || !(getChildAt(0) instanceof LinearLayout)) {
			throw new RuntimeException("ScrollView must have child and the first child must be LinearLayout");
		}
		
		this.bounchView = bounchView;
		this.orginalHeight = height;
		
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-1, height);
		LinearLayout layout = (LinearLayout) getChildAt(0);
		layout.addView(bounchView, 0, params);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (null == bounchView || bounch) {
			return super.onTouchEvent(event);
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (-1 == downY) {
				downY = event.getRawY();
			}
			float distance = event.getRawY() - downY;
			downY = event.getRawY();
			distance = distance / (bounchView.getHeight() / 200.0f + 1);
			
			//下拉
			if (distance > 0) {
				distance += bounchView.getHeight();
				setBounchViewHeight((int)distance);
			}
			break;
		case MotionEvent.ACTION_UP:
			setNormal();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	@SuppressLint("NewApi")
	public void setNormal() {
		bounch = true;
		
		final ObjectAnimator oa = ObjectAnimator.ofInt(this, "BounchViewHeight", bounchView.getHeight(), orginalHeight);
		oa.setInterpolator(new LinearInterpolator());
		oa.setDuration(200);
		oa.start();
		oa.addListener(new AnimatorListenerNullImpl() {
			@Override
			public void onAnimationEnd(Animator arg0) {
				bounch = false;
				downY = -1;
			}
		});
	}
	
	private void setBounchViewHeight(int height) {
		android.view.ViewGroup.LayoutParams params = bounchView.getLayoutParams();
		params.height = height;
		bounchView.setLayoutParams(params);
	}
}
