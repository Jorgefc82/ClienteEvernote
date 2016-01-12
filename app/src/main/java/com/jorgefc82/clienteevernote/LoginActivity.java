package com.jorgefc82.clienteevernote;
/**
 * Created by Jorgefc82.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

/*Se implementa interface EvernoteLoginFragment.ResultCallback*/
public class LoginActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback{

    /*Se definen credenciales de la API generada*/
    private static final String CONSUMER_KEY = "jorgefc82-0043";
    private static final String CONSUMER_SECRET = "f85a5208054f7213";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button boton_login = (Button) findViewById(R.id.btn_login);
        /*Se inicializa EvernoteSesión con los datos necesarios en onCreate */
        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
        /*Si ya se está logueado se oculta botón login por estética y se pasa true
        * a método onLoginFinished*/
                if(EvernoteSession.getInstance().isLoggedIn()){
                    boton_login.setVisibility(View.GONE);
                    onLoginFinished(true);
                }
        boton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            /*Botón login lanzará autentificación de EvernoteSession*/
            public void onClick(View v) {
                EvernoteSession.getInstance().authenticate(LoginActivity.this);
            }
        });
    }

    /*Se sobreescribe método  para determinar comportamiento cuando el login haya finalizado */
    @Override
    public void onLoginFinished(boolean successful) {
        if(successful) {
            this.finish();
            Intent notas = new Intent(this, NotasActivity.class);
            notas.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(notas);
        }
    }
}
