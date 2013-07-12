package com.gamesourcecode;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gamesourcecode.gfx.Image;

public class Game extends SurfaceView implements Runnable {

	private Bitmap bitmap;
	private Thread thread;
	private SurfaceHolder holder;
	private Canvas screen;
	private Image image;

	private boolean running = false;

	public Game(Context context) {
		super(context);
		holder = getHolder();
		
		// This is easier to see
		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.random);
		bitmap = temp.copy(Bitmap.Config.ARGB_8888, true);
		
		image = new Image(bitmap, this);

		start();
	}

	public synchronized void start() {
		running = true;

		thread = new Thread(this, "Canvas");
		thread.start();
	}

	public synchronized void stop() {
		running = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			System.out.println("Could not close the game thread!");
			e.printStackTrace();
		}
	}

	public void run() {
		while (running) {
			// Restarts while loop
			if (!holder.getSurface().isValid()) continue;

			screen = holder.lockCanvas();

			screen.drawRGB(255, 255, 100);
			
			image.render(screen);
			image.tick();
			
			holder.unlockCanvasAndPost(screen);
		}
	}
	
	public double getTime() {
		return 45;
	}

}
