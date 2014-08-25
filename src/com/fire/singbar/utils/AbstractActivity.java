package com.fire.singbar.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

import com.fire.singbar.R;

public abstract class AbstractActivity extends Activity implements IInitializeStep {
	
	/**
	 *	view 监听Activity里onActivityResult回调事件.
	 */
	public interface ActivityResultCallback {
		void onActivityResult(int requestCode, int resultCode, Intent data);
	}
	
	private ActivityResultCallback mActivityResultCallback;
	
	public void setActivityResultCallback(ActivityResultCallback activityResultCallback) {
		this.mActivityResultCallback = activityResultCallback;
	}
	
	/**
	 * 依次调用 
	 * 	<p>initData(); </p> 
	 * 	<p>initView(); </p> 
	 * 	<p>initViewData();</p>  
	 * 	<p>initViewListener();方法.
	 */
	public void init() {
		
		initData();
		
		initView();
		
		initViewData();
		
		initViewListener();
	}
	
	public void startActivityWithAnim(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.activity_right_in, R.anim.activity_right_exit);
	}
	
	public void startActivityForResultWithAnim(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.activity_right_in, R.anim.activity_right_exit);
	}
	
	public void finishActivityWithAnim() {
		this.finish();
		overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_exit);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishActivityWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (null != mActivityResultCallback) {
			mActivityResultCallback.onActivityResult(requestCode, resultCode, data);
			mActivityResultCallback = null;
		}
	}
}
