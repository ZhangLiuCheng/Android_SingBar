package com.fire.singbar.widget.pulllistview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.fire.singbar.R;
import com.fire.singbar.widget.pulllistview.PullLoadMoreView.FooterTriggerLoadMore;
import com.fire.singbar.widget.pulllistview.PullRefreshView.HeaderTriggerRefresh;

/**
 * 下拉刷新，下拉加载更多的listView.
 * @author zhangliucheng
 *
 */
@SuppressLint("NewApi")
public class PullListView extends RelativeLayout implements OnTouchListener, OnScrollListener,
							HeaderTriggerRefresh, FooterTriggerLoadMore {
	
	public static final int TRIGGER_HEIGHT = 80;
	
	private int defaultColor = Color.WHITE;
	private int defaultArrow = R.drawable.black_arrow;
	
	private final Handler mHandler = new Handler();
	
	private boolean listViewIsRefreshing;
	private boolean listViewIsLoadMore;
	
	private PullRefreshView refreshView;
	private PullLoadMoreView loadMoreView;
	private ListView mListView;
	
	private ScrollState scrollState = ScrollState.NORMAL;
	private ListViewPullDelegate listViewDelegate;
	
	private LayoutDirection layoutDirection;
	
	private enum ScrollState { NORMAL, TOP, DWON }				
	private enum LayoutDirection { UPWARD, DOWNWARD }
	
	public PullListView(Context context) {
		super(context);
		initView();
	}
	
	public PullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	public interface ListViewPullDelegate {
		void pullListViewTriggerRefresh(PullListView listView);
		void pullListViewTriggerLoadMore(PullListView listView);
	}
	
	public void setListViewDelegate(ListViewPullDelegate listViewDelegate) {
		this.listViewDelegate = listViewDelegate;
	}

	public ListView getListView() {
		return mListView;
	}

	public void setAdapter(ListAdapter adapter) {
		mListView.setAdapter(adapter);
	}
	
	public ListAdapter getAdapter() {
		return mListView.getAdapter();
	}
	
	/**
	 * 设置刷新和加载更多View的背景色.
	 * @param colorId
	 */
	public void setRefreshAndLoadMoreViewBackground(int colorId) {
		refreshView.setBackgroundColor(colorId);
		loadMoreView.setBackgroundColor(colorId);
	}
	
	/**
	 * 设置尖头图片.
	 * @param imageId
	 */
	public void setArrowImage(int imageId) {
		refreshView.setArrowImage(imageId);
		loadMoreView.setArrowImage(imageId);
	}
	
	/**
	private VelocityTracker mVelocityTracker;
	private boolean intercept = false;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (null == mVelocityTracker) {
			mVelocityTracker = VelocityTracker.obtain();
		}

		mVelocityTracker.addMovement(ev);
		mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity());
		if (mVelocityTracker.getYVelocity() == 0) {
			intercept = true;
		} else {
			intercept = false;
		}

		if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
			if (null != mVelocityTracker) {
				mVelocityTracker.clear();
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			intercept = false;
		}
		return super.onInterceptTouchEvent(ev);
	}
	*/

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			getParent().requestDisallowInterceptTouchEvent(false);
//		} else if (event.getAction() == MotionEvent.ACTION_UP) {
//			getParent().requestDisallowInterceptTouchEvent(true);
//		}
		/**
		if (intercept) {
			return super.onTouchEvent(event);
		}
		*/
		//正在刷新或者加载，直接返回
		if (listViewIsRefreshing || listViewIsLoadMore) {
			return false;

		//listView滑动到顶部，并且继续向下滑动
		} else if (scrollState == ScrollState.TOP) {
			return refreshView.headerOnTouch(v, event);

		//向上滑动
		} else if (scrollState == ScrollState.DWON) {
			return loadMoreView.footerOnTouch(v, event);
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public void refresh() {
		//先滚动到顶部
		if (mListView.getChildCount() > 0) {
			mListView.setSelection(0);
		}

		mListView.setEnabled(false);
		setListViewIsRefreshing(true);
		
		if (null != listViewDelegate) {
			//延迟是为了有刷新的动画，也可以直接调用
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					listViewDelegate.pullListViewTriggerRefresh(PullListView.this);
				}
			}, 500);
//			listViewDelegate.pullListViewTriggerRefresh(this);
		}
	}
	
	@Override
	public void refreshFinish() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListView.setEnabled(true);
				setListViewIsRefreshing(false);
			}
		});
	}
	
	@Override
	public void loadMore() {
		mListView.setEnabled(false);
		setListViewIsLoadMore(true);
		if (null != listViewDelegate) {
			//延迟是为了有刷新的动画，也可以直接调用
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					listViewDelegate.pullListViewTriggerLoadMore(PullListView.this);
				}
			}, 500);
//			listViewDelegate.pullListViewTriggerLoadMore(this);
		}
	}

	@Override
	public void loadMoreFinish() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListView.setEnabled(true);
				setListViewIsLoadMore(false);				
			}
		});
	}
	
	/**
	 * 设置RefreshView状态.
	 * @param isRefreshing true 进入刷新状态，否则进入正常状态
	 */
	private void setListViewIsRefreshing(boolean isRefreshing) {
		if (!listViewIsRefreshing && isRefreshing) {
			refreshView.setRefresh();
			listViewIsRefreshing = true;
		} else if (!isRefreshing) {
			refreshView.setNormal();
			listViewIsRefreshing = false;
		}
	}
	
	/**
	 * 设置LoadMoreView状态.
	 * @param isLoadMore true 进入加载更多，否则进入正常状态
	 */
	private void setListViewIsLoadMore(boolean isLoadMore) {
		if (!listViewIsLoadMore && isLoadMore) {
			loadMoreView.setRefresh();
			listViewIsLoadMore = true;
		} else if (!isLoadMore) {
			loadMoreView.setNormal();
			listViewIsLoadMore = false;
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if (firstVisibleItem == 0) {
			final View f =  mListView.getChildAt(0);
			if (null != f && f.getTop() == 0) {
				setPullDirection(LayoutDirection.DOWNWARD);
				scrollState = ScrollState.TOP;
				return;
			}
		} 
		if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
			final View fb =  mListView.getChildAt(mListView.getChildCount() - 1);
			if (null != fb && getHeight() == fb.getBottom()) {
				setPullDirection(LayoutDirection.UPWARD);
				scrollState = ScrollState.DWON;
				return;
			}
		} 
		
		scrollState = ScrollState.NORMAL;
	}
	
	private void initView() {
		listViewIsRefreshing = false;
		listViewIsLoadMore = false;
		
		//添加刷新view
		refreshView = new PullRefreshView(getContext(), this);
		refreshView.setId(1);
		addView(refreshView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		//添加listView
		mListView = new ListView(getContext());
		mListView.setId(2);
//		mListView.setDividerHeight(10);
//		mListView.setDivider(null);
//		mListView.setSelector(android.R.color.transparent);
		mListView.setVerticalScrollBarEnabled(true);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		addView(mListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		//添加加载更多
		loadMoreView = new PullLoadMoreView(getContext(), this);
		loadMoreView.setId(3);
		addView(loadMoreView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		//设置监听事件
		mListView.setOnTouchListener(this);
		mListView.setOnScrollListener(this);
		
		setRefreshAndLoadMoreViewBackground(defaultColor);
		
		//设置默认状态
		setPullDirection(LayoutDirection.DOWNWARD);
		refreshView.setArrowImage(defaultArrow);
		loadMoreView.setArrowImage(defaultArrow);
	}
	
	/**
	 * 但listView滑动到顶部或者顶部会掉用该方法，重新设置布局方式.
	 * @param layoutDirection 为true的时候(listView下拉到顶部)， loadMoreView  -below- listView，listView  -blow -refreshView.
	 * 					为false的时候(listView上拉到底部)，listView  -above -refreshView, loadMoreView  -above- listView
	 */
	private void setPullDirection(LayoutDirection layoutDirection) {
		if (this.layoutDirection == layoutDirection) {
			return;
		}
		this.layoutDirection = layoutDirection;
		
		if (this.layoutDirection == LayoutDirection.DOWNWARD) {
			final LayoutParams refreshParams = new LayoutParams(refreshView.getLayoutParams().width, 
					refreshView.getLayoutParams().height);
			refreshParams.topMargin = -TRIGGER_HEIGHT;
			updateViewLayout(refreshView, refreshParams);
			
			final LayoutParams listViewParams = new LayoutParams(mListView.getLayoutParams().width, 
					mListView.getLayoutParams().height);
			listViewParams.addRule(BELOW, refreshView.getId());
			updateViewLayout(mListView, listViewParams);
			
			final LayoutParams loadMoreParams = new LayoutParams(loadMoreView.getLayoutParams().width, 
					loadMoreView.getLayoutParams().height);
			loadMoreParams.addRule(BELOW, mListView.getId());
			loadMoreParams.bottomMargin = -TRIGGER_HEIGHT;
			updateViewLayout(loadMoreView, loadMoreParams);
			
		} else if (this.layoutDirection == LayoutDirection.UPWARD) {
			final LayoutParams loadMoreParams = new LayoutParams(loadMoreView.getLayoutParams().width, 
					loadMoreView.getLayoutParams().height);
			loadMoreParams.bottomMargin = -TRIGGER_HEIGHT;
			loadMoreParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
			updateViewLayout(loadMoreView, loadMoreParams);
			
			final LayoutParams listViewParams = new LayoutParams(mListView.getLayoutParams().width, 
					mListView.getLayoutParams().height);
			listViewParams.addRule(ABOVE, loadMoreView.getId());
			updateViewLayout(mListView, listViewParams);
			
			final LayoutParams refreshParams = new LayoutParams(refreshView.getLayoutParams().width, 
					refreshView.getLayoutParams().height);
			refreshParams.topMargin = -TRIGGER_HEIGHT;
			refreshParams.addRule(ABOVE, mListView.getId());
			updateViewLayout(refreshView, refreshParams);
		}
	}
	
	public static class AnimatorListenerNullImpl implements AnimatorListener {
		@Override
		public void onAnimationCancel(Animator animation) {
		}
		@Override
		public void onAnimationEnd(Animator animation) {
		}
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		@Override
		public void onAnimationStart(Animator animation) {
		}
	}
}
