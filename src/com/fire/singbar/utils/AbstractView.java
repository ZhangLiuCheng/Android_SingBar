package com.fire.singbar.utils;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

public abstract class AbstractView extends LinearLayout implements IInitializeStep {

	private AbstractActivity activity;
	
	public AbstractView(AbstractActivity activity) {
		super(activity);
		this.activity = activity;
	}
	
	public void init(int layoutId) {
		LayoutInflater.from(activity).inflate(layoutId, this);
		
		initData();
		
		initView();
		
		initViewData();
		
		initViewListener();
	}
	
	public AbstractActivity getActivity() {
		return this.activity;
	}
}
