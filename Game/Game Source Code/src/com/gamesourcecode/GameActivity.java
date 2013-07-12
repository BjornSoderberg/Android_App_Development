package com.gamesourcecode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

public class GameActivity extends Activity {

	private Bitmap image;
	private Game game;
	private boolean running = false;
	private Thread thread;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = new Game(this);
        setContentView(game);
        
        System.out.println("Started");
    }
    
}
