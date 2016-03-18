package fr.ups.sim.superpianotiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


public class TilesStartActivity extends Activity {
    private static TilesView tilesView;
    private int bestScore, level, current_score;
    private boolean debutDePartie;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!tilesView.translate(level)) {
                        handler.post(noTouchTileEndRunnable);
                    }
                    else{
                        handler.postDelayed(runnable, 1);
                    }
                }
            });
        }
    };

    private Runnable noTouchTileEndRunnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!tilesView.noTouchTileAnimation()) {
                        finDePartie();
                    }
                    else{
                        handler.postDelayed(noTouchTileEndRunnable, 1);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiles_start);

        //initialise tilesView
        tilesView = (TilesView) findViewById(R.id.view);

        //gestion des évènements tactiles
        tilesView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEventHandler(event);

            }
        });

        bestScore = 0;
        current_score = 0;
        level = 16;
        debutDePartie = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tiles_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // ICI - A compléter pour déclencher l'ouverture de l'écran de paramétrage
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * méthode appelée lors des évènements tactiles
     */
    private boolean onTouchEventHandler (MotionEvent evt){
        if(evt.getAction() == MotionEvent.ACTION_DOWN){
            int i = tilesView.blackTile(evt.getX(), evt.getY());
            if(debutDePartie){
                if(tilesView.onTouchTile(i)){
                    current_score++;
                    handler.post(runnable);
                    debutDePartie = false;
                }
            }
            else{
                switch(i){
                    case -1:
                        handler.post(noTouchTileEndRunnable);
                        Log.i("TilesView", "WHITE TILE : LOOSER !!!!!!");
                        return false;
                    default:
                        if(tilesView.onTouchTile(i)){
                            current_score++;
                            Log.i("TilesView", "YES !!!! BLACK TILE " + i + "!!!");
                        }
                        else{

                            Log.i("TilesView", "BLACK TILE " + i + "!!!");
                        }
                }
            }
        }
        return true;
    }

    private AlertDialog FinDePartieDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(current_score>bestScore){
            bestScore = current_score;
            builder.setTitle("Best Score");
            builder.setMessage(" Score : " + current_score);
        }
        else{
            builder.setTitle("GAME OVER");
            builder.setMessage("Best Score : " + bestScore + "\n\nYour Score : " + current_score);
        }
        builder.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // retour à l'activité menu

            }
        });
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //recommencer la partie
                        tilesView.retry();
                        current_score = 0;
                        debutDePartie = true;
                    }
                });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void finDePartie(){
        handler.removeCallbacks(runnable);
        FinDePartieDialog().show();
    }
}
