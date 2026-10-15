package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmarReunion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmar_reunion);

        TextView txtOk = findViewById(R.id.txtConf);
        Intent intent = new Intent();
        EditText etNomCliente = findViewById(R.id.etEscribirCliente2);
        String nombre = etNomCliente.getText().toString().trim();
   // intent.putExtra("Resultado",txtOk.setText("De acuerdo %s, usted ha ACEPTADO/CANCELADO la reunión."));
     setResult(RESULT_OK, intent);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confReunion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}