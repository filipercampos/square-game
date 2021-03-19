package br.com.flp.square_game.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import br.com.flp.android.game_engine.GameUtil;
import br.com.flp.square_game.game_engine.GameView;
import br.com.flp.square_game.game_engine.PlayMusic;
import br.com.flp.square_game.game_engine.PlaySound;
import br.com.flp.square_game.R;
import br.com.flp.square_game.sprite.GameLevel;
import br.com.flp.square_game.sprite.GameScore;
import br.com.flp.square_game.sprite.Square;

public class MainGameView extends GameView {
	// posicao do lado esquerdo
	// int width = getWidth();
	// posicao do lado direito
	// int height = getHeight();

	/** Classe para chama a tela de fim de jogo */
	private class GameOver implements Runnable {
		/** Termina o jogo e chama a tela de gameover */
		@Override
		public void run() {

			gameOver = true;
			// se acabou por causa da detonacao da bomba
			if (explosion) {
				// reproduza um som
				playSound.play(idBombExplosion);

				// espere a reproducao do som
				try {

					Log.e("GameOver", "Aguardando explosão concluir");
					Thread.sleep(2500);
				} catch (InterruptedException e) {

				}
			}

			// TODO Auto-generated method stub
			MainGameView.this.gameLoop.setRunning(false);
			// pare a musica de fundo
			castleMusic.release();
			dyingMusic.release();

			// para o audio se estiver tocando ou nao
			playSound.stop(idTimeOut);

			// termina a mesa de som
			playSound.release();

			((Activity) context).finish();
			Intent intent = new Intent(context, GameOverActivity.class);
			intent.putExtra("SCORE", score);
			context.startActivity(intent);

		}

	}

	private List<Square> squareList;
	// objeto para pintar os objeto que irao aparecer
	private List<Paint> paints;
	private long startTime;
	private long timeLevel;
	// Controlador de audio
	private PlayMusic castleMusic;
	private PlayMusic dyingMusic;
	private PlaySound playSound;
	private int idLevelUp;
	private int idScoreHit;
	private int idTimeOut;
	private int idFailHit;
	private int idBombExplosion;
	private int idWin;
	// flag para chama tela de game somente uma vez
	// o flag running ainda estara ativo quando o game over for chamado
	// pois uma thread para aguardar o som da bomba explodindo eh lancada dentro
	// da Runnable GameOver
	private boolean gameOver;
	// Atividade a ser chamada
	private final Context context;
	// Contador do tempo
	private long time;
	private Paint paint;
	// contador de spriteList capturados pelo touch
	private int score;
	// flag para subir de level no jogo
	private boolean levelUp;
	// contador de falha ao tentar destruir o objeto
	private int scoreFailedCount;
	// level atual do jogo
	private int levelAtual;
	// aviso de destruicao
	private boolean explosion;

	public MainGameView(Context context) {

		super(context);
		this.context = context;
		this.paint = new Paint();
		this.scoreFailedCount = 0;
		this.levelAtual = GameLevel.LEVEL_1;
		this.time = 0;
		this.score = 0;
	}

	/** Um lista de cores para pintar a tela */
	private void initColors() {

		// objeto para desenhar os spriteList que irao surgir
		this.paints = new ArrayList<Paint>();

		for (int i = 0; i < 7; i++) {

			Paint p = new Paint();
			if (i == 0)
				p.setColor(Color.GREEN);
			if (i == 1)
				p.setColor(Color.RED);
			if (i == 2)
				p.setColor(Color.MAGENTA);
			if (i == 3)
				p.setColor(Color.YELLOW);
			if (i == 4)
				p.setColor(Color.WHITE);
			if (i == 5)
				p.setColor(Color.BLACK);
			if (i == 6)
				p.setColor(Color.BLUE);

			paints.add(p);

		}

	}

	/** Cria os elementos do tipo Square a serem exibidos na tela */
	private List<Square> createElements() {

		List<Square> squareList = new ArrayList<Square>();
		int width = getWidth();
		int height = getHeight();
		Resources r = getResources();
		int count = (height / 100);

		for (int i = 0; i < count; i++) {
			int y = i * -100;
			int x = (int) (Math.random() * (width - 50));

			if (r != null) {
				squareList.add(new Square(x, y, r));
			} else {
				squareList.add(new Square(x, y));
			}
		}

		return squareList;
	}

	/** Inicia os audios que serao emitidos durante o jogo */
	private void initSounds() {

		this.castleMusic = new PlayMusic(context, R.raw.dracula_castle_music);

		this.dyingMusic = new PlayMusic(context, R.raw.dying_music);

		this.playSound = new PlaySound(context, 10);

		this.idLevelUp = playSound.addSoundPool(R.raw.level_up);
		this.idScoreHit = playSound.addSoundPool(R.raw.score_hit);
		this.idFailHit = playSound.addSoundPool(R.raw.fail_hit);
		this.idBombExplosion = playSound.addSoundPool(R.raw.bomb_explosion);
		this.idTimeOut = playSound.addSoundPool(R.raw.time_warning);
		this.idWin = playSound.addSoundPool(R.raw.win);

	}

	/** Inicializa os componentes da atividade */
	@Override
	public void onLoad() {

		Log.i("TAG", "Jogo em execucao");

		this.startTime = System.currentTimeMillis();
		this.timeLevel = System.currentTimeMillis();
		// cria uma mesa com os sons que serão emitido
		initSounds();
		// um lista de cores para os squares
		initColors();

		// defino a cor do texto
		paint.setColor(Color.BLACK);

		paint.setTextSize(40);

		// toque a musica de fundo
		castleMusic.play();
		// crie os elementos
		this.squareList = createElements();
		// permissao para rodar o jogo
		this.gameLoop.setRunning(true);

		// imagem de fundo
		this.setBackgroundDesign(getResources(), R.drawable.sky);

		// mudei a interação do alerta
		// Thread para emitir um alerta sonoro que o fim se aproxima
		// new Thread(new Bomb()).start();

	}

	/**
	 * Realiza a queda e velocidade dos quadrado Metodo principal que faz o meu
	 * jogo rodar
	 */
	@Override
	public void update() {

		if (isGameOver() && gameOver == false) {
			runGameOver();
		} else {

			// movimenta os objetos em uma velocidade x
			if (squareList != null) {
				for (Square s : squareList) {
					s.setY(s.getY() + gameLoop.getSpeed());
					s.mexe(getHeight(), getWidth());
				}
			}

			// verificaçoes em tempo de execução
			try {

				// se estive em perigo das vidas acabar
				// e a musica terminou ou ainda nao foi tocada
				if (isDangerous() && dyingMusic.isPlaying() == false) {
					// pare a musica de fundo
					castleMusic.stop();

					// toque o aviso
					dyingMusic.restart();
				}

				// se a musica acabar
				// e a musica que as vida estão acabando nao estiver rolando
				// comece denovo
				// se a musica parou e nao esta em perido
				// e nao detonaram a bomba
				else if (castleMusic.isPlaying() == false
						&& isDangerous() == false && explosion == false) {

					// pare o aviso
					dyingMusic.stop();

					// toca a musica denovo
					castleMusic.restart();

				}
			} catch (IllegalStateException ex) {

			}
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (canvas != null) {
			drawCustom(canvas);
			updateScore(canvas);
			updateTempo(canvas);
			updateStatus(canvas);
		}

	}

	/* Exibe um texto no canto superior no inicio da tela */
	private void updateScore(Canvas canvas) {
		// TODO Auto-generated method stub

		paint.setColor(Color.WHITE);
		if (getScore() >= GameScore.SCORE_LEVEL_MAX) {
			canvas.drawText("Pontuação Máxima: §§§§§§§§§", 20, 60, paint);
		} else {
			canvas.drawText("Pontos: " + getScore(), 20, 60, paint);
		}
	}

	/** Coloca o texto do tempo em formato mm:yy */
	private void updateTempo(Canvas canvas) {
		// TODO Auto-generated method stub

		int segundos = (int) (GameUtil.getTimeNow() - timeLevel) / 1000;

		segundos = (int) (segundos - (this.time * 60));

		// add o 0 antes segundos pra ficar no padrao HH:mm:ss
		String secs = (segundos < 10) ? "0" + segundos : "" + segundos;

		if (segundos > 59) {
			this.time++;
			secs = "" + 0;
		}

		// congela o jogo
		if (time >= 60) {
			canvas.drawText("Tempo: " + time + ":" + secs,
                    getWidth() - 260,
					60, paint);
			gameOver();
			return;
		}
		// add o 00:00
		else if (time < 10) {

			canvas.drawText("Tempo: 0" + time + ":" + secs,
                    getWidth() - 260,
					60, paint);
		} else {
			canvas.drawText("Tempo: " + time + ":" + secs,
                    getWidth() - 260,
					60, paint);
		}
	}

	/* Exibe um texto no lado direito inferior no fim da tela */

	private void updateStatus(Canvas canvas) {
		// TODO Auto-generated method stub

		paint.setColor(Color.BLACK);
		canvas.drawText("Falhas: " + getScoreFailedCount(), 10,
				getHeight() - 220, paint);

		// getWidth() - 180
		// getHeight() - 10
		canvas.drawText("Destrua os objetos", 10, getHeight() - 170, paint);

		canvas.drawText("Level: " + levelAtual, 10, getHeight() - 120, paint);
	}

	@Override
	public void TouchEvents(MotionEvent event) {

		if (isGameOver()) {
			return;
		}
		// pega o evento
		int action = event.getAction();
		// pega a posicao do dedo
		int x = (int) event.getX();
		int y = (int) event.getY();

		// se apertou o dedo
		if (action == MotionEvent.ACTION_DOWN) {

			// registra o ponto
			if (setScore(x, y)) {

				playSound.play(idScoreHit);
			} else {

				playSound.play(idFailHit);

				// verifique e avise se os objetos não estão sendo destruidos
				setScoreFailedCount();

				if (isTimeOut()) {

					Log.i("Dangerous", "" + getScoreFailedCount());
					playSound.play(idTimeOut);

				}
			}

			// verifique ou atualize o level
			updateLevel();

			if (isLevelUp()) {

				playSound.play(idLevelUp);

				clearGameView();

				if (score >= GameScore.SCORE_LEVEL_10) {

					playSound.play(idWin);

				}
			}

		} else if (action == MotionEvent.ACTION_UP) {
			// soltou o dedo

		} else if (action == MotionEvent.ACTION_MOVE) {
			// movimentou o dedo

		}

	}

	/** Limpar os elementos da tela */
	public void clearGameView() {
		// TODO Auto-generated method stub
		for (Square s : squareList) {
			// destroi a animacao
			s.kill();
		}

	}

	/** Termina o jogo e chama a tela de gameover */
	public void runGameOver() {
		// thread para terminar o jogo
		new Thread(new GameOver()).start();

	}

	/* Customiza o metodo draw */
	public void drawCustom(Canvas canvas) {

		if (canvas == null || squareList == null) {
			return;
		}
		// desenha a cor / imagem do plano de fundo
		this.paintBackground(canvas);

		int j = -1, tam = squareList.size();

		for (int i = 0; i < tam; i++) {

			Square s = squareList.get(i);
			// faz o repaint com uma nova cor
			if (j < paints.size() - 1) {

				j++;
			} else {
				// terminou o vetor de cores
				// add a cor comecei o vetor de cores novamente
				j = 0;
			}

			// define a cor do desenho
			s.setmPaint(paints.get(j));

			// joga duas bomba
			if (getScore() > GameScore.SCORE_LEVEL_1
					&& getScore() < GameScore.SCORE_LEVEL_5
					// se o for menor que dois transforme o square em robo
					&& i < 2) {
				s.setBomb(true);
				// jogue uma bomba
				s.drawBitMap(canvas);

			}

			// Joga a metade quadrado e outra metade um
			else if (getScore() > GameScore.SCORE_LEVEL_5 && i % 2 == 0) {
				s.setBomb(true);
				// jogue um robo
				s.drawBitMap(canvas);

			} else {
				s.setBomb(false);
				// use o metodo de desenho default
				s.onDraw(canvas);
			}

		}

	}

	/** Seta a pontuacao ou falha */
	private boolean setScore(int x, int y) {

		if (squareList != null) {
			if (score < GameScore.SCORE_LEVEL_MAX) {
				// afundou o dedo
				for (Square s : squareList) {

					if (s.isColide(x, y)) {

						// destroi a animacao
						s.kill();

						// se eu destrui uma bomba acaba o jogo
						if (s.isBomb()) {

							// avise para o jogo terminar
							explosion = true;
							castleMusic.stop();
							return false;
						}
						if (scoreFailedCount > 0) {
							scoreFailedCount--;
						}

						bonusScore();

						return true;
					}
				}
			}
		}

		return false;
	}

	/** Bonus em pontos dependendo de quao rapido o square foi capturado */
	private void bonusScore() {

		int add = (int) Math.max(
				100 - levelAtual * 3 - (System.currentTimeMillis() - startTime)
						/ 500, 1);

		if (add < 10) {
			add = 10;

			score += 10;
		} else {

			score += add;

		}

		/*
		 * switch (score) {
		 *
		 * case GameScore.SCORE_LEVEL_2: score = 2; break;
		 *
		 * case GameScore.SCORE_LEVEL_3: score += 3; break;
		 *
		 * case GameScore.SCORE_LEVEL_4: score += 4; break;
		 *
		 * case GameLevel.LEVEL_5: score += 3; break;
		 *
		 * default: score = score * ((levelAtual/2)+1); break;
		 *
		 * }
		 */

		Log.e("Bonus", "Bonus:" + score);

	}

	/** Numero de falhas cometidas pelo jogador */
	public int getScoreFailedCount() {
		return scoreFailedCount;
	}

	private void setScoreFailedCount() {

		if (this.scoreFailedCount == 0) {

			this.scoreFailedCount = 1;
		} else {
			this.scoreFailedCount++;
		}

	}

	public int getScore() {
		return score;
	}

	private void updateLevel() {
		// TODO Auto-generated method stub

		Log.e("LEVEL", "Level antes: " + levelAtual + " " + isLevelUp());

		setLevelUp(false);

		switch (levelAtual) {
		case GameLevel.LEVEL_1:
			if (score > GameScore.SCORE_LEVEL_1) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_2;
			}
			break;
		case GameLevel.LEVEL_2:
			if (score > GameScore.SCORE_LEVEL_2) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_3;
				this.gameLoop.setSpeed(1);
			}
			break;
		case GameLevel.LEVEL_3:
			if (score > GameScore.SCORE_LEVEL_3) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_4;
				this.gameLoop.setSpeed(2);
			}
			break;
		case GameLevel.LEVEL_4:
			if (score > GameScore.SCORE_LEVEL_4) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_5;
				this.gameLoop.setSpeed(3);
			}
			break;
		case GameLevel.LEVEL_5:
			if (score > GameScore.SCORE_LEVEL_5) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_6;
				this.gameLoop.setSpeed(4);
			}
			break;
		case GameLevel.LEVEL_6:
			if (score > GameScore.SCORE_LEVEL_6) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_7;
				this.gameLoop.setSpeed(5);
			}
			break;
		case GameLevel.LEVEL_7:
			if (score > GameScore.SCORE_LEVEL_7) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_8;
				this.gameLoop.setSpeed(6);
			}
			break;
		case GameLevel.LEVEL_8:
			if (score > GameScore.SCORE_LEVEL_8) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_9;
				this.gameLoop.setSpeed(7);
			}
			break;
		case GameLevel.LEVEL_9:
			if (score > GameScore.SCORE_LEVEL_9) {
				setLevelUp(true);
				levelAtual = GameLevel.LEVEL_10;
				this.gameLoop.setSpeed(8);
			}
			break;

		default:
			setLevelUp(false);
			break;
		}
		Log.i("LEVEL", "Level depois: " + levelAtual + " " + isLevelUp());
	}

	public boolean isLevelUp() {

		return levelUp;
	}

	public void setLevelUp(boolean levelUp) {

		this.timeLevel = System.currentTimeMillis();
		// TODO Auto-generated method stub
		this.levelUp = levelUp;

	}

	public boolean isTimeOut() {

		return time >= 60 || scoreFailedCount == 7;
	}

	public boolean isDangerous() {

		return scoreFailedCount == 9;
	}

	/** Verifica se o usuario cometou 10 erros ou se a bomba foi detonada */
	public boolean isGameOver() {

		// bomba explodiu // se for igual a 10 termine o laco
		return explosion == true || scoreFailedCount >= 10;
	}

	/** Termina o jogo */
	public void gameOver() {

		this.gameLoop.setRunning(false);
		castleMusic.release();
		dyingMusic.release();
		playSound.release();
	}

	public PlaySound getPlaySound() {
		return playSound;
	}

	public PlayMusic getBackgroud_music() {
		return castleMusic;
	}
	/**
	 * Classe para ilustrar a detonacao da bomba private class Bomb implements
	 * Runnable {
	 *
	 * private long tick;
	 *
	 * public Bomb() { this.tick = 3000;
	 *
	 * }
	 *
	 * @Override public void run() {
	 *
	 *           while (gameLoop.isRunning()) {
	 *
	 *           if (getScoreFailedCount() > 7 && getScoreFailedCount() < 10) {
	 *           // permissao para iniciar a musica de fundo blow = true; //
	 *           pare a musica de fundo if (castleMusic.isStop() == false) {
	 *           castleMusic.stop(); }
	 *
	 *           try {
	 *
	 *           if (bombMusic.isPlaying()) { Log.i("Bomb", "Bomba foi parada");
	 *           bombMusic.stop();
	 *
	 *           } else { bombMusic.restart(); Log.e("Bomb",
	 *           "Bomba vai explodir "); Thread.sleep(tick); }
	 *
	 *           } catch (InterruptedException e) { // TODO Auto-generated catch
	 *           block e.printStackTrace(); }
	 *
	 *           } else { if (bombMusic.isPlaying()) { Log.i("Bomb",
	 *           "Bomba foi parada"); bombMusic.stop(); blow = false; } } } } }
	 */
}
