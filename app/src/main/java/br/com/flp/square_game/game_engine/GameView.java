package br.com.flp.square_game.game_engine;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 
 * Ela que ir� preparar e desenhar a tela, lidar� com o update() geral e com o
 * onDraw().
 * 
 * Estender o SurfaceView, que � uma View que permite manipula��o direta do que
 * ir� desenhar, pixel a pixel e ela eh mais rapida que uma {@link View}.
 * 
 * 
 * Toda SurfaceView est� ligada a uma SurfaceHolder e essa implementa alguns
 * m�todos para indicar se a SurfaceView est� criada, se foi mudada ou
 * destru�da.
 * 
 * */
public abstract class GameView extends SurfaceView implements
		SurfaceHolder.Callback {

	/**
	 * Toda SurfaceView est� ligada a uma SurfaceHolder e essa implementa alguns
	 * m�todos para indicar se a SurfaceView est� criada, se foi mudada ou
	 * destru�da. Em surfaceCreated verificamos se a GameLoopThread j� foi
	 * executada (jogo estava executando e outra aplica��o tomou o palco, assim
	 * como ir pra home do Android) e se for o caso temos que iniciar outra
	 * Thread pois a antiga est� morta e n�o pode ser iniciada de novo, caso
	 * contr�rio a SurfaceView est� sendo criada pela primeira vez ent�o n�o h�
	 * necessidade de criar uma nova Thread e sim executar o onLoad, que deve
	 * ser implementado pela GameView do seu jogo para carregar o estado inicial
	 * do seu jogo.
	 * */
	protected SurfaceHolder mHolder;
	// Loop principal
	protected GameViewThread gameLoop;
	// desenho para a tela
	protected Bitmap backgroundDesign;
	// Sprites para facilitar o desenho de imagens na tela.
	protected ArrayList<Sprite> mSprites;

	/** Prepara e desenha os elementos que ser�o exibidos na tela */
	protected abstract void onLoad();

	/**
	 * Obt�m as informa��es de processamento para o {@link onTouchEvent} da
	 * classe
	 */
	public abstract void TouchEvents(MotionEvent event);

	public GameView(Context context) {
		super(context);

		gameLoop = new GameViewThread(this);
		mSprites = new ArrayList<Sprite>();
		mHolder = getHolder();
		mHolder.addCallback(this);

	}

	/**
	 * Limpa a tela e pinta tudo de preto. <br/>
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (canvas == null || mSprites == null) {

			Log.i("GameView", "onDraw ->Canvas null");
			return;
		}
		// pinta tudo de preto
		// canvas.drawRGB(0, 0, 0);
		// tbm posso fazer
		canvas.drawColor(Color.BLACK);

		for (Sprite sprite : mSprites) {
			// realiza o desenho
			if (sprite.isVisible()) {
				sprite.drawBitMap(canvas);
			} else {
				sprite.onDraw(canvas);
			}
		}

	}

	/**
	 * Entrada no jogo: Tela sens�vel ao toque.<br/>
	 * Recebe o m�todo {@link TouchEvents} que deve ser implementado pelo filho
	 * do GameView.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		/**
		 * Impede que o TouchEvents seja executado ao mesmo tempo que o update
		 * ou o onDraw. Pronto, agora basta o seu GameView implementar o
		 * TouchEvents e usar o argumento recebido para tirar as informa��es e
		 * fazer o processamento. Aten��o: Pode haver mais de uma chamada ao
		 * onTouchEvent por frame.
		 */
		synchronized (getHolder()) {
			TouchEvents(event);
		}
		return true;
	}

	/**
	 * Veririca se a GameLoopThread j� foi executada (jogo estava executando e
	 * outra aplica��o tomou o palco, assim como ir pra home do Android) e se
	 * for o caso temos que iniciar outra Thread pois a antiga est� morta e n�o
	 * pode ser iniciada de novo, caso contr�rio a SurfaceView est� sendo criada
	 * pela primeira vez ent�o n�o h� necessidade de criar uma nova Thread e sim
	 * executar o onLoad, que deve ser implementado pela GameView do seu jogo
	 * para carregar o estado inicial do seu jogo.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (gameLoop.getState() == Thread.State.TERMINATED) {
			gameLoop = new GameViewThread(GameView.this);
		} else {
			onLoad();
		}
		gameLoop.setRunning(true);
		gameLoop.start();
	}

	/**
	 * Para o GameLoopThread, setamos o estado com
	 * gameLoopThread.setRunning(false) e usamos um loop que junto com o
	 * gameLoopThread.join() vai esperar nossa GameLoopThread terminar.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		gameLoop.setRunning(false);
		while (retry) {
			try {
				// Espere a aplica��o terminar
				// Isso � necess�rio pois depois
				// da execu��o da surfaceDestroyed, nada pode ser desenhado
				// na surface e se n�o pararmos o looop ele tentar� fazer
				// isso gerando um crash no aplicativo.
				gameLoop.join();
				retry = false;

			} catch (InterruptedException e) {
			}
		}
	}

	// SurfaceChanged ainda n�o faz nada
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	public Bitmap getBackgroundDesign() {
		return backgroundDesign;
	}

	public void setBackgroundDesign(Resources resource, int design) {

		if (resource != null) {
			this.backgroundDesign = BitmapFactory.decodeResource(
					getResources(), design);
			this.backgroundDesign = Bitmap.createScaledBitmap(backgroundDesign,
					getWidth(), getHeight(), true);
		}
	}

	public void paintBackground(Canvas canvas,int cor) {

		if (backgroundDesign != null) {
			canvas.drawColor(cor);
		}
	}
	
	public void paintBackground(Canvas canvas) {

		if (backgroundDesign != null) {
			canvas.drawBitmap(backgroundDesign, 0, 0, new Paint());
		}
	}

	/**
	 * Atualizacao dos sprites da vis�o do jogo, sendo que cada sprite da lista
	 * executar� o seu pr�prio m�todo update(), que servir� para atualizar sua
	 * imagem, posi��o, rea��o de IA.
	 * 
	 */
	public void update() {
		for (Sprite sprite : mSprites)
			sprite.update();
	}

}