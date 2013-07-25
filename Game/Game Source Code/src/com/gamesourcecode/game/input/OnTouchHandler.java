package com.gamesourcecode.game.input;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gamesourcecode.button.Button;
import com.gamesourcecode.button.game.GameButton;
import com.gamesourcecode.button.game.LetterButton;
import com.gamesourcecode.button.game.WordButton;
import com.gamesourcecode.game.Game;

public class OnTouchHandler implements OnTouchListener {

	private Game game;
	private int x, y, xOrigin, yOrigin;

	public OnTouchHandler(Game game) {
		this.game = game;
	}

	public boolean onTouch(View v, MotionEvent e) {

		x = (int) e.getX();
		y = (int) e.getY();
		
		if (e.getAction() == MotionEvent.ACTION_UP) {
			for (GameButton b : game.getButtons()) {
				if (b.getRect().intersects(x, y, x, y) && !hasMoved() && !b.hasBeenOutside()) {
					b.onClick();
				}
				
				if (b.isGrabbed()) {
					for(GameButton bb : game.getWordButtons()) {
						if(b.getCenter().intersect(bb.getRect()) && b instanceof LetterButton) {
							((LetterButton) b).onClick(bb);
						}
					}
					b.released();
				}
			}
		}

		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			xOrigin = (int) e.getX();
			yOrigin = (int) e.getY();
			
			for (GameButton b : game.getButtons()) {
				if (b.getRect().intersects(x, y, x, y)) {
					// Releases all the buttons so that only one can be grabbed at the time
					for(GameButton bb : game.getButtons()) {
						bb.released();
					}
					b.grabbed();
				}
			}
		}

		if (e.getAction() == MotionEvent.ACTION_MOVE) {
			for (GameButton b : game.getLetterButtons()) {
				if (b.isGrabbed() && b.containsLetter()) {
					b.setXY(x, y);
					// If the button does not intersect its original rect, it has been outside
					// This prevents the button to be "clicked" when dragged back to its original location
					if(!b.getRect().intersect(b.getOriginRect())) {
						b.outside();
					}
				}
			}
		}

		return true;
	}
	
	private boolean hasMoved() {
		int area = 20;
		if(xOrigin + area > x && x > xOrigin - area) {
			if(yOrigin + area > y && y > yOrigin - area) {
				return false;
			}
		}
		return true;
	}
}
