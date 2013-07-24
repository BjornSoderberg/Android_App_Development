package com.gamesourcecode.game.button;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gamesourcecode.R;
import com.gamesourcecode.game.Game;

public class LetterButton extends Button {

	public LetterButton(int x, int y, int width, int height, Game game, char c) {
		super(x, y, width, height, game);
		this.c = c;
		bitmap = game.getAlphabetBitmap(c, width, height);
		containsLetter = true;
	}

	public void onClick() {
		if(containsLetter) {
			for(WordButton b : game.getWordButtons()) {
				if(!b.containsLetter()) {
					b.setLetter(c);
					containsLetter = false;
					setOriginalBitmap(width, height);
					break;
				}
			}
		}
	}
	
	public void onClick(Button b) {
		if(containsLetter && !b.containsLetter()) {
			b.setLetter(c);
			containsLetter = false;
			setOriginalBitmap(width, height);
		}
	}
	
	public void tick() {
//		if(hasMoved() && !grabbed && containsLetter) {
//			double angle = Math.atan2(y - yOriginal, x - xOriginal);
//			angle -= Math.toRadians(180);
//			x += (int) (Math.cos(angle) * 50);
//			y += (int) (Math.sin(angle) * 50);
//		}
	}
	
	private boolean hasMoved() {
		int area = 50;
		if(xOriginal + area > x && x > xOriginal - area) {
			if(yOriginal + area > y && y > yOriginal - area) {
				x = xOriginal;
				y = yOriginal;
				return false;
			}
		}
		return true;
	}

}
