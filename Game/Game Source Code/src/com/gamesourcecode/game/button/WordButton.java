package com.gamesourcecode.game.button;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.gamesourcecode.R;
import com.gamesourcecode.game.Game;

public class WordButton extends Button {

	public WordButton(int x, int y, int width, int height, Game game) {
		super(x, y, width, height, game);
		containsLetter = false;
	}
	
	public void onClick() {
		if(containsLetter) {
			for(LetterButton b : game.getLetterButtons()) {
				Log.i("Char", b.getChar() + "");
				if(!b.containsLetter() && b.getChar() == c) {
					b.setLetter(c);
					containsLetter = false;
					setOriginalBitmap(width, height);
					break;
				}
			}
		}
	}
}
