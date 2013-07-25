package com.gamesourcecode.button.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.gamesourcecode.R;
import com.gamesourcecode.button.Button;
import com.gamesourcecode.game.Game;

public class LetterButton extends GameButton {
	
	public LetterButton(int x, int y, int width, int height, Game game, Bitmap bitmap, char c) {
		super(x, y, width, height, game, bitmap);
		this.c = c;
		bitmap = game.getAlphabetBitmap(c, width, height);
		
		containsLetter = true;
	}

	public void render(Canvas screen, Paint paint) {
		// If the button is grabbed, an empty button is displayed on its original location
		Paint p = (getRect().intersect(getOriginRect())) ? paint : null;
		if (grabbed) screen.drawBitmap(game.getEmptyBitmap(width, height), xOrigin, yOrigin, p);
		
		super.render(screen, paint);
	}

	public void onClick() {
		if(containsLetter) {
			for(WordButton b : game.getWordButtons()) {
				if(!b.containsLetter()) {
					b.setChar(c);
					containsLetter = false;
					bitmap = game.getEmptyBitmap(width, height);
					break;
				}
			}
		}
	}
	
	public void onClick(GameButton b) {
		if(containsLetter && !b.containsLetter()) {
			b.setChar(c);
			containsLetter = false;
			bitmap = game.getEmptyBitmap(width, height);
		}
	}	
}
