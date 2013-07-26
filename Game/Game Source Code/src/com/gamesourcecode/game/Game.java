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

	private int letterButtonCols = 7, letterButtonRows = 2;
	private int wordButtonSize;
	private int paddingX, paddingY;
	private final int WIDTH, HEIGHT;

	private double WHRatio = 1.618;
	private double currentTime = 0, totalTime = 20;

	private char[] chars;

	private String word;

	private List<WordButton> wordButtons;
	private List<LetterButton> letterButtons;
	private List<GameButton> buttons;
	private List<GameButton> miscButtons;

	private boolean running = false;
	private boolean guessedRight = false;

	// Just for testing
	int color = 0x44;

	public Game(Context context, GameActivity activity, Bitmap[] bitmaps, String word) {
		super(context);
		this.activity = activity;
		this.word = word;

		holder = getHolder();

		if (activity.getWidth() * WHRatio > activity.getHeight()) {
			HEIGHT = (int) (activity.getHeight() * 0.9);
			WIDTH = (int) (HEIGHT / WHRatio);
		} else {
			WIDTH = (int) (activity.getWidth() * 0.9);
			HEIGHT = (int) (WIDTH * WHRatio);
		}

		paddingX = (activity.getWidth() - WIDTH) / 2;
		paddingY = (activity.getHeight() - HEIGHT) / 2;

		if (word.length() < 8) wordButtonSize = WIDTH / 8;
		else wordButtonSize = WIDTH / (word.length() + 1);

		// +2 is to add space for the clear and scramble buttons
		// +3 is +2 and to prevent letterButtonCols be to short since it's an
		// int
		// if word.length() = 9 ==> letterButtonCols = 9 / 2 = 4.5 = 4 (when
		// casting to an int)
		// cols * rows = 2 * 4 = 8 > word.length()
		if (word.length() > letterButtonCols * letterButtonRows + 2) {
			letterButtonCols = (word.length() + 3) / letterButtonRows;
		}

		image = new Image(this, bitmaps, WIDTH);
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

		for (GameButton b : buttons) {
			b.tick();
		}

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

		// Renders the grabbed button last (so that it will be over the other
		// buttons)
		for (GameButton b : getButtons()) {
			if (!b.isGrabbed()) b.render(screen, null);

		}

		for (GameButton b : getButtons()) {
			if (b.isGrabbed()) b.render(screen, alpha);
		}

		holder.unlockCanvasAndPost(screen);
	}

	private void initButtons() {
		buttons = new ArrayList<GameButton>();
		miscButtons = new ArrayList<GameButton>();
		letterButtons = new ArrayList<LetterButton>();
		wordButtons = new ArrayList<WordButton>();

		int width = WIDTH / letterButtonCols;
		int height = width;
		int xOffset = paddingX, yOffset = paddingY;

		// -2 to not make chars for the scramble and clear buttons
		chars = new char[letterButtonCols * letterButtonRows - 2];
		chars = generateLetterButtonChars(chars);
		int charIndex = 0;

		for (int y = 0; y < letterButtonRows; y++) {
			yOffset = (HEIGHT + paddingY) - height * (letterButtonRows - y);
			for (int x = 0; x < letterButtonCols; x++) {
				xOffset = x * width + paddingX;
				// if x is the last button of the col
				// This makes the scramble and clear buttons
				if (x == letterButtonCols - 1) {
					if (y == 0) {
						Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scramble);
						bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
						miscButtons.add(new GameButton(xOffset, yOffset, width, height, this, bitmap) {
							// Makes the chars of the letter buttons which contain a char randomly change
							// The empty letter buttons will not change
							public void onClick() {
								int index = 0;
								for (LetterButton l : letterButtons) {
									if (l.containsLetter()) {
										index++;
									}
								}
								char[] chars = new char[index]; 
								index = 0;
								for (int i = 0; i < letterButtons.size(); i++) {
									if (letterButtons.get(i).containsLetter()) {
										chars[index] = letterButtons.get(i).getChar();
										index++;
									}
								}
								chars = scrambleCharArray(chars);
								index = 0;
								for (int i = 0; i < letterButtons.size(); i++) {
									if (letterButtons.get(i).containsLetter()) {
										letterButtons.get(i).scramble(chars[index]);
										index++;
									}
								}
							}
						});
					} else if (y == 1) {
						Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clear);
						bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
						miscButtons.add(new GameButton(xOffset, yOffset, width, height, this, bitmap) {
							public void onClick() {
								for (GameButton b : game.getButtons()) {
									b.reset();
								}
							}
						});
					} else {
						Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
						miscButtons.add(new GameButton(xOffset, yOffset, width, height, this, bitmap));
					}
				} else {
					// charIndex makes it easier to chose the correct char from
					// the char array
					// The scramble and clear buttons make it impossible to use
					// x + y * letterButtonCols
					char c = chars[charIndex];
					Bitmap bitmap = getAlphabetBitmap(c, width, height);
					letterButtons.add(new LetterButton(xOffset, yOffset, width, height, this, bitmap, c));
					charIndex++;
				}
			}
		}

		width = wordButtonSize;
		height = wordButtonSize;

		// Will make the word buttons appear 1x the height of a letter button,
		// over the topmost letter button row
		yOffset = letterButtons.get(0).getY() - letterButtons.get(0).getHeight() - wordButtonSize;
		xOffset = (WIDTH - word.length() * width) / 2 + paddingX;

		for (int i = 0; i < word.length(); i++) {
			Bitmap bitmap = getAlphabetBitmap(' ', width, height);
			if (word.charAt(i) != ' ') wordButtons.add(new WordButton(xOffset, yOffset, width, height, this, bitmap));
			xOffset += width;
		}

		buttons.addAll(letterButtons);
		buttons.addAll(wordButtons);
		buttons.addAll(miscButtons);

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

	public List<GameButton> getButtons() {
		return buttons;
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

	public double getTotalTime() {
		return totalTime;
	}

	public double getTime() {
		return currentTime;
	}

	public int getPaddingX() {
		return paddingX;
	}

	public int getPaddingY() {
		return paddingY;
	}

	public char[] getCharsArray() {
		return null;
	}
}
