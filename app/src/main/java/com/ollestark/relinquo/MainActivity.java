package com.ollestark.relinquo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Startknapp och onClickListener-metodens anrop som startar den huvudsakliga aktivitetsklassen
        startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(this);
    }



    //Metod for att starta nasta aktivitet/klass "Timer" nar knapptryck detekteras, dar appens
    //huvudsakliga funktionalitet ar placerad. Harifran inkluderas inga extras.
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, com.ollestark.relinquo.Timer.class);
        startActivity(intent);
        finish();

    }
}
