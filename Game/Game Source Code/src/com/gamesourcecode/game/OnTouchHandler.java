package com.gamesourcecode.game;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gamesourcecode.button.game.GameButton;
import com.gamesourcecode.button.game.LetterButton;

public class OnTouchHandler implements OnTouchListener {

	private Game game;
	private int x, y, xOrigin, yOrigin;

	public OnTouchHandler(Game game) {
		this.game = game;
	}

	public boolean onTouch(View v, MotionEvent e) {

		x = (int) e.getX();
		y = (int) e.getY();
		
		Log.i("HOME - Touch Handler", "Touched");

		if (e.getAction() == MotionEvent.ACTION_UP) {
			for (GameButton b : game.getButtons()) {

				if (b.isGrabbed()) {
					if (b.getRect().intersects(x, y, x, y)) {
						b.onClick();
					}

					for (GameButton bb : game.getWordButtons()) {
						if (b.getCenter().intersect(bb.getRect()) && b instanceof LetterButton) {
							((LetterButton) b).onClick(bb);
						}
					}
					b.release();
				}
			}
		}

		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			xOrigin = (int) e.getX();
			yOrigin = (int) e.getY();

			for (GameButton b : game.getButtons()) {
				if (b.getRect().intersects(x, y, x, y)) {
					// Releases all the buttons so that only one can be grabbed
					// at the time
					for (GameButton bb : game.getButtons()) {
						bb.release();
					}
					b.grab();
					b.setTouchXY(x, y);
				}
			}
		}

		if (e.getAction() == MotionEvent.ACTION_MOVE) {
			for (GameButton b : game.getButtons()) {
				if (b.isGrabbed()) {
					b.setTouchXY(x, y);
					// If the button does not intersect its original rect, it
					// has been outside
					// This prevents the button to be "clicked" when dragged
					// back to its original location
					if (b instanceof LetterButton) {
						if (b.containsLetter()) ((LetterButton) b).setXY(x - xOrigin + b.getOriginX(), y - yOrigin + b.getOriginY());
						
						if (!b.getRect().intersect(b.getOriginRect())) {
							((LetterButton) b).outside();
						}
					}

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
