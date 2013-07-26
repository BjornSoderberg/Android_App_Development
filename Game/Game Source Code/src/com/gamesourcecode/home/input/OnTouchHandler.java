package com.gamesourcecode.home.input;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gamesourcecode.button.Button;
import com.gamesourcecode.home.Home;

public class OnTouchHandler implements OnTouchListener {

	private Home home;

	private int x, y, xOrigin, yOrigin;

	public OnTouchHandler(Home home) {
		this.home = home;
	}

	public boolean onTouch(View v, MotionEvent e) {

		x = (int) e.getX();
		y = (int) e.getY();

		if (e.getAction() == MotionEvent.ACTION_UP) {
			for (Button b : home.getButtons()) {
				if (b.getRect().intersect(x, y, x, y)) {
					b.onClick();
				}
				if (b.isGrabbed()) {
					b.release();
				}
			}
		}

		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			xOrigin = (int) e.getX();
			yOrigin = (int) e.getY();

			for (Button b : home.getButtons()) {
				if (b.getRect().intersects(x, y, x, y)) {
					for (Button bb : home.getButtons()) {
						bb.release();
					}
					b.grab();
					b.setTouchXY(x, y);
				}
			}
		}
		
		if(e.getAction() == MotionEvent.ACTION_MOVE) {
			for(Button b : home.getButtons()) {
				if(b.isGrabbed()) {
					b.setTouchXY(x, y);
				}
			}
		}

		return true;

	}

	private boolean hasMoved() {
		int area = 20;
		if (xOrigin + area > x && x > xOrigin - area) {
			if (yOrigin + area > y && y > yOrigin - area) return false;
		}
		return true;
	}
}
