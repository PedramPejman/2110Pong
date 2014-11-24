package com.pedapps.cs2110example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class GameActivity extends Activity implements View.OnTouchListener{

    ImageView paddle;
    Ball ball;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setUpLayout();
        setUpPaddle();
        setUpBall();
    }

    public void setUpLayout() {
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setOnTouchListener(this);
    }

    public void setUpPaddle() {
        paddle = (ImageView) findViewById(R.id.paddle);
    }

    public void setUpBall() {
        ball = new Ball();
        ball.image.setX(100);
        ball.execute();
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
            paddle.setX(e.getX());
        return true;
    }


    public class Ball extends AsyncTask<Void, Integer, Void>{
        ImageView image;
        boolean gameOn;
        float vx, vy;
        int X_MIN, X_MAX, Y_MIN, Y_MAX, THRESH;

        public Ball() {
            image = (ImageView) findViewById(R.id.ball);
            gameOn = true;
            vx = 5;
            vy = 5;
        }

        protected Void doInBackground(Void... v) {

            while(gameOn) {
                THRESH = 40;
                X_MIN = THRESH;
                X_MAX = layout.getMeasuredWidth() - image.getMeasuredWidth() - THRESH;
                Y_MIN = THRESH;
                Y_MAX = layout.getMeasuredHeight() - image.getMeasuredHeight() - THRESH;
                try{
                    Thread.sleep(10);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
                Log.d(X_MIN + " : " + X_MAX, Y_MIN + " : " + Y_MAX);
                Log.d("x,y", image.getX() + ", " + image.getY());
            }
            return null;
        }

        protected void onProgressUpdate(Integer... ints) {
            moveBall();
        }

        protected void onPostExecute(Void e) {

        }

        public void moveBall() {
            if (image.getX() > X_MAX ) {
                vx *= -1;
            }
            if (image.getY() > Y_MAX ) {
                vy *= -1;
            }
            if (image.getX() < X_MIN ) {
                vx *= -1;
            }
            if (image.getY() < Y_MIN) {     //The ball is touching the bottom of the screen
                if (detectCollisionWithPaddle()) {
                    vy *= -1;
                }
                else {
                    //the GameActivity.this is a subtle trick. It's not a big deal if you don't understand it.
                    //Essentially, we need to get ahold of an instance of the GameActivity class because it has the
                    //Context that the Toast object requires. Again, Context is a weird class; don't worry if you don't fully get it.
                    Toast.makeText(GameActivity.this, "You lost", Toast.LENGTH_SHORT).show();
                    gameOn = false;
                }
            }
            image.setX(image.getX() + vx);
            image.setY(image.getY() + vy);
        }

        public boolean detectCollisionWithPaddle() {
            //Log.d("paddle:", paddle.getX() + " : " + paddle.getY());
            //Log.d("ball:", image.getX() + " : " + image.getY());
            return ( (image.getX() > paddle.getX()) && (image.getX() < (paddle.getX() + paddle.getMeasuredWidth())));
        }
    }
}
