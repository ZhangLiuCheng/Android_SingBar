package com.fire.singbar.widget.pulllistview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fire.singbar.widget.pulllistview.PullListView.AnimatorListenerNullImpl;

/**
 *
 * 在listView的上面，默认是看不见的，下拉listView的时候才会出现.
 * 
 * @author zhangliucheng
 *
 */
@SuppressLint({ "NewApi", "ViewConstructor" })
public class PullRefreshView extends LinearLayout {
	
	private final int trigger = PullListView.TRIGGER_HEIGHT;
	private final String pullInfo = "下拉刷新";
	private final String normalInfo = "释放刷新...";
	private final String refreshInfo = "努力加载中...";
	
	private HeaderTriggerRefresh mHeaderTriggerRefresh;
	
	private boolean isTrigger;
	
	private ImageView mArrow;
	private ProgressBar mProgress;
	private TextView mLabel;
	private TextView mUpdatTime;
	
	private float downY;
	
	public interface HeaderTriggerRefresh {
		void refresh();
		void refreshFinish();
	}
	
	public PullRefreshView(Context context, HeaderTriggerRefresh headerTriggerRefresh) {
		super(context);
		this.mHeaderTriggerRefresh = headerTriggerRefresh;
		initView();
		initUpdateTime();
	}
	
	public boolean headerOnTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			//下拉减速
			float distance = event.getRawY() - downY;
			downY = event.getRawY();
			distance = distance / (getPaddingTop() / 100.0f + 1);
			distance += getPaddingTop();
			
			//下拉匀速
//			float distance = event.getRawY() - downY;
			
			//如果是向上pull，直接滚动到0，并且返回
			if (distance < 0) {
				scrollDistance(0);
				return false;
			} 
			//设置下拉的幅度开始触发事件
			if (distance > 15) {
				scrollDistance(distance);
			}
			break;
		case MotionEvent.ACTION_UP:
			endScroll();
			break;
			
		default:
			break;
		}
		return true;
	}
	
	public void setArrowImage(int imageId) {
		mArrow.setBackgroundResource(imageId);
	}
	
	public void scrollDistance(float distance) {
		//isPulling为ture，说明当前在向下pull，反之
		final boolean isPulling = getPaddingTop() - distance < 0;
		
		setPaddingTop((int) distance);
		
		//paddingTop大于trigger，并且当前在向下pull，触发动画
		if (distance > trigger && isPulling) {
			setTriggerAnim(true);
		
		} else if (distance < trigger && !isPulling) {
			setTriggerAnim(false);
		}
	}
	
	public void endScroll() {
		if (getPaddingTop() > trigger) {
			mHeaderTriggerRefresh.refresh();
			isTrigger = true;
		} else if (getPaddingTop() < trigger) {
			mHeaderTriggerRefresh.refreshFinish();
			isTrigger = false;
		}
	}
	
	/**
	 * 恢复正常状态，将尖头图片显示，等待框隐藏，并设置label的信息.
	 */
	public void setNormal() {
		final ObjectAnimator oa = ObjectAnimator.ofInt(this, "paddingTop", getPaddingTop(), 0);
		oa.setInterpolator(new LinearInterpolator());
		oa.setDuration(200);
		oa.start();
		oa.addListener(new AnimatorListenerNullImpl() {
			@Override
			public void onAnimationEnd(Animator arg0) {
				mArrow.setVisibility(View.VISIBLE);
				mArrow.setRotation(0);
				mProgress.setVisibility(View.GONE);
				mLabel.setText(pullInfo);
				
				if (isTrigger) {
					refrehUpdateTime();
					isTrigger = false;
				}
			}
		});
	}
	
	/**
	 * 设置正在刷新，将尖头图片隐藏，等待框显示出来，并设置label的信息.
	 */
	public void setRefresh() {
		final ObjectAnimator oa = ObjectAnimator.ofInt(this, "paddingTop", getPaddingTop(), trigger);
		oa.setInterpolator(new LinearInterpolator());
		oa.setDuration(200);
		oa.start();
		oa.addListener(new AnimatorListenerNullImpl() {
			@Override
			public void onAnimationEnd(Animator arg0) {
				mArrow.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
				mLabel.setText(refreshInfo);
			}
		});
	}
	
	/**
	 * 这个方法供 ObjectAnimator.ofInt(this, "paddingTop", getPaddingTop(), trigger);使用.
	 * @param f
	 */
	public void setPaddingTop(int f) {
		setPadding(0, f, 0, 0);
		((View) getParent()).setPadding(0, 0, 0, -f);
	}
	
	/**
	 * 下拉或上拉的时候，尖头产生动画.
	 * @param isOpen
	 */
	public void setTriggerAnim(boolean isOpen) {
		if (isOpen) {
			if (mArrow.getRotation() == 0) {
				mLabel.setText(normalInfo);
				mArrow.animate().setDuration(100).setInterpolator(new AccelerateDecelerateInterpolator()).
				rotation(-180).start();
			}
		} else {
			if (mArrow.getRotation() == -180) {
				mLabel.setText(pullInfo);
				mArrow.animate().setDuration(100).setInterpolator(new AccelerateDecelerateInterpolator()).
				rotation(0).start();
			}
		}
	}
	
	private void initView() {
		final RelativeLayout layout = new RelativeLayout(getContext());
		addView(layout, LayoutParams.MATCH_PARENT, trigger);
		
		LinearLayout textLayout = new LinearLayout(getContext());
		textLayout.setOrientation(LinearLayout.VERTICAL);
		textLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		textLayout.setId(1);
		final RelativeLayout.LayoutParams textPrams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textPrams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		textPrams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		textPrams.bottomMargin = 10;
		layout.addView(textLayout, textPrams);
		
		//文字说明
		mLabel = new TextView(getContext());
		mLabel.setTextColor(Color.BLACK);
		mLabel.setText(pullInfo);
		mLabel.setTextSize(14);
		final LinearLayout.LayoutParams namePrams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textLayout.addView(mLabel, namePrams);
		
		//上次更新时间
		mUpdatTime = new TextView(getContext());
		mUpdatTime.setTextColor(Color.BLACK);
		mUpdatTime.setText("最后更新: 2014-7-12 17:15");
		mUpdatTime.setTextSize(14);
		final LinearLayout.LayoutParams timePrams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textLayout.addView(mUpdatTime, timePrams);
		
		final RelativeLayout imgLayout = new RelativeLayout(getContext());
		final RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(80, 80);
		imgParams.addRule(RelativeLayout.CENTER_VERTICAL);
		imgParams.addRule(RelativeLayout.LEFT_OF, textLayout.getId());
		imgParams.rightMargin = 20;
		layout.addView(imgLayout, imgParams);
		
		//尖头图片
		mArrow = new ImageView(getContext());
		final RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(20, 50);
		arrowParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		imgLayout.addView(mArrow, arrowParams);
		
		//进度条
		mProgress = new ProgressBar(getContext());
		mProgress.setVisibility(View.GONE);
		final RelativeLayout.LayoutParams progressPrams = new RelativeLayout.LayoutParams(50, 50);
		progressPrams.addRule(RelativeLayout.CENTER_IN_PARENT);
		imgLayout.addView(mProgress, progressPrams);
	}
	
	private void initUpdateTime() {
		SharedPreferences preferences = getContext().getSharedPreferences("updateTime", Activity.MODE_PRIVATE);
		String time = preferences.getString("time", getCurFormatterTime());
		mUpdatTime.setText(time);
	}
	
	private void refrehUpdateTime() {
		String time = getCurFormatterTime();
		mUpdatTime.setText(time);
		SharedPreferences.Editor editor = getContext().getSharedPreferences("updateTime", Activity.MODE_PRIVATE).edit();
		editor.putString("time", time);
		editor.commit();
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getCurFormatterTime() {
		SimpleDateFormat format = new SimpleDateFormat("最后更新: yyyy-MM-dd HH:mm");
		return format.format(new Date());
	}
}
