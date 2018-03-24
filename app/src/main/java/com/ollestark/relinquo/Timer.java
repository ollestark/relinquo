//Timer-klassen ar applikationens huvudsakliga aktivitet, dar saval tidraknaren och accelerometern
//ar placerade.
package com.ollestark.relinquo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

public class Timer extends Activity implements SensorEventListener {

    //Variabler for tidtagare. Variabler av typen long kommer anvandas
    //for att lagra millisekunder, vilka mats i antal millisekunder sedan
    //den forsta januari 1970 och behover darfor storre lagringskapacitet
    //an int-variablerna som bara lagrar mindre tal som timmar och minuter.
    //Slutligen skapas en instans av klassen Handler for att senare anvandas
    //tillsammans med interfacet Runnable dar tidtagningen sker.
    //(Singh, u. a., Oracle, u. a., se referenslista i rapport).

    TextView tidtagning;
    long starttid = 0L;
    long tidIMillisekunder = 0L;
    int sekunder = 0;
    int minuter = 0;
    int timmar = 0;
    int millisekunder = 0;
    Handler handler = new Handler();

    //Variabler for accelerometer och rorelsedetektion. De tva forsta anvands 
    //for att fa tillgang till Android-systemets accelerometer, och resterande
    //anvands i en senare metod for att detektera accelerometerns forandrade tillstand.
    //(Android Developers, u. a. a., Android Developers, u. a. b., Govender, 2014, Stack Overflow, 2013, se referenslista i rapport).

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long aktuellTid;
    private long passeradTid;
    private long senastUppdaterad = 0; 
    private float senaste_x, senaste_y, senaste_z; 
    private static final int RORELSEGRANS = 20; 

    //I denna onCreate-metod startas accelerometern och tilldelas en lyssnare, tidtagningens metod raknaTid() anropas.
    //(Android Developers, u. a. a., Android Developers, u. a. b., Govender, 2014, Stack Overflow, 2013, se referenslista i rapport).

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        tidtagning = (TextView) findViewById(R.id.tidtagare);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        raknaTid();

    }

    //Metod for att starta tidrakning. Starttiden anges forst i millisekunder for att 
    //senare anvandas i interfacet Runnable, som i sin tur anropas genom denna metod.
    //Handler-klassens metod postDelayed() startar Runnable-interfacet (uppdateraTidtagare) 
    //efter ett visst antal millisekunder, i detta fall omedelbart (0 millisekunder).
    //(Singh, u. a., Oracle, u. a., se referenslista i rapport).

    public void raknaTid() {
        starttid = SystemClock.elapsedRealtime();
        handler.postDelayed(uppdateraTidtagare, 0);


    }

    //Java-interfacet Runnable som hanterar simultana tradar. Innehaller den abstrakta metoden "run()" som
    //i det har fallet anvands for att hantera tidraknaren genom att anvanda systemets klocka och mata
    //passerad tid i millisekunder och darmed rakna ut motsvarande sekunder och minuter och slutligen
    //konkatenera dessa variabler for att visas i textvyn. Slutligen anropas interfacet igen med hjalp
    //av Handler-objektet for att fortsatta halla traden aktiv.
    //(Singh, u. a., Oracle, u. a., se referenslista i rapport).

    public Runnable uppdateraTidtagare = new Runnable() {
        @Override
        public void run() {
            tidIMillisekunder = SystemClock.elapsedRealtime() - starttid;
            sekunder = (int) (tidIMillisekunder / 1000);
            minuter = sekunder / 60;
            timmar = minuter / 60;
            minuter = minuter % 60;
            sekunder = sekunder % 60;
            millisekunder = (int) (tidIMillisekunder % 1000);
            tidtagning.setText("" + String.format("%02d", timmar) + ":" + String.format("%02d", minuter) + ":" + String.format("%02d", sekunder) + ":" + String.format("%03d", millisekunder));
            handler.postDelayed(this, 0);
        }
    };

    //onClickListener-liknande metod bestaende av nastlade if-satser som anvands for att 
    //detektera accelerometerns rorelser. Pa grund av sin komplexitet kommer den forklaras
    //narmre stycke for stycke, da sammanfattad forklaring riskerar att bli for otydlig.
    //(Android Developers, u. a. a., Android Developers, u. a. b., Govender, 2014, Stack Overflow, 2013, se referenslista i rapport).

    @Override
    public void onSensorChanged(SensorEvent event) {
        
        //Den forsta if-satsens villkor uppfylls om accelerometerns sensor 
        //ar aktiv, vilket den ar sedan onCreate-metoden. Darfor lagras 
        //accelerometerns varde for x-, y-, och z-axlarna samt den aktuella 
        //tidpunkten i millisekunder i variablar.

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            aktuellTid = SystemClock.elapsedRealtime();

            //Nasta if-sats raknar ut huruvida axlarnas varde forandras och ar applikationens mest komplexa funktionalitet.
            //Denna sats exekveras ungefar var hundrade millisekund da villkoret som startar satsen ar att differensen mellan
            //den aktuella tid som angetts ovan och den tidpunkt nar satsen senast exekverades overstiger 100. Dessa variabler
            //anvands sedan lopande genom att variabeln passeradTid tilldelas den mellanskillnad av millisekunder som sker
            //mellan den aktuella tiden som hamtas ovan i aktuellTid och den senast uppmatta tiden som sparas i senastUppdaterad.
            //Klassen Math och metoden abs() anvands for att fa fram det absoluta vardet ifran axlarnas sammanlagda positioner 
            //minus axlarnas senast uppmatta positioner, vilket i oforandrat tillstand givetvis ar 0 men omedelbart andras om
            //telefonen flyttas. Detta varde delas sedan i differensen av passerade millisekunder och multipliceras med tiotusen
            //da accelerometern mater i en enhet motsvarande g-krafter, vilket aven under normala forhallanden detekterar valdigt 
            //sma forandringar. Exempelvis mats den konstanta kraften ifran gravitationen langs z-axeln om telefonen ligger ner
            //plant, vilket innebar att detektorn hade utlosts omedelbart i nasta sats om villkoret bara var att axlarnas varde
            //ar storre en 0. Denna ekvation sakerstaller alltsa att det sammanlagda vardet for axlarna ar 0 sa lange telefonen
            //inte utsatts for rorelser och ger samtidigt ett multiplicerat tal som kan jamforas mot konstanten RORELSEGRANS i
            //nasta if-sats. Slutligen sparas axlarnas nuvarande positioner for att aterigen anvandas i ekvationen nasta gang
            //satsen exekveras, det vill saga om ungefar hundra millisekunder.

            if ((aktuellTid - senastUppdaterad) > 100) {
                passeradTid = (aktuellTid - senastUppdaterad);
                senastUppdaterad = aktuellTid;

                float forandratSensorillstand = Math.abs(x + y + z - senaste_x - senaste_y - senaste_z) / passeradTid * 10000;
                senaste_x = x;
                senaste_y = y;
                senaste_z = z;

                //Villkoret i metodens sista if-sats uppfylls om det sammanlagda vardet for axlarnas forandrade tillstand
                //overstiger det troskelvarde som tilldelats konstanten RORELSEGRANS i borjan av klassen. Da denna tilldelats
                //ett lagt varde, 20, ar telefonen mycket kanslig for rorelser och overstiger detta varde redan vid sma forandringar.
                //Nar konstanten overstiger vardet inaktiveras bade accelerometern och traden for tidtagning som startats i interfacet,
                //och applikationens sista klass startas. Tidtagarens aktuella variabler sparas som en strang och skickas med som extras
                //till nasta aktivitet.

                if (forandratSensorillstand > RORELSEGRANS) {
                    senSensorManager.unregisterListener(this);
                    handler.removeCallbacks(uppdateraTidtagare);
                    Intent intent = new Intent(this, com.ollestark.relinquo.GameOver.class);
                    String resultat = ""+timmar+":"+minuter+":"+ String.format("%02d", sekunder) + ":" + String.format("%03d", millisekunder);
                    intent.putExtra("Resultatet", resultat);
                    startActivity(intent);
                    finish();

                }
                
            }

        }

    }


    //Obligatiorisk metod som autogenereras och maste finnas med nar accelerometern importeras.
    //(Android Developers, u. a. a., Android Developers, u. a. b., se referenslista i rapport).
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

