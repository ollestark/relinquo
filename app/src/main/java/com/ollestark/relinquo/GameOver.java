package com.ollestark.relinquo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Olle on 2016-05-21.
 */
public class GameOver extends Activity implements View.OnClickListener {
    TextView gameOver;
    TextView tidTagning;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);
        Bundle bundle = getIntent().getExtras();

        //Textvyer dar slutmeddelande och slutgiltig tid visas.
        gameOver = (TextView) findViewById(R.id.gameover);
        tidTagning = (TextView) findViewById(R.id.tidtagare);
        String resultat = (String) bundle.get("Resultatet");
        tidTagning.setText(resultat);

        //Knappwidget for att anropa metod som leder till foregaende klass "Timer".
        resetButton = (Button) findViewById(R.id.resetbutton);
        resetButton.setOnClickListener(this);

    }
    //onClick-metod dar "Timer"-klassen startas om pa nytt.
    public void onClick(View v) {
        Intent intent = new Intent(this, com.ollestark.relinquo.Timer.class);
        startActivity(intent);
        finish();

    }
}
