package br.com.flp.square_game.game_engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Sprite {

	protected int x = 0;// Posi��o da Sprite
	protected int y = 0; // Posi��o da Sprite
	protected Bitmap mBitmap; // imagem
	private boolean visible; // Visivel

	public int width;// Largura de um frame (n�o do bitmap
	public int height; // altura de um frame (n�o do bitmap

	protected Paint mPaint; // Paint usado para desenho

	private Sprite() {
		this.mPaint = new Paint();		
	}

	public Sprite(int x, int y, int width, int height) {
		this();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Sprite(int x, int y, int width, int height, Resources res,
			int drawable) {

		this(x, y, width, height);

		if (mBitmap == null) {
			// gera a imagem que ser� destruida pelo usuario
			mBitmap = BitmapFactory.decodeResource(res, drawable);
			// redimensiona imagem
			mBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, true);
		}

	}

	/**
	 * Recebe como par�metros a imagem em Bitmap, a quantidade de linhas (de
	 * frames) e de colunas, define a largura e altura do frame, o ultimo frame,
	 * e se tiver apenas um frame define a anima��o como ANIM_STOP (parada).
	 * 
	 * Perceba que o par�metro da imagem � da classe Bitmap, ou seja, voc� tem
	 * que carregar o Bitmap no seu GameView e passar j� pronto. Caso a imagem
	 * venha de um recurso (ao inv�s de asset, ou arquivo) voc� pode carrega-lo
	 * assim:
	 * 
	 * <pre>
	 * Resources res = getResources();
	 * 
	 * Bitmap imagem = BitmapFactory.decodeResource(res, R.drawable.personagem);
	 * </pre>
	 */
	public Sprite(Bitmap bmp, int bmp_rows, int bmp_columns) {

		this.mPaint = new Paint();
		this.visible = true;
		this.mBitmap = bmp;

		this.width = bmp.getWidth() / bmp_columns;
		this.height = bmp.getHeight() / bmp_rows;

	}

	public Sprite(Bitmap bmp) {
		this(bmp, 1, 1);

	}

	/** Realiza o desenho na tela */
	public void onDraw(Canvas canvas) {

		if (canvas == null) {
			return;
		}

		if (isVisible()) {
			drawBitMap(canvas);
		} else {
			canvas.drawRect(getX(), getY(), getX() + getWidth(), getY()

			+ getHeight(), mPaint);
		}

	}

	/** Metodo para mandar meu objeto ser uma imagem */
	public void drawBitMap(Canvas canvas) {

		if (canvas != null && mBitmap != null) {
			// para desenhar a imagem
			canvas.drawBitmap(mBitmap, getX(), getY(), mPaint);
		}
	}

	/** Metodo para mandar meu objeto ser uma imagem */
	public void drawBitMap(Canvas canvas, Bitmap bitmap) {

		this.mBitmap = bitmap;

		drawBitMap(canvas);
	}

	public void update() {

	}

	// Fun��es para auxiliar a anima��o:

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Bitmap getmBitmap() {
		return mBitmap;
	}

	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public void setBackgroud() {

		// define um plano de fundo
		// backgroudOracle = BitmapFactory.decodeResource(getResources(),
		// R.drawable.oracle);
		// backgroudOracle = Bitmap.createScaledBitmap(backgroudOracle,
		// getWidth(), getHeight(), true);

	}
}