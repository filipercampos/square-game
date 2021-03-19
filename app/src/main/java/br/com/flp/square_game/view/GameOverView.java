package br.com.flp.square_game.view;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import br.com.flp.square_game.MainActivity;
import br.com.flp.square_game.game_engine.GameView;
import br.com.flp.square_game.game_engine.PlayMusic;
import br.com.flp.square_game.game_engine.Sprite;
import br.com.flp.square_game.R;

/**
 * GameOverView � a tela que ser� chamada no fim do jogo.<br/>
 * Ela herda da {@link GameView} que � um {@link SurfaceView} e � mais r�pida
 * que a {@link View}
 */
public class GameOverView extends GameView {

	private Paint paint;
	private Context context;
	private int score, highScore;
	private Sprite gameover;
	private PlayMusic gameoverMusic;

	public GameOverView(Context context) {
		super(context);
		this.context = context;

	}

	@Override
	public void TouchEvents(MotionEvent event) {

		if (gameLoop.isRunning() && gameoverMusic.isPlaying() == false) {
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {

				gameLoop.setRunning(false);
				// TODO Auto-generated method stub
				Context ctx = getContext();
				((Activity) ctx).finish();
				Intent intent = new Intent(ctx, MainActivity.class);
				// chama a tela do jogo novamente
				ctx.startActivity(intent);

			}
		}
	}

	/*
	 * Inicializa a imagem, exibe e salva placar realizado se ele for maior que
	 * o anterior
	 */
	@Override
	public void onLoad() {

		Log.i("GameOverView", "Fim de jogo");
		this.gameover = new Sprite(BitmapFactory.decodeResource(getResources(),
				R.drawable.gameover));

		this.gameover.setVisible(true);

		mSprites.add(gameover);

		gameover.setX(getWidth() / 2 - gameover.width / 2);
		gameover.setY((int) (getHeight() * 0.2f));
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(40);
		score = ((Activity) context).getIntent().getIntExtra("SCORE", 0);

		// Manda um som
		gameoverMusic = new PlayMusic(context, R.raw.gameover);
		gameoverMusic.play();

		saveNewScore();

	}

	/** Salva a nova pontua��o */
	public void saveNewScore() {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		highScore = prefs.getInt("HIGH_SCORE", 0);
		if (score > highScore) {
			prefs.edit().putInt("HIGH_SCORE", score).commit();
		}
	}

	/** Exibe a pontua��o realizada e maior pontua��o atual */
	@Override
	public void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		if (canvas != null) {
			NumberFormat format = NumberFormat.getInstance();

			String scoreString = context.getString(R.string.score) + " "
					+ format.format(score);
			String highScoreString = context.getString(R.string.highscore)
					+ " " + format.format(highScore);

			String startgame = context.getString(R.string.iniciar_jogo);

			canvas.drawText(scoreString, 50, getHeight() * 0.6f, paint);

			canvas.drawText(highScoreString, 50, getHeight() * 0.68f, paint);

			canvas.drawText(startgame, 50, getHeight() * 0.82f, paint);
		}
	}

	public PlayMusic getGameoverMusic() {
		return gameoverMusic;
	}

}
