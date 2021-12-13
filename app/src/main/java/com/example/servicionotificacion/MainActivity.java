package com.example.servicionotificacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private PendingIntent pendingIntent;
    private PendingIntent DetenerReproduccionPending;
    private PendingIntent IniciarReproduccionPending;
    private final static String CanalID = "NOTIFICACION";
    private final static int NotificacionID = 0;
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button arrancar = (Button) findViewById(R.id.boton_arrancar);
        arrancar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Crea el servicio
                startService(new Intent(MainActivity.this, ServicioMusica.class));
                iniciarReproduccion();
                detenerReproduccion();
                pulsarNotificacion();
                crearCanalNotificacion();
                crearNotificacion();
            }
        });

        Button detener = (Button) findViewById(R.id.boton_detener);
        //Detiene el servicio
        detener.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, ServicioMusica.class));
            }
        });

    }
    //Que hacer cuando pulsamos la opcion de iniciar en la notificacion
    private void iniciarReproduccion(){
        startService(new Intent(MainActivity.this, ServicioMusica.class));
    }

    //Que hacer cuando pulsamos la opcion de detener en la notificacion
    private void detenerReproduccion(){
        stopService(new Intent(MainActivity.this, ServicioMusica.class));
    }

    //Para donde nos manda una vez que pulsemos la notificacion
    private void pulsarNotificacion(){
        Intent intent = new Intent(this, ServicioMusica.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ServicioMusica.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    //para comprobar que se pueda ejercutar en la version, si no, no se ejecuta
    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence nombre = "Noticacion";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificacioncanal = new NotificationChannel(CanalID,nombre,importancia);
            NotificationManager notificacionadmin = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificacionadmin.createNotificationChannel(notificacioncanal);
        }
    }

    private void crearNotificacion() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CanalID)
                //parametros de la notificacion
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notificacion Servicio")
                .setContentText("Servicio Arrancado")
                .setColor(Color.MAGENTA)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                //añado accion que pueda realizarse cuando se pulse la notificacion
                .addAction(R.drawable.ic_launcher_foreground,"Iniciar",IniciarReproduccionPending)
                .addAction(R.drawable.ic_launcher_foreground,"Detener",DetenerReproduccionPending);

        //para activar la notificacion
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NotificacionID, builder.build());
    }
}