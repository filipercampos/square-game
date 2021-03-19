package br.com.flp.square_game;

import android.app.Activity;
import android.os.Bundle;
import br.com.flp.android.game_engine.GameUtil;
import br.com.flp.square_game.view.MainGameView;

public class MainActivity extends Activity {

	private MainGameView game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// TODO Auto-generated constructor stub
		// Seta o modo tela cheia
		GameUtil.fullScreen(this);

		// mostra a implementacao
		setContentView(this.game = new MainGameView(this));
		 
	}

	protected void onDestroy() {

		super.onDestroy();

		game.gameOver();

	}

}
