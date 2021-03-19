package br.com.flp.square_game.game_engine;

import android.content.Context;
import android.media.MediaPlayer;

public class PlayMusic {

	private Context context;
	private MediaPlayer mediaPlayer;
	private boolean stop;
	private int music;
 

	public PlayMusic(Context context, int music) {

		this.mediaPlayer = MediaPlayer.create(context, music);
		this.context = context;
		this.music = music;
	}



	public void setMusic(int music) {

		this.mediaPlayer = MediaPlayer.create(context, music);
		this.music = music;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void play() {
		if (mediaPlayer != null) {
			this.mediaPlayer.start();
			this.stop = false;
		}
	}

	public boolean isPlaying() {

		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			return true;
		}
		return false;
	}

	/**Diferente do play o restart recria a musica informada anteriormente*/
	public void restart() {
		if (mediaPlayer != null) {			
			this.mediaPlayer = MediaPlayer.create(context, music);
			this.mediaPlayer.start();
			this.stop = false;
		}
	}
	
	public void stop() {
		if (mediaPlayer != null) {
			this.stop = true;
			this.mediaPlayer.stop();
		}
	}

	public boolean isStop() {

		return stop;
	}

	public void release() {

		if (mediaPlayer != null) {
			this.mediaPlayer.release();
		}

	}

}
