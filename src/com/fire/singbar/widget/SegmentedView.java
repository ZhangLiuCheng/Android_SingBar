package com.fire.singbar.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fire.singbar.R;

/**
 * 多选一控件.
 * @author zhangliucheng
 *
 */
public class SegmentedView extends LinearLayout {
	
	private OnValueChageListener mOnValueChageListener;
	
	private int tabBackgroundId = R.drawable.segmented_tab;		//默认tab的背景选中和为选中效果是模仿ios的
	private int tabTextColorId = R.color.tab_text_color;
	
	private boolean canChange = true;
	
	private int deselectedTabId =  -1;
	
	private OnClickListener tabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.isSelected() || !canChange) {
				return;
			}
			for (int i = 0; i < getChildCount(); i++) {
				final Tab tab = (Tab) getChildAt(i);
				if (tab.isSelected()) {
					deselectedTabId = i;
				}
				tab.setSelected(false);
			}
			((Tab) v).setSelected(true);
			if (null != mOnValueChageListener) {
				mOnValueChageListener.valueChange(Integer.parseInt(v.getTag().toString()));
			}
		}
	};
	
	public SegmentedView(Context context) {
		super(context);
		init();
	}
	
	public SegmentedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void setOnValueChageListener(OnValueChageListener mOnValueChageListener) {
		this.mOnValueChageListener = mOnValueChageListener;
	}
	
	/**
	 * 设置SegmentedView 是否可以切换状态.
	 * @param canChange true可以切换
	 */
	public void setCanChange(boolean canChange) {
		this.canChange = canChange;
	}

	public void setTabBackgroundId(int tabBackgroundId) {
		this.tabBackgroundId = tabBackgroundId;
	}
	
	public void setTabTextColorId(int tabTextColorId) {
		this.tabTextColorId = tabTextColorId;
	}

	public int getDeselectedTabId() {
		return deselectedTabId;
	}

	/**
	 * 设置tab选中.
	 * @param index
	 */
	public void setTabSelected(int index) {
		if (index < 0 || index >= getChildCount()) {
			return;
		}
		
		final View view = getChildAt(index);
		view.performClick();
	}
	
	/**
	 * 增加标题label.
	 * @param label
	 * @return 
	 */
	public void addTabWithLabel(String label) {
		final Tab tab = new Tab(label);
		addTab(tab);
	}
	
	/**
	 * 增加ImageView.
	 * @param imageId
	 */
	public void addTabWithImage(int imageId) {
		final Tab tab = new Tab(imageId);
		addTab(tab);
	}
	
	/**
	 * 增加个有图片和标题的tab.
	 * @param imageId  图片背景里面包括选中和没选中状态	例如
	 * @param label   imageView下面的标题
	 */
	public void addTabWithImageAndLabel(int imageId, String label) {
		final Tab tab = new Tab(imageId, label);
		addTab(tab);
	}
	
	public interface OnValueChageListener {
		void valueChange(int index);
	}
	
	private void init() {
		setBackgroundResource(R.drawable.segmented_background);
		setPadding(5, 5, 5, 5);
	}
	
	private synchronized void addTab(Tab addTab) {
		addView(addTab);
		addTab.setOnClickListener(tabClickListener);
	}
	
	/**
	 * 图片id，要优选中和没选中效果，如下面这样.
	 */
	private class Tab extends LinearLayout {
		public Tab(Context context) {
			super(context);
			initSetting();
		}
		
		public Tab(String label) {
			this(SegmentedView.this.getContext());
			final TextView textView = createLabel(label);
			addView(textView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		
		public Tab(int imageId) {
			this(SegmentedView.this.getContext());
			final ImageView imageView = createImageView(imageId);
			addView(imageView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		
		public Tab(int imageId, String label) {
			this(SegmentedView.this.getContext());
			final ImageView imageView = createImageView(imageId);
			final TextView textView = createLabel(label);
			addView(imageView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			addView(textView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		
		private void initSetting() {
			setTag(SegmentedView.this.getChildCount());
			setOrientation(LinearLayout.VERTICAL);
			final LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			setGravity(Gravity.CENTER);
			setPadding(0, 5, 0, 5);
			setLayoutParams(params);
			setBackgroundResource(tabBackgroundId);
		}
		
		@Override
		public void setSelected(boolean selected) {
			super.setSelected(selected);
			for (int i = 0; i < getChildCount(); i++) {
				final View view = getChildAt(i);
				view.setSelected(selected);
			}
		}
		
		private TextView createLabel(String label) {
			final TextView textView = new TextView(getContext());
			final ColorStateList csl = (ColorStateList) getResources().getColorStateList(tabTextColorId); 
			textView.setTextColor(csl);
			textView.setText(label);
			textView.setTextSize(12);
			return textView;
		}
		
		private ImageView createImageView(int imageId) {
			final ImageView imageView = new ImageView(getContext());
			imageView.setBackgroundResource(imageId);
			return imageView;
		}
	}
}
