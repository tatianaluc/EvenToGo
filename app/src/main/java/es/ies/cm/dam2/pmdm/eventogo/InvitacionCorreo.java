package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InvitacionCorreo extends AppCompatActivity {

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
            intent.setData(Uri.parse("Compartir a:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{etCorreo.getText().toString().trim()});
            intent.putExtra(Intent.EXTRA_SUBJECT, etAsunto.getText().toString().trim());
            intent.putExtra(Intent.EXTRA_TEXT, etTexto.getText().toString().trim());
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Debe tener una app de correo", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.correo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}