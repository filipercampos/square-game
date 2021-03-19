package br.com.flp.square_game.sprite;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import br.com.flp.square_game.game_engine.Sprite;
import br.com.flp.square_game.R;

/** Usei no comeco para gerar a quadra de varios quadrados na tela */
public class Square extends Sprite {

	// para ver se o quadrado foi destruido
	private boolean dead;
	private boolean bomb;

	public Square(int x, int y) {
		super(x, y, 80, 80);
	}

	public Square(int x, int y, Resources res) {
		super(x, y, 80, 80, res, R.drawable.bomb1);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public void kill() {
		// destroi a animacao
		this.setX(-100);
		this.dead = true;
	}

	public boolean isDead() {
		return dead;
	}

	/** Esse metodo faz o meu objeto sair da vertical superior */
	public void mexe(int height, int width) {


		if (getY() < height) {
			//velocidade de queda
			setY(getY() + 4 );
		} else {
			//posicao aleatoria na tela
			int x = (int) (Math.random() * (width - 50));

			setX(x);
			setY(-50);
		}
	}

	public boolean isColide(int x, int y) {
		if (x > this.x + width) {
			return false;
		}
		if (y > this.y + height) {
			return false;
		}
		if (x < this.x) {
			return false;
		}
		if (y < this.y) {
			return false;
		}
		return true;
	}

	
	public boolean isBomb(){
		return bomb;
	}

	public void setBomb(boolean bomb) {
		this.bomb = bomb;
	}

	public Paint getPaint() {
		return mPaint;
	}

	/** A cor do quadrado */
	public int getCor() {
		return mPaint != null ? mPaint.getColor() : 0;
	}

}