package com.gamesourcecode.home.input;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gamesourcecode.button.Button;
import com.gamesourcecode.home.Home;

public class OnTouchHandler implements OnTouchListener {

	private Home home;

	private int x, y;

	public OnTouchHandler(Home home) {
		this.home = home;
	}

	public boolean onTouch(View v, MotionEvent e) {

		x = (int) e.getX();
		y = (int) e.getY();

		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			for (Button b : home.getButtons()) {
				if (b.getRect().intersect(x, y, x, y)) {
					b.onClick();
				}
			}
		}

		return false;

	}
}
