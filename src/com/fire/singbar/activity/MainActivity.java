package com.fire.singbar.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.fire.singbar.R;
import com.fire.singbar.utils.AbstractActivity;
import com.fire.singbar.widget.SegmentedView;
import com.fire.singbar.widget.SegmentedView.OnValueChageListener;

public class MainActivity extends AbstractActivity {

	private SegmentedView mSegmentedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		
		mSegmentedView.setTabSelected(0);
	}

	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		mSegmentedView = (SegmentedView) findViewById(R.id.segmentedView);
	}

	@Override
	public void initViewData() {
		mSegmentedView.addTabWithImageAndLabel(R.drawable.tabbar_1, "ktv");
		mSegmentedView.addTabWithImageAndLabel(R.drawable.tabbar_2, "消息");
		mSegmentedView.addTabWithImageAndLabel(R.drawable.tabbar_3, "我的");
	}
	
	@Override
	public void initViewListener() {
		mSegmentedView.setOnValueChageListener(new OnValueChageListener() {
			@Override
			public void valueChange(int index) {
				Toast.makeText(MainActivity.this, "选中了  " + index, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
