package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Compartir extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compartir);
        Button btnEnviarEv = findViewById(R.id.btnEnviarEv);
        EditText etCompEv = findViewById(R.id.etAsuntoCompartir);

        //utilizamos un textwatcher para revisar cuando la prsona este escribiendo (con after)
        // y activar el boton de enviar
        etCompEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //si el edit  text no esta vacio, activamos el boton
                if(!etCompEv.getText().toString().trim().isEmpty()){
                    btnEnviarEv.setEnabled(true);
                } else {
                    btnEnviarEv.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        //ahora le decimos a enviar lo que tiene que hacer
        btnEnviarEv.setOnClickListener(v ->{
            String asunto = etCompEv.getText().toString().trim();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain"); // se envia texto
            i.putExtra(Intent.EXTRA_TEXT, asunto); //le pasamos lo que se ha escrito en el edit txt
            i.setPackage("com.whatsapp"); //identificador de whap en android, lo abre directo
           //encerramos el intent en un try catch por si ka persona no tiene WHA instalado
            try {
               startActivity(i); //si la tiene, lo lleva
           }catch (ActivityNotFoundException WhaEx){
                //pero si no  la tiene lo lleva al market de google a descargar
                //el URI es e enlace que deja descargar wha y el id del  wha en package android
                Intent iDescargaWha = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp"));
                startActivity(iDescargaWha);
           }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.share), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}