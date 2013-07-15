package com.gamesourcecode.input;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gamesourcecode.Game;
import com.gamesourcecode.button.Button;
import com.gamesourcecode.button.LetterButton;

public class OnTouchHandler implements OnTouchListener {

	private Game game;
	private int x, y, xOriginal, yOriginal;

	public OnTouchHandler(Game game) {
		this.game = game;
	}

	public boolean onTouch(View v, MotionEvent e) {

		x = (int) e.getX();
		y = (int) e.getY();

		if (e.getAction() == MotionEvent.ACTION_UP) {
			for (Button b : game.getButtons()) {
				if (b.isGrabbed()) {
					for(Button bb : game.getWordButtons()) {
						if(b.getCenter().intersect(bb.getRect()) && b instanceof LetterButton) {
							((LetterButton) b).onClick(bb);
						}
					}
					b.released();
				}
				if (b.getRect().intersects(x, y, x, y) && !hasMoved()) {
					b.onClick();
				}
			}
		}

		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			xOriginal = (int) e.getX();
			yOriginal = (int) e.getY();
			
			for (Button b : game.getButtons()) {
				if (b.getRect().intersects(x, y, x, y)) {
					b.grabbed();
				}
			}
		}

		if (e.getAction() == MotionEvent.ACTION_MOVE) {
			for (Button b : game.getLetterButtons()) {
				if (b.isGrabbed() && b.containsLetter()) {
					b.setXY(x, y);
				}
			}
		}

		return true;
	}
	
	private boolean hasMoved() {
		int area = 20;
		if(xOriginal + area > x && x > xOriginal - area) {
			if(yOriginal + area > y && y > yOriginal - area) {
				return false;
			}
		}
		return true;
	}
}
