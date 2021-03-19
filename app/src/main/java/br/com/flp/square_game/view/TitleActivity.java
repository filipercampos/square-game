package br.com.flp.square_game.view;

import br.com.flp.android.game_engine.GameUtil;
import android.app.Activity;
import android.os.Bundle;

public class TitleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	GameUtil.fullScreen(this);
       setContentView(new TitleGameView(this));
    }
}