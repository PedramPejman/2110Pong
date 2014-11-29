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
import android.widget.TextView;
import android.widget.Toast;


public class GameActivity extends Activity implements View.OnTouchListener{

    ImageView paddle;
    Ball ball;
    RelativeLayout layout;
    TextView scoreboard;

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
        scoreboard = (TextView) findViewById(R.id.score);
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
                //Log.d(X_MIN + " : " + X_MAX, Y_MIN + " : " + Y_MAX);
                //Log.d("x,y", image.getX() + ", " + image.getY());
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
            if (image.getY() > Y_MAX ) { //The ball is touching the bottom of the screen
                vy *= -1;
                if (detectCollisionWithPaddle()) {
                    //the GameActivity.this is a subtle trick. It's not a big deal if you don't understand it.
                    //Essentially, we need to get ahold of an instance of the GameActivity class because it has the
                    //Context that the Toast object requires. Again, Context is a weird class; don't worry if you don't fully get it.
                    //Toast.makeText(GameActivity.this, "collision", Toast.LENGTH_SHORT).show();
                    incrementScore();
                }
            }
            if (image.getX() < X_MIN ) {
                vx *= -1;
            }
            if (image.getY() < Y_MIN) {
                vy *= -1;
            }
            image.setX(image.getX() + vx);
            image.setY(image.getY() + vy);
        }

        //update the scoreboard
        public void incrementScore() {
            final String STUB = "Score: ";
            String score_text = (String) scoreboard.getText();
            int score = Integer.parseInt(score_text.substring(STUB.length()));
            score++;
            scoreboard.setText(STUB + score);
        }
        public boolean detectCollisionWithPaddle() {
            float paddle_x_min = paddle.getX()  ;
            float paddle_x_max = paddle.getX() + paddle.getMeasuredWidth() ;
            float ball_x = image.getX() + image.getMeasuredWidth()/2;
            //Log.d("paddle:", paddle_x_min + " to " + paddle_x_max);
            //Log.d("ball:", ball_x+ "");
            return ( (ball_x > paddle_x_min) && (ball_x < paddle_x_max));
        }
    }
}
