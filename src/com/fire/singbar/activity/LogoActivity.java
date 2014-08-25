package com.fire.singbar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fire.singbar.R;
import com.fire.singbar.utils.AbstractActivity;

public class LogoActivity extends AbstractActivity {
	
	private final int delayMillis = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		
		init();
	}
	
	@Override
	public void initData() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				toMainActivity();
			}
		}, delayMillis);
	}

	@Override
	public void initView() {

	}

	@Override
	public void initViewData() {

	}

	@Override
	public void initViewListener() {

	}
	
	private void toMainActivity() {
		final Intent intent = new Intent(LogoActivity.this, MainActivity.class);
		startActivityWithAnim(intent);
		finish();
	}
}
