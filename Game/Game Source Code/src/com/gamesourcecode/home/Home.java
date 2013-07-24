package com.gamesourcecode.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gamesourcecode.game.GameActivity;

public class Home extends SurfaceView implements Runnable, OnTouchListener {

	private SurfaceHolder holder;
	private Thread thread;
	private Canvas screen;

	private HomeActivity activity;

	private boolean running = false;

	public Home(Context context, HomeActivity activity) {
		super(context);
		this.activity = activity;
		holder = getHolder();

		setOnTouchListener(this);
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
		double startTime = SystemClock.uptimeMillis();
		long lastTime = SystemClock.uptimeMillis();
		double millisPerTick = 1000D / 60D;
		double delta = 0;

		while (running) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			delta += (SystemClock.uptimeMillis() - lastTime) / millisPerTick;
			lastTime = SystemClock.uptimeMillis();

			while (delta >= 1) {
				tick();
				render();
				delta--;
			}
		}

		stop();
	}

	private void tick() {

	}

	private void render() {
		if (!holder.getSurface().isValid()) return;

		screen = holder.lockCanvas();
		screen.drawRGB(200, 200, 100);

		holder.unlockCanvasAndPost(screen);
	}

	public boolean onTouch(View v, MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			startGame();
		}

		return false;
	}

	private void startGame() {

		Intent intent = new Intent(getContext(), GameActivity.class);

		activity.startActivity(intent);
		activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		running = false;
	}
}
