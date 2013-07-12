package com.gamesourcecode.button;

import com.gamesourcecode.Game;

public class Button {

	protected Game game;
	protected int x, y, width, height;
	
	

	public Button(int x, int y, int width, int height, Game game) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.game = game;
	}
	
	public void onClick() {
		
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
