package br.com.flp.square_game.game_engine;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class PlaySound {

	private SoundPool soundPool;
	private Context context;

	/**
	 * Construtor para um SoundPool<br/>
	 * Aten��o: N�o carrega arquivos com mais de 1 MB. <br/>
	 * Informa��o testada e n�o mencionada na documenta��o. 
	 * 
	 */
	public PlaySound(Context context, int maxStreams) {

		// TODO Auto-generated constructor stub
		this.context = context;
		soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
	}

	/**
	 * SoundPool funciona como uma mesa de som. Primeiro se cria a "mesa"
	 * atrav�s do construtor passando como par�metros: a quantidade m�xima de
	 * sons que pode ser tocados ao mesmo tempo, o tipo de audio, e a qualidade
	 * (atualmente sem efeito).
	 * 
	 * @param audio
	 */
	public int addSoundPool(int audio) {
		// retorna id do audio
		return soundPool.load(context, audio, 1);
	}

	/** Toca o audio informado na mesa */
	public void play(int id) {
		soundPool.play(id, 1f, 1f, 0, 0, 1f);
	}

	/** Para o audio informado na mesa */
	public void stop(int id) {
		soundPool.stop(id);
	}

	public void release() {

		if (soundPool != null) {
			soundPool.release();
		}
	}
}
