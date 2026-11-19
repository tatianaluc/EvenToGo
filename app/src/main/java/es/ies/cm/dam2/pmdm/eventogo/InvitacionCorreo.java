package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InvitacionCorreo extends ActividadConBarra {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invitacion_correo);
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etAsunto = findViewById(R.id.etAsunto);
        EditText etTexto = findViewById(R.id.etCuerpo);
        Button btnCompartir = findViewById(R.id.btnCompartir);

        btnCompartir.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            // "mailto:" hace que solo respondan las apps de correo
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{etCorreo.getText().toString().trim()});
            intent.putExtra(Intent.EXTRA_SUBJECT, etAsunto.getText().toString().trim());
            intent.putExtra(Intent.EXTRA_TEXT, etTexto.getText().toString().trim());
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.sin_app_correo, Toast.LENGTH_SHORT).show();
            }
        });

        // ATRAS: cierra esta pantalla y vuelve a la reunión
        Button btnAtras = findViewById(R.id.btnAtrasCorreo);
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.correo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}