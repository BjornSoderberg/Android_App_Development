package com.gamesourcecode.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gamesourcecode.R;
import com.gamesourcecode.button.Button;
import com.gamesourcecode.button.game.GameButton;
import com.gamesourcecode.button.game.LetterButton;
import com.gamesourcecode.button.game.WordButton;
import com.gamesourcecode.game.gfx.Image;
import com.gamesourcecode.game.input.OnTouchHandler;

public class Game extends SurfaceView implements Runnable {

	private Thread thread;
	private SurfaceHolder holder;
	private Canvas screen;
	private final Random random = new Random();

	private Image image;
	private OnTouchHandler motion;
	private GameActivity activity;

	private int letterButtonCols = 6, letterButtonRows = 2;
	private int wordButtonSize;

	private double currentTime = 0, totalTime = 20;

	private String word;

	private List<WordButton> wordButtons;
	private List<LetterButton> letterButtons;

	private boolean running = false;
	private boolean guessedRight = false;

	// Just for testing
	int color = 255;

	public Game(Context context, GameActivity activity, Bitmap[] bitmaps, String word) {
		super(context);
		this.activity = activity;
		this.word = word;

		holder = getHolder();

		if (word.length() < 8) wordButtonSize = activity.getWidth() / 8;
		else wordButtonSize = activity.getWidth() / (word.length() + 1);

		if (word.length() > letterButtonCols * letterButtonRows) {
			letterButtonCols = (word.length() + 1) / letterButtonRows;
		}

		image = new Image(this, bitmaps);
		motion = new OnTouchHandler(this);
		setOnTouchListener(motion);

		initButtons();
		start();
	}

	public synchronized void start() {
		running = true;

		thread = new Thread(this, "Game");
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
				currentTime = (SystemClock.uptimeMillis() - startTime) / 1000;
			}
		}

		stop();
	}

	private void tick() {

		for (Button b : getButtons()) {
			b.tick();
		}

		if (currentTime < totalTime) image.tick();
		else if (!guessedRight) color = 0;

		String string = "";
		for (GameButton b : wordButtons) {
			if (!b.containsLetter()) return;
			string += b.getChar();
		}

		// Makes the word ignore every space
		String word = "";
		for (int i = 0; i < this.word.length(); i++) {
			if (this.word.charAt(i) != ' ') word += this.word.charAt(i);
		}

		if (string.equalsIgnoreCase(word)) {
			guessedRight = true;
		}
	}

	private void render() {
		if (!holder.getSurface().isValid()) return;

		screen = holder.lockCanvas();
		screen.drawRGB(color, color, color);

		image.render(screen);

		Paint alpha = new Paint();
		alpha.setAlpha(180);
		
		// Renders the grabbed button last (so that it will be over the other buttons)
		for (GameButton b : getButtons()) {
			if (!b.isGrabbed() || !b.containsLetter()) b.render(screen, null);

		}

		for (GameButton b : getButtons()) {
			if (b.isGrabbed() && b.containsLetter()) b.render(screen, alpha);
		}

		holder.unlockCanvasAndPost(screen);
	}

	private void initButtons() {
		letterButtons = new ArrayList<LetterButton>();

		int width = activity.getWidth() / letterButtonCols;
		int height = width;
		int xOffset = 0, yOffset = 0;

		char[] chars = new char[letterButtonCols * letterButtonRows];
		chars = generateLetterButtonChars(chars);

		for (int y = 0; y < letterButtonRows; y++) {
			yOffset = activity.getHeight() - height * (y + letterButtonRows - 1);
			for (int x = 0; x < letterButtonCols; x++) {
				xOffset = x * width;
				char c = chars[x + y * letterButtonCols];
				Bitmap bitmap = getAlphabetBitmap(c, width, height);
				letterButtons.add(new LetterButton(xOffset, yOffset, width, height, this, bitmap, c));

			}
		}

		wordButtons = new ArrayList<WordButton>();
		xOffset = yOffset = 0;

		// yOffset = ((activity.getHeight() - letterButtonRows * height) +
		// (image.getY() + image.getHeight())) / 2;
		yOffset = (int) (activity.getHeight() * .62);

		width = wordButtonSize;
		height = wordButtonSize;

		xOffset = (activity.getWidth() - word.length() * width) / 2;

		for (int i = 0; i < word.length(); i++) {
			Bitmap bitmap = getAlphabetBitmap(' ', width, height);
			if (word.charAt(i) != ' ') wordButtons.add(new WordButton(xOffset, yOffset, width, height, this, bitmap));
			xOffset += width;
		}

	}

	public Bitmap getAlphabetBitmap(char c, int width, int height) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty);

		if (c == 'a') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a);
		if (c == 'b') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b);
		if (c == 'c') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.c);
		if (c == 'd') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.d);
		if (c == 'e') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.e);
		if (c == 'f') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.f);
		if (c == 'g') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.g);
		if (c == 'h') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.h);
		if (c == 'i') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.i);
		if (c == 'j') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.j);
		if (c == 'k') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.k);
		if (c == 'l') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.l);
		if (c == 'm') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.m);
		if (c == 'n') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.n);
		if (c == 'o') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o);
		if (c == 'p') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p);
		if (c == 'q') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.q);
		if (c == 'r') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.r);
		if (c == 's') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.s);
		if (c == 't') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.t);
		if (c == 'u') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.u);
		if (c == 'v') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.v);
		if (c == 'w') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w);
		if (c == 'x') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x);
		if (c == 'y') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.y);
		if (c == 'z') bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.z);

		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
		return bitmap;
	}

	private char[] generateLetterButtonChars(char[] chars) {

		for (int i = 0; i < chars.length; i++) {
			chars[i] = ' ';
			if (i < word.length() && word.charAt(i) != ' ') {
				chars[i] = word.charAt(i);
			} else {
				int rand = random.nextInt(26);
				if (rand == 0) chars[i] = 'a';
				if (rand == 1) chars[i] = 'b';
				if (rand == 2) chars[i] = 'c';
				if (rand == 3) chars[i] = 'd';
				if (rand == 4) chars[i] = 'e';
				if (rand == 5) chars[i] = 'f';
				if (rand == 6) chars[i] = 'g';
				if (rand == 7) chars[i] = 'h';
				if (rand == 8) chars[i] = 'i';
				if (rand == 9) chars[i] = 'j';
				if (rand == 10) chars[i] = 'k';
				if (rand == 11) chars[i] = 'l';
				if (rand == 12) chars[i] = 'm';
				if (rand == 13) chars[i] = 'n';
				if (rand == 14) chars[i] = 'o';
				if (rand == 15) chars[i] = 'p';
				if (rand == 16) chars[i] = 'q';
				if (rand == 17) chars[i] = 'r';
				if (rand == 18) chars[i] = 's';
				if (rand == 19) chars[i] = 't';
				if (rand == 20) chars[i] = 'u';
				if (rand == 21) chars[i] = 'v';
				if (rand == 22) chars[i] = 'w';
				if (rand == 23) chars[i] = 'x';
				if (rand == 24) chars[i] = 'y';
				if (rand == 25) chars[i] = 'z';
			}
		}

		// Randomizes the order of the array
		// Returns the temp array
		
		return scrambleCharArray(chars);
		
	}
	
	private char[] scrambleCharArray(char[] chars) {
		
		char[] temp = new char[chars.length];
		boolean[] bool = new boolean[chars.length];
		for (int i = 0; i < bool.length; i++) {
			bool[i] = true;
		}
		for (int i = 0; i < chars.length; i++) {
			while (true) {

				int rand = random.nextInt(chars.length);
				if (bool[rand]) {
					temp[i] = chars[rand];
					bool[rand] = false;
					break;
				}
			}
		}

		return temp;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public double getTime() {
		return currentTime;
	}

	public GameButton[] getButtons() {
		GameButton[] b = new GameButton[wordButtons.size() + letterButtons.size()];
		int i = 0;
		for (GameButton bb : wordButtons) {
			b[i] = bb;
			i++;
		}

		for (GameButton bb : letterButtons) {
			b[i] = bb;
			i++;
		}

		return b;
	}

	public Bitmap getEmptyBitmap(int width, int height) {
		return getAlphabetBitmap(' ', width, height);
	}

	public List<WordButton> getWordButtons() {
		return wordButtons;
	}

	public List<LetterButton> getLetterButtons() {
		return letterButtons;
	}

	public boolean hasGuessedRight() {
		return guessedRight;
	}
}
