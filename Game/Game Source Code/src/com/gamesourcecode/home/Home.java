package com.gamesourcecode.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.gamesourcecode.R;
import com.gamesourcecode.button.Button;
import com.gamesourcecode.game.GameActivity;

public class Home extends SurfaceView implements Runnable, Callback {
	private SurfaceHolder holder;
	private Thread thread;
	private Canvas screen;
	private OnTouchHandler touch;

	private List<Button> buttons;

	private HomeActivity activity;

	private boolean running = false;
	private boolean surfaceDestroyed = false;

	public Home(Context context, HomeActivity activity) {
		super(context);
		this.activity = activity;

		holder = getHolder();
		holder.addCallback(this);

		touch = new OnTouchHandler(this);

		initButtons();

		start();
	}

	public synchronized void start() {
		running = true;
		surfaceDestroyed = false;

		setOnTouchListener(touch);

		thread = new Thread(this, "Home");
		thread.start();
	}

	public synchronized void stop() {
		running = false;

		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}

		recycle();

		activity.finish();
	}

	public void run() {
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
		if (surfaceDestroyed) return;
		if (!holder.getSurface().isValid()) return;

		screen = holder.lockCanvas();
		if (screen == null) return;

		screen.drawRGB(0x66, 0x66, 0x66);

		Paint alpha = new Paint();
		alpha.setAlpha(180);

		if (buttons != null) {
			for (Button b : buttons) {
				if (b.isGrabbed() && screen != null) b.render(screen, alpha);
				else b.render(screen, null);
			}
		}
		if (screen != null) holder.unlockCanvasAndPost(screen);
	}

	private void initButtons() {
		buttons = new ArrayList<Button>();
		Bitmap startImage = BitmapFactory.decodeResource(getResources(), R.drawable.startgame);
		Bitmap logoutImage = BitmapFactory.decodeResource(getResources(), R.drawable.logout);

		int width = (int) (activity.getWidth() * 0.8);
		int height = width * startImage.getHeight() / startImage.getWidth();

		Button start = new Button((activity.getWidth() - width) / 2, (activity.getHeight() - height - (int) (height * 1.1)) / 2, width, height, startImage) {
			public void onClick() {
				startIntent(GameActivity.class);
			}
		};

		Button logout = new Button((activity.getWidth() - width) / 2, (activity.getHeight() - height + (int) (height * 1.1)) / 2, width, height, logoutImage) {
			public void onClick() {
				activity.getSession().logoutUser();
				activity.getSession().checkLogin();
			}
		};

		buttons.add(start);
		buttons.add(logout);
	}

	private void startIntent(Class<?> c) {
		render();

		Intent intent = new Intent(getContext(), c);

		activity.startActivity(intent);
		//activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

		running = false;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setRunning(boolean bool) {
		running = bool;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceDestroyed = true;
	}

	private void recycle() {
		if (buttons != null) {
			for (Button b : getButtons()) {
				b.recycleBitmaps();
				b = null;
			}
			buttons = null;
		}

		screen = null;
		thread = null;
	}

}
