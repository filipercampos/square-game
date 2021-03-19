package br.com.flp.android.game_engine;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class GameUtil {
	
	/**Seta o modo de tela cheia. <br/>
	 * 
	 * Use no metodo onCreate
	 * 
	 * */
	public static void fullScreen(Activity activity){		

        // remove o titulo
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Seta o modo tela cheia
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);       	
	
	}
	
	/**
	 * O tempo atual da OS
	 */
	public static long getTimeNow() {

		return System.currentTimeMillis();

	}

}
