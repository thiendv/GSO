package com.gso.hogoapi.anim;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Util {

	public static void buttionPressed(Context context, final View v){
		AlphaAnimation animation1 = new AlphaAnimation(1f, 0.3f);
		animation1.setDuration(1000);
		animation1.setStartOffset(5000);
		animation1.setFillAfter(true);
		animation1.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				v.getBackground().setAlpha(255);
			}
		});
		v.startAnimation(animation1);
	}
}
