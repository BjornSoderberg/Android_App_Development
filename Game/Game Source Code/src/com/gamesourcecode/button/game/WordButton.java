package com.gamesourcecode.button.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gamesourcecode.R;
import com.gamesourcecode.button.Button;
import com.gamesourcecode.game.Game;

public class WordButton extends GameButton {

	public WordButton(int x, int y, int width, int height, Game game, Bitmap bitmap) {
		super(x, y, width, height, game, bitmap);
		containsLetter = false;
	}
	
	public void onClick() {
		if(containsLetter) {
			for(LetterButton b : game.getLetterButtons()) {
				if(!b.containsLetter() && b.getChar() == c) {
					b.setChar(c);
					containsLetter = false;
					bitmap = originalBitmap;
					break;
				}
			}
		}
	}
	
	public void reset() {
		super.reset();
		containsLetter = false;
	}
}
