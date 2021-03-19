package br.com.flp.square_game.game_engine;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * @author Filipe
 * 
 *         Todo jogo tem um loop principal que é onde acontece basicamente 3
 *         coisas:
 * 
 *         Captura de entrada; Calculo da física, atualização de IA. Saída
 *         (desenhar tela, produzir som).
 * 
 * */
public class GameViewThread extends Thread {

	// GameView principal do jogo, onde tudo ocorrerá. Mostrarei e explicarei
	// ela em breve;
	private GameView view;
	// mRunning é uma variável de controle para parar o loop;
	private boolean running;
	
	//Não consegui fazer a thread mudar o valor do intervalo em tempo de execução
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

				// trava e retorna asuperfície a ser editada apenas por este
				// processo;
				canvas = view.getHolder().lockCanvas();

				// SurfaceHolder da GameView (extensão da SurfaceView)
				// que controla o acesso a área dedesenho da View
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

					// para destravar e desenhar a área de desenho da View na
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