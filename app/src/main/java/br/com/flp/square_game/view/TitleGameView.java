package br.com.flp.square_game.view;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import androidx.preference.PreferenceManager;

import br.com.flp.square_game.game_engine.GameView;
import br.com.flp.square_game.game_engine.Sprite;
import br.com.flp.square_game.R;
import br.com.flp.square_game.MainActivity;

public class TitleGameView extends GameView {

    private Paint paint;
    private int highScore;
    protected Context context;
    private Sprite title;

    public TitleGameView(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onLoad() {
        Log.i("TitleGameView", "Tela inicial");
        this.title = new Sprite(BitmapFactory.decodeResource(getResources(),
                R.drawable.square_title));

        this.title.setVisible(true);
        mSprites.add(title);
        title.setX(getWidth() / 3 - title.width / 3);
        title.setY(20);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        // carrega a melhor pontuação feita
        highScore = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("HIGH_SCORE", 0);

    }

    @Override
    public void TouchEvents(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            Context ctx = getContext();
            ((Activity) ctx).finish();
            Intent intent = new Intent(ctx, MainActivity.class);
            ctx.startActivity(intent);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {

            String highScoreString = context.getString(R.string.highscore)
                    + " " + NumberFormat.getInstance().format(highScore);

            canvas.drawText(context.getString(R.string.iniciar_jogo), 60,
                    getHeight() * 0.6f, paint);

            canvas.drawText(highScoreString, 40, getHeight() * 0.8f, paint);
        }
    }

}