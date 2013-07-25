package com.gamesourcecode.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.gamesourcecode.R;
import com.gamesourcecode.button.Button;
import com.gamesourcecode.game.GameActivity;
import com.gamesourcecode.home.input.OnTouchHandler;

public class Home extends SurfaceView implements Runnable {

	private SurfaceHolder holder;
	private Thread thread;
	private Canvas screen;
	private OnTouchHandler motion;
	
	private List<Button> buttons;

	private HomeActivity activity;

	private boolean running = false;

	public Home(Context context, HomeActivity activity) {
		super(context);
		this.activity = activity;
		holder = getHolder();
		
		motion = new OnTouchHandler(this);
		setOnTouchListener(motion);
		
		initButtons();
		
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
		
		screen.drawRGB(255, 255, 255);
		
		for(Button b : buttons) {
			b.render(screen, null);
		}

		holder.unlockCanvasAndPost(screen);
	}

	public boolean onTouch(View v, MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			startGame();
		}

		return false;
	}
	
	private void initButtons() {
		buttons = new ArrayList<Button>();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.startgame);
		
		int width = (int)(activity.getWidth() * 0.8);
		int height = width * bitmap.getHeight() / bitmap.getWidth();
		
		Button start = new Button((activity.getWidth() - width) / 2, (activity.getHeight() - height) / 2, width, height, bitmap) {
			public void onClick() {
				startGame();
			}
		};
		
		buttons.add(start);
	}

	private void startGame() {

		Intent intent = new Intent(getContext(), GameActivity.class);

		activity.startActivity(intent);
		activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		running = false;
	}
	
	public List<Button> getButtons() {
		return buttons;
	}
}
