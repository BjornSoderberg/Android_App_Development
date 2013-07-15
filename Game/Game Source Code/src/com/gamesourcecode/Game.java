package com.gamesourcecode;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gamesourcecode.button.Button;
import com.gamesourcecode.button.LetterButton;
import com.gamesourcecode.button.WordButton;
import com.gamesourcecode.gfx.Image;
import com.gamesourcecode.input.OnTouchHandler;

public class Game extends SurfaceView implements Runnable {

	private Bitmap bitmap;
	private Bitmap alphabet;
	private Thread thread;
	private SurfaceHolder holder;
	private Canvas screen;

	private Image image;
	private OnTouchHandler motion;
	private GameActivity activity;

	private int letterButtonCols = 6, letterButtonRows = 2;
	private int wordButtonSize;
	
	// Displayed in nanoseconds
	private double currentTime = 0, totalTime = 10 * 1000 * 1000 * 1000;

	private String word = "runescape";

	private LetterButton[] letterButtons;
	private WordButton[] wordButtons;

	private boolean running = false;
	
	// Just for testing
	int color = 255;

	public Game(Context context, GameActivity activity) {
		super(context);
		this.activity = activity;
		holder = getHolder();

		wordButtonSize = activity.getWidth() / 9;

		// This is easier to see
		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.aaa0);
		bitmap = temp.copy(Bitmap.Config.ARGB_8888, true);

		alphabet = BitmapFactory.decodeResource(getResources(), R.drawable.alphabet);

		image = new Image(bitmap, this);
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
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;
		double delta = 0;

		requestFocus();

		while (running) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				System.out.println("Could not sleep!");
				e.printStackTrace();
			}

			delta += (System.nanoTime() - lastTime) / nsPerTick;
			lastTime = System.nanoTime();

			while (delta >= 1) {
				tick();
				render();
				delta--;
				currentTime += nsPerTick;
			}
		}

		stop();
	}

	private void tick() {

		
		image.tick();
		for(Button b : getButtons()) {
			b.tick();
		}
		

		
		color = 130;
		
		if(currentTime >= totalTime) color = 0;
		
		String string = "";
		for(Button b : wordButtons) {
			if(!b.containsLetter()) return;
			string += b.getChar();
		}
		
		
		if(string.equalsIgnoreCase(word)) color = 255;
	}

	private void render() {
		if (!holder.getSurface().isValid()) return;

		screen = holder.lockCanvas();
		screen.drawRGB(color, color, color);

		image.render(screen);

		for (int i = 0; i < letterButtons.length; i++) {
			letterButtons[i].render(screen);
		}

		for (int i = 0; i < wordButtons.length; i++) {
			wordButtons[i].render(screen);
		}

		holder.unlockCanvasAndPost(screen);
	}

	private void initButtons() {
		letterButtons = new LetterButton[letterButtonCols * letterButtonRows];

		int width = activity.getWidth() / letterButtonCols;
		int height = width;
		int xOffset = 0, yOffset = 0;

		char[] chars = new char[letterButtons.length];
		chars = generateLetterButtonChars(chars);

		for (int y = 0; y < letterButtonRows; y++) {
			yOffset = activity.getHeight() - height * (y + letterButtonRows - 1);
			for (int x = 0; x < letterButtonCols; x++) {
				xOffset = x * width;
				char c = chars[x + y * letterButtonCols];
				letterButtons[x + y * letterButtonCols] = new LetterButton(xOffset, yOffset, width, height, this, c);
			}
		}

		wordButtons = new WordButton[word.length()];
		xOffset = yOffset = 0;

		// yOffset = ((activity.getHeight() - letterButtonRows * height) +
		// (image.getY() + image.getHeight())) / 2;
		yOffset = (int) (activity.getHeight() * .62);

		width = wordButtonSize;
		height = wordButtonSize;

		xOffset = (activity.getWidth() - wordButtons.length * width) / 2;

		for (int i = 0; i < wordButtons.length; i++) {
			wordButtons[i] = new WordButton(xOffset, yOffset, width, height, this);
			xOffset += width;
		}

	}

	public Bitmap getAlphabetBitmap(char c, int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(alphabet, 84, 168, 42, 42);

		if (c == 'a') bitmap = Bitmap.createBitmap(alphabet, 0, 0, 42, 42);
		if (c == 'b') bitmap = Bitmap.createBitmap(alphabet, 42, 0, 42, 42);
		if (c == 'c') bitmap = Bitmap.createBitmap(alphabet, 84, 0, 42, 42);
		if (c == 'd') bitmap = Bitmap.createBitmap(alphabet, 126, 0, 42, 42);
		if (c == 'e') bitmap = Bitmap.createBitmap(alphabet, 168, 0, 42, 42);
		if (c == 'f') bitmap = Bitmap.createBitmap(alphabet, 210, 0, 42, 42);
		if (c == 'g') bitmap = Bitmap.createBitmap(alphabet, 0, 42, 42, 42);
		if (c == 'h') bitmap = Bitmap.createBitmap(alphabet, 42, 42, 42, 42);
		if (c == 'i') bitmap = Bitmap.createBitmap(alphabet, 84, 42, 42, 42);
		if (c == 'j') bitmap = Bitmap.createBitmap(alphabet, 126, 42, 42, 42);
		if (c == 'k') bitmap = Bitmap.createBitmap(alphabet, 168, 42, 42, 42);
		if (c == 'l') bitmap = Bitmap.createBitmap(alphabet, 210, 42, 42, 42);
		if (c == 'm') bitmap = Bitmap.createBitmap(alphabet, 0, 84, 42, 42);
		if (c == 'n') bitmap = Bitmap.createBitmap(alphabet, 42, 84, 42, 42);
		if (c == 'o') bitmap = Bitmap.createBitmap(alphabet, 84, 84, 42, 42);
		if (c == 'p') bitmap = Bitmap.createBitmap(alphabet, 126, 84, 42, 42);
		if (c == 'q') bitmap = Bitmap.createBitmap(alphabet, 168, 84, 42, 42);
		if (c == 'r') bitmap = Bitmap.createBitmap(alphabet, 210, 84, 42, 42);
		if (c == 's') bitmap = Bitmap.createBitmap(alphabet, 0, 126, 42, 42);
		if (c == 't') bitmap = Bitmap.createBitmap(alphabet, 42, 126, 42, 42);
		if (c == 'u') bitmap = Bitmap.createBitmap(alphabet, 84, 126, 42, 42);
		if (c == 'v') bitmap = Bitmap.createBitmap(alphabet, 126, 126, 42, 42);
		if (c == 'w') bitmap = Bitmap.createBitmap(alphabet, 168, 126, 42, 42);
		if (c == 'x') bitmap = Bitmap.createBitmap(alphabet, 210, 126, 42, 42);
		if (c == 'y') bitmap = Bitmap.createBitmap(alphabet, 0, 168, 42, 42);
		if (c == 'z') bitmap = Bitmap.createBitmap(alphabet, 42, 168, 42, 42);

		bitmap = Bitmap.createScaledBitmap(bitmap, width - 1, height - 1, true);
		return bitmap;
	}

	private char[] generateLetterButtonChars(char[] chars) {

		Random random = new Random();

		for (int i = 0; i < chars.length; i++) {
			chars[i] = '?';
			if (i < word.length()) {
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

	public Button[] getButtons() {
		Button[] b = new Button[wordButtons.length + letterButtons.length];
		int i = 0;
		for (Button bb : wordButtons) {
			b[i] = bb;
			i++;
		}

		for (Button bb : letterButtons) {
			b[i] = bb;
			i++;
		}

		return b;
	}

	public WordButton[] getWordButtons() {
		return wordButtons;
	}

	public LetterButton[] getLetterButtons() {
		return letterButtons;
	}
}
