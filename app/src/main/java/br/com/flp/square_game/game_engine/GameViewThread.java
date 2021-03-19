package br.com.flp.square_game.game_engine;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * @author Filipe
 * 
 *         Todo jogo tem um loop principal que � onde acontece basicamente 3
 *         coisas:
 * 
 *         Captura de entrada; Calculo da f�sica, atualiza��o de IA. Sa�da
 *         (desenhar tela, produzir som).
 * 
 * */
public class GameViewThread extends Thread {

	// GameView principal do jogo, onde tudo ocorrer�. Mostrarei e explicarei
	// ela em breve;
	private GameView view;
	// mRunning � uma vari�vel de controle para parar o loop;
	private boolean running;
	
	//N�o consegui fazer a thread mudar o valor do intervalo em tempo de execu��o
	// velocidade que os objetos irao surgir na tela (delay da thread)
	private int speed;

	public GameViewThread(GameView gameView) {
		this.view = gameView;

		this.running = false;
	}

	

	@Override
	public void run() {
		Canvas canvas = null;
		while (running) {

			SurfaceHolder sh = null;

			try {

				// trava e retorna asuperf�cie a ser editada apenas por este
				// processo;
				canvas = view.getHolder().lockCanvas();

				// SurfaceHolder da GameView (extens�o da SurfaceView)
				// que controla o acesso a �rea dedesenho da View
				sh = view.getHolder();

				synchronized (sh) {

					// aplica as atualizacoes
					// gameView.update();
					// realizo o desenho
					if (view != null) {
						view.update();
						view.onDraw(canvas);						
					}
				}

			} finally {
				if (canvas != null) {

					// para destravar e desenhar a �rea de desenho da View na
					// tela.
					view.getHolder().unlockCanvasAndPost(canvas);
				}
			}

			try {
				if (getSpeed() > 0) {
					Thread.sleep(getSpeed());
				}
			} catch (Exception e) {
				Log.e("GameViewThread", "Erro no speed da thread "
								+ GameViewThread.this.getClass());
			}
		}
	}

	public void setRunning(boolean running) {

		this.running = running;
	}
	public boolean isRunning() {
		return running;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

}