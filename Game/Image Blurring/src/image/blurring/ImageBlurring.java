package image.blurring;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ImageBlurring extends Canvas {

	private JFrame frame;

	private int radius = 1;
	private int iterations = 0;
	private int index = 0;
	
	private boolean running = false;

	private BufferedImage image, tempImg;
	private int width = 0, height = 0, divide = 0, desiredWidth = 0, desiredHeight = 0;

	private String fileName;
	private String path;

	private double[] red, green, blue, r, g, b;
	private int[] pixels;

	public ImageBlurring() {
		initFrame();

		try {
			path = "C:\\Users\\user\\Desktop\\images";
			File folder = new File(path);

			for (File fileEntry : folder.listFiles()) {
				fileName = fileEntry.getName();
				if (getExtension(fileName).equals(".png")) {
					System.out.println("Loaded");
					tempImg = ImageIO.read(fileEntry);
					width = tempImg.getWidth();
					height = tempImg.getHeight();

					getDesiredScales();

					tempImg = getScaledImage(tempImg, desiredWidth, desiredHeight);

					width = desiredWidth;
					height = desiredHeight;

					start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	private void getDesiredScales() {
		desiredWidth = (int) ((width < height) ? 400 : 400 * (width * 1.0) / (height * 1.0));
		desiredHeight = (int) ((width > height) ? 400 : 400 * (height * 1.0) / (width * 1.0));
	}

	private void start() {
		initArrays();
		iterations = index = 0;
		
		running = true;

		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));

		frame.pack();

		run();
	}

	private void run() {
		render();
		while (running) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			render();

			System.out.println(iterations);

			iterations++;

			if (iterations == 1 || iterations == 2 || iterations == 4 || iterations == 7 || iterations == 12 || iterations == 20 || iterations == 30 || iterations == 40 || iterations == 50 || iterations == 65 || iterations == 80 || iterations == 100 || iterations == 140 || iterations == 180 || iterations == 250 || iterations == 350 || iterations == 500 || iterations == 700 || iterations == 900 || iterations == 1100 || iterations == 1400 || iterations == 1900 || iterations == 2500 || iterations == 3000) {
				System.out.println("Saving image\t" + iterations);
				save();
			}
			
			if(iterations == 3000) {
				running = false;
			}

			blurImage();
		}
	}

	private void initArrays() {
		int[] temp = tempImg.getRGB(0, 0, tempImg.getWidth(), tempImg.getHeight(), null, 0, tempImg.getWidth());

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		red = new double[width * height];
		green = new double[width * height];
		blue = new double[width * height];

		r = new double[width * height];
		g = new double[width * height];
		b = new double[width * height];

		for (int i = 0; i < pixels.length; i++) {
			red[i] = (temp[i] >> 16) & 0xff;
			green[i] = (temp[i] >> 8) & 0xff;
			blue[i] = (temp[i] >> 0) & 0xff;
			pixels[i] = temp[i];
		}
	}

	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		g.setColor(Color.pink);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, null);

		g.dispose();

		bs.show();
	}

	private void blurImage() {
		double[] matrixR = new double[(radius * 2 + 1) * (radius * 2 + 1)];
		double[] matrixG = new double[(radius * 2 + 1) * (radius * 2 + 1)];
		double[] matrixB = new double[(radius * 2 + 1) * (radius * 2 + 1)];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				matrixR = getMatrix(red, x, y, radius);
				matrixG = getMatrix(green, x, y, radius);
				matrixB = getMatrix(blue, x, y, radius);

				double rr = 0, gg = 0, bb = 0;
				rr = averageColor(matrixR);
				gg = averageColor(matrixG);
				bb = averageColor(matrixB);

				r[x + y * width] = rr;
				g[x + y * width] = gg;
				b[x + y * width] = bb;
			}
		}

		for (int i = 0; i < pixels.length; i++) {
			red[i] = r[i];
			green[i] = g[i];
			blue[i] = b[i];

			pixels[i] = (int) (r[i]) * 256 * 256 + (int) (g[i]) * 256 + (int) b[i];
		}

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] += 0xff000000;
		}
	}

	private double[] getMatrix(double[] pixels, int x, int y, int radius) {
		double[] matrix = new double[(2 * radius + 1) * (2 * radius + 1)];
		int k = 0;
		divide = 0;

		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if ((x + j) + (y + i) * width >= 0 && (x + j) + (y + i) * width < pixels.length) {
					matrix[k] = pixels[(x + j) + (y + i) * width];
					divide++;
				}
				k++;
			}
		}

		return matrix;
	}

	private double averageColor(double[] matrix) {
		double color = 0;

		for (int i = 0; i < matrix.length; i++) {
			color += matrix[i];
		}

		return color / divide;
	}

	private void initFrame() {
		setPreferredSize(new Dimension(600, 600));
		setMaximumSize(new Dimension(600, 600));
		setMinimumSize(new Dimension(600, 600));

		frame = new JFrame();
		frame.setResizable(true);
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private String getExtension(String string) {
		String sub = "";

		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '.') {
				sub = string.substring(i);
			}
		}

		return sub;
	}

	private void save() {

		String ext = "jpg";
		File file = new File("C:\\xampp\\htdocs\\android\\images\\" + removeExtension(fileName) + index + "." + ext);
		try {
			ImageIO.write(image, ext, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		index++;
	}

	private String removeExtension(String string) {
		String sub = "";

		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '.') {
				sub = string.substring(0, i);
			}
		}

		return sub;
	}

	public static void main(String[] args) {
		new ImageBlurring();
	}
}
