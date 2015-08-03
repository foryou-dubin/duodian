package com.foryou.truck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyListView extends ListView {
	Context context;
	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
	}

	public MyListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
//		Log.i("aa", "MyListView--dispatchTouchEvent");
		return super.dispatchTouchEvent(ev);
	}
	
	
	
	public  boolean vpFlag=false; 
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
	//	Log.i("aa", "MyListView--onInterceptTouchEvent");
		boolean flags = super.onInterceptTouchEvent(ev);

		if(!HorizontialListView.isTouchFlag){
			return false;
		}
		
		if(vpFlag){
			return false;
		}
		return flags;
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		boolean result = super.onInterceptTouchEvent(ev);
//		if (getFirstVisiblePosition() == 0) {
//			if (touchView != null) {
//				Rect rect = new Rect(touchView.getLeft(), touchView.getTop(), touchView.getRight(), touchView.getBottom());
//				if (rect.contains((int) ev.getX(), (int) ev.getY())) {
//					result = false;
//				}
//			}
//		}
//		return result;
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		Log.i("aa", "MyListView--onTouchEvent");
		return super.onTouchEvent(event);
	}

}

