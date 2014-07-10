package com.example.minesweeperv2;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Field extends Activity {
	// Visual part
	private Context currentContext;
	private LinearLayout motherLayout;
	private LinearLayout[] visualMatrixHolders;
	private Button[][] visualButtonMatrix;

	// Emoji
	private LinearLayout holderEmoji;
	private ImageView emoji;

	// Logic
	private int height = 9;
	private int width = 5;
	private int[][] logicalMatrix;
	private int bombs = 0;
	private boolean shouldGameEnd = false;

	// Constructors

	// public Field(LinearLayout coreLayout) {
	// this.currentContext = getApplicationContext();
	// this.visualButtonMatrix = new Button[this.height][this.width];
	// this.visualMatrixHolders = new LinearLayout[this.height];
	// this.motherLayout = coreLayout;
	// visualizeButtons();
	// this.logicalMatrix = new int[this.height][this.width];
	// }
	//
	// public Field(Context currentContext, LinearLayout coreLayout) {
	// this.currentContext = currentContext;
	// this.visualButtonMatrix = new Button[this.height][this.width];
	// this.visualMatrixHolders = new LinearLayout[this.height];
	// this.motherLayout = coreLayout;
	// visualizeButtons();
	// this.logicalMatrix = new int[this.height][this.width];
	// }
	//
	// public Field(LinearLayout coreLayout, int height, int width) {
	// this.currentContext = getApplicationContext();
	// this.height = height;
	// this.width = width;
	// this.visualButtonMatrix = new Button[this.height][this.width];
	// this.visualMatrixHolders = new LinearLayout[this.height];
	// this.motherLayout = coreLayout;
	// visualizeButtons();
	// this.logicalMatrix = new int[this.height][this.width];
	// }

	// use this one
	public Field(Context currentContext, LinearLayout coreLayout, int height,
			int width, int numberOfBombs, LinearLayout emojiHolder,
			ImageView emoji) {
		this.currentContext = currentContext;
		this.height = height;
		this.width = width;
		this.bombs = numberOfBombs;
		this.logicalMatrix = new int[this.height][this.width];
		this.visualButtonMatrix = new Button[this.height][this.width];
		this.visualMatrixHolders = new LinearLayout[this.height];
		this.motherLayout = coreLayout;

		this.holderEmoji = emojiHolder;
		this.emoji = emoji;
	}

	// Get methods
	public Context getCurrentContext() {
		return this.currentContext;
	}

	public Button[][] getVisualButtonMatrix() {
		return this.visualButtonMatrix;
	}

	public LinearLayout[] getVisualMatrixHolders() {
		return this.visualMatrixHolders;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getSpecificCellValue(int row, int col) {
		return this.logicalMatrix[row][col];
	}

	public boolean shouldGameEnd() {
		return this.shouldGameEnd;
	}

	// Set methods
	public void setContext(Context currentContext) {
		this.currentContext = currentContext;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setSpecificCellValue(int row, int col, int value) {
		this.logicalMatrix[row][col] = value;
	}

	// Logical methods

	public void generateBombs(int count) {
		for (int i = 0; i < count; i++) {
			generateRandomIntUpdated(i);
		}
	}
	
	@Deprecated
	private void generateRandomInt() {
		Random rand = new Random();
		int row = rand.nextInt(this.height);
		int col = rand.nextInt(this.width);

		while (this.logicalMatrix[row][col] == -1) {
			if (col % 2 == 0) {
				row = rand.nextInt(this.height);
			} else {
				col = rand.nextInt(this.width);
			}
		}
		this.logicalMatrix[row][col] = -1;
	}

	private void generateRandomIntUpdated(int col) {
		int notMoreThan = Math.max(this.width, this.bombs)/Math.min(this.width, this.bombs);
		Random rand = new Random();
		int max = rand.nextInt(this.height);
		if (max >= height)
			max = height / 2 - 1;
		if (max == 0 | max == 1 & max < this.height)
			max++;

		for (int i = 0; i < max && i < notMoreThan; i++) {
			int row = rand.nextInt(this.height);

			while (this.logicalMatrix[row][col] == -1) {
				row = rand.nextInt(this.height);
			}
			this.logicalMatrix[row][col] = -1;
		}
	}

	private void fillLogicMatrix() {
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				if (logicalMatrix[row][col] == -1)
					continue;
				logicalMatrix[row][col] = countBombs(row, col);
			}
		}
	}

	private int countBombs(int row, int col) {
		int counter = 0;
		// the +
		if (row > 0 && this.logicalMatrix[row - 1][col] == -1)
			counter++;
		if (row < this.height - 1 && this.logicalMatrix[row + 1][col] == -1)
			counter++;
		if (col > 0 && this.logicalMatrix[row][col - 1] == -1)
			counter++;
		if (col < this.width - 1 && this.logicalMatrix[row][col + 1] == -1)
			counter++;
		// diagonals
		if (row > 0 && col > 0 && this.logicalMatrix[row - 1][col - 1] == -1)
			counter++;
		if (row < this.height - 1 && col > 0
				&& this.logicalMatrix[row + 1][col - 1] == -1)
			counter++;
		if (col < this.width - 1 && row > 0
				&& this.logicalMatrix[row - 1][col + 1] == -1)
			counter++;
		if (col < this.width - 1 && row < this.height - 1
				&& this.logicalMatrix[row + 1][col + 1] == -1)
			counter++;

		return counter;
	}

	// Visual methods

	@SuppressLint("NewApi") private void visualizeButtons() {
		DisplayMetrics metrics = getCurrentContext().getResources()
				.getDisplayMetrics();
		final int width = metrics.widthPixels;
		final int height = metrics.heightPixels;
				
		Toast.makeText(this.currentContext, width + " " + height,
				Toast.LENGTH_LONG).show();
		for (int row = 0; row < this.height; row++) {
			this.visualMatrixHolders[row] = new LinearLayout(
					this.currentContext);
			this.visualMatrixHolders[row]
					.setOrientation(LinearLayout.HORIZONTAL);
			for (int col = 0; col < this.width; col++) {
				this.visualButtonMatrix[row][col] = new Button(
						this.currentContext);
				this.visualButtonMatrix[row][col]
						.setLayoutParams(new LayoutParams((int) (Math
								.floor(200.0 / 1080.0 * width) - 2),
								(int) (Math.floor(140.0 / 1920.0 * height))));
				// this.visualButtonMatrix[row][col].setText(""+row+" "+col);
				this.visualMatrixHolders[row]
						.addView(this.visualButtonMatrix[row][col]);
			}
			this.motherLayout.addView(this.visualMatrixHolders[row]);
		}
	}

	private void setEmoji() {
		this.emoji.setImageResource(R.drawable.alivesmiley);
		this.emoji.setLayoutParams(new LayoutParams(200, 140));
		this.holderEmoji.addView(emoji);
	}

	private void changeEmojiPicture() {
		this.emoji.setImageResource(R.drawable.radioactivesmiley);
	}

	private void setOnClickListeners() {
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {

				final int row1 = row;
				final int col1 = col;
				final int val = this.logicalMatrix[row1][col1];

				this.visualButtonMatrix[row][col]
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								visualButtonMatrix[row1][col1]
										.setEnabled(false);
								if (logicalMatrix[row1][col1] == -1) {
									shouldGameEnd = true;
									if (shouldGameEnd == true) {
										changeEmojiPicture();
									}
									visualButtonMatrix[row1][col1]
											.setBackgroundResource(R.drawable.bomb);
								} else {
									visualButtonMatrix[row1][col1].setText(""
											+ val);
									visualButtonMatrix[row1][col1]
											.setBackgroundColor(Color.TRANSPARENT);
									visualButtonMatrix[row1][col1]
											.setTextColor(Color.MAGENTA);
								}
							}
						});
			}
		}
	}

	private void setOnLongClickListeners() {
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				final int row1 = row;
				final int col1 = col;
				this.visualButtonMatrix[row][col]
						.setOnLongClickListener(new View.OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {

								visualButtonMatrix[row1][col1]
										.setBackgroundResource(R.drawable.flag);
								// visualButtonMatrix[row1][col1].setBackgroundColor(Color.TRANSPARENT);
								visualButtonMatrix[row1][col1]
										.setEnabled(false);
								return false;
							}
						});
			}
		}
	}

	// Engine methods

	public void engine() {
		visualizeButtons();
		generateBombs(this.width);
		fillLogicMatrix();
		setOnClickListeners();
		setOnLongClickListeners();
		setEmoji();
	}
}