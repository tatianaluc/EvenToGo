package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmarReunion extends AppCompatActivity {

    // clave para devolver la respuesta a la pantalla Reunion
    public static final String EXTRA_ASISTENCIA = "asistencia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmar_reunion);

        Button btnAceptar = findViewById(R.id.btnAceptar);
        Button btnCancelar = findViewById(R.id.btnCancelar);
        Button btnAtras = findViewById(R.id.btnAtras);

        // cada boton devuelve la respuesta a reunion y de ahi llamamos lo que haya escogido
        btnAceptar.setOnClickListener(v -> responder("ACEPTADO"));
        btnCancelar.setOnClickListener(v -> responder("CANCELADO"));

        // bton atras
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confReunion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void responder(String respuesta) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ASISTENCIA, respuesta); // metemos la respuesta en el extra
        setResult(RESULT_OK, data);                  // y antes de cerrarvolvemos a la primera pantalla
        finish();
    }
}