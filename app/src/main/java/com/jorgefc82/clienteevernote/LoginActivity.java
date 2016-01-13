package com.jorgefc82.clienteevernote;
/**
 * Created by Jorgefc82.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

/*Se implementa interface EvernoteLoginFragment.ResultCallback*/
public class LoginActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback{

    /*Se definen credenciales de la API generada*/
    private static final String CONSUMER_KEY = "jorgefc82-0043";
    private static final String CONSUMER_SECRET = "f85a5208054f7213";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final String TAG_AUTENTIFICACION = "Log autentificación";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button boton_login = (Button) findViewById(R.id.btn_login);
        //Se inicializa EvernoteSesión con los datos necesarios en onCreate */
        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
        //Se llama a método que comprobará si ya se está autentificado*/
        compruebaAutentificacion();
        //Botón login lanzará autentificación de EvernoteSession*/
        boton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAutentificacion();
            }
        });
    }

    /*Se sobreescribe método  para determinar comportamiento cuando el login haya finalizado */
    @Override
    public void onLoginFinished(boolean successful) {
        if(successful) {
            //Se muestra Toast de autentificación correcta
            Toast autentificado =
                    Toast.makeText(getApplicationContext(),
                            R.string.autentificado
                            , Toast.LENGTH_SHORT);

            autentificado.show();
            //Finaliza actividad
            this.finish();
            //Lanza actividad notas
            Intent notas = new Intent(this, NotasActivity.class);
            notas.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(notas);
        }else{
            //Si el resultado de la autentificación es falso se muestra Toast
            Toast error_acceso_aut =
                    Toast.makeText(getApplicationContext(),
                            R.string.error_acceso_aut
                            , Toast.LENGTH_LONG);
            error_acceso_aut.show();
        }
    }

    /*Método que comprueba si se está logueado y determina comportamiento*/
    private void compruebaAutentificacion(){
    //Si se está logueado se pasa true a método onLoginFinished*/
        if(EvernoteSession.getInstance().isLoggedIn()) {
            onLoginFinished(true);
        }
    }
    /*Método para lanzar autentificación*/
    private void getAutentificacion (){
        try {
            EvernoteSession.getInstance().authenticate(LoginActivity.this);
         }catch (Exception e) {
            Log.e(TAG_AUTENTIFICACION, "Error al instanciar autentificación");
        }
    }
}
