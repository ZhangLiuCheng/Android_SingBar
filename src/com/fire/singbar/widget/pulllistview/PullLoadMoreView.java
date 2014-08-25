package com.fire.singbar.widget.pulllistview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
 * 在listView的下面，默认是看不见的，上拉listView的时候才会出现.
 * @author zhangliucheng
 *
 */
@SuppressLint({ "NewApi", "ViewConstructor" })
public class PullLoadMoreView extends LinearLayout {
	
	private final int trigger = PullListView.TRIGGER_HEIGHT;
	private final String pullInfo = "上拉显示更多...";
	private final String normalInfo = "释放显示更多...";
	private final String refreshInfo = "正在加载中...";
	
	private FooterTriggerLoadMore mFooterTriggerLoadmore;
	
	private ImageView mArrow;
	private ProgressBar mProgress;
	private TextView mLabel;
	
	private float downY;

	
	public interface FooterTriggerLoadMore {
		void loadMore();
		void loadMoreFinish();
	}
	
	public PullLoadMoreView(Context context, FooterTriggerLoadMore footerTriggerLoadmore) {
		super(context);
		this.mFooterTriggerLoadmore = footerTriggerLoadmore;
		initView();
	}
	
	public boolean footerOnTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			//上拉减速
			float distance = event.getRawY() - downY;
			downY = event.getRawY();
			distance = distance / (getPaddingBottom() / 100.0f + 1);
			distance -= getPaddingBottom();
			
			//上拉匀速
//			float distance = event.getRawY() - downY;

			if (distance > 0) {
				scrollDistance(0);
				return false;
			} 
			scrollDistance(Math.abs(distance));
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
		//isPulling为ture，说明当前在向上pull，反之
		final boolean isPulling = getPaddingBottom() - distance < 0;
		
		setPaddingBottom((int) distance);

		if (distance > trigger && isPulling) {
			setTriggerAnim(true);
		} else if (distance < trigger && !isPulling) {
			setTriggerAnim(false);
		}
	}
	
	public void endScroll() {
		if (getPaddingBottom() > trigger) {
			mFooterTriggerLoadmore.loadMore();
		} else if (getPaddingBottom() < trigger) {
			mFooterTriggerLoadmore.loadMoreFinish();
		}
	}
	
	/**
	 * 恢复正常状态，将尖头图片显示，等待框隐藏，并设置label的信息.
	 */
	public void setNormal() {
		final ObjectAnimator oa = ObjectAnimator.ofInt(this, "paddingBottom", getPaddingBottom(), 0);
		oa.setInterpolator(new LinearInterpolator());
		oa.setDuration(200);
		oa.start();
		oa.addListener(new AnimatorListenerNullImpl() {
			@Override
			public void onAnimationEnd(Animator arg0) {
				mArrow.setVisibility(View.VISIBLE);
				mArrow.setRotation(-180);
				mProgress.setVisibility(View.GONE);
				mLabel.setText(pullInfo);
			}
		});
	}
	
	/**
	 * 设置正在刷新，将尖头图片隐藏，等待框显示出来，并设置label的信息.
	 */
	public void setRefresh() {
		final ObjectAnimator oa = ObjectAnimator.ofInt(this, "paddingBottom", getPaddingBottom(), trigger);
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
	 * 这个方法供 ObjectAnimator.ofInt(this, "paddingBottom", getPaddingBottom(), trigger);使用.
	 * @param f
	 */
	public void setPaddingBottom(int f) {
		setPadding(0, 0, 0, f);
		//这句话很关键
		((View) getParent()).setPadding(0, -f, 0, 0);
	}
	
	/**
	 * 下拉或上拉的时候，尖头产生动画.
	 * @param isOpen
	 */
	public void setTriggerAnim(boolean isOpen) {
		if (isOpen) {
			if (mArrow.getRotation() == -180) {
				mLabel.setText(normalInfo);
				mArrow.animate().setDuration(100).setInterpolator(new AccelerateDecelerateInterpolator()).
				rotation(0).start();
			}
		} else {
			if (mArrow.getRotation() == 0) {
				mLabel.setText(pullInfo);
				mArrow.animate().setDuration(100).setInterpolator(new AccelerateDecelerateInterpolator()).
				rotation(-180).start();
			}
		}
	}
	
	private void initView() {
		final RelativeLayout layout = new RelativeLayout(getContext());
		addView(layout, LayoutParams.MATCH_PARENT, trigger);
		
		//文字说明
		mLabel = new TextView(getContext());
		mLabel.setId(1);
		mLabel.setTextColor(Color.BLACK);
		mLabel.setText(pullInfo);
		mLabel.setTextSize(14);
		final RelativeLayout.LayoutParams namePrams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		namePrams.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.addView(mLabel, namePrams);
		
		
		final RelativeLayout relLayout = new RelativeLayout(getContext());
		final RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(80, 80);
		relParams.addRule(RelativeLayout.LEFT_OF, mLabel.getId());
		relParams.leftMargin = 30;
		layout.addView(relLayout, relParams);
		
		//尖头图片
		mArrow = new ImageView(getContext());
		mArrow.setRotation(-180);
		final RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(20, 50);
		arrowParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		relLayout.addView(mArrow, arrowParams);
		
		//进度条
		mProgress = new ProgressBar(getContext());
		mProgress.setVisibility(View.GONE);
		final RelativeLayout.LayoutParams progressPrams = new RelativeLayout.LayoutParams(50, 50);
		progressPrams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		relLayout.addView(mProgress, progressPrams);
	}
}
