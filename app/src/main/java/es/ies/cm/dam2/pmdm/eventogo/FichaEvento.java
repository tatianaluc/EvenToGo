package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FichaEvento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_evento);

        Intent intent = getIntent();

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvTipo = findViewById(R.id.tvTipo);
        TextView tvCliente = findViewById(R.id.tvCliente);
        TextView tvAsistentes = findViewById(R.id.tvAsistentes);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvServicios = findViewById(R.id.tvServ);
        TextView tvAV = findViewById(R.id.tvAvs);

        tvNombre.setText(intent.getStringExtra("nombre"));
        tvTipo.setText(intent.getStringExtra("tipo"));
        tvCliente.setText(intent.getStringExtra("cliente"));
        tvAsistentes.setText(String.valueOf(intent.getIntExtra("asistentes", 0)));
        tvFecha.setText(intent.getStringExtra("fecha"));
        tvServicios.setText(String.valueOf(intent.getIntExtra("servicios", 1)));
        tvAV.setText(intent.getBooleanExtra("incluyeAV", true) ? "Sí" : "No");

        tvNombre.setText("Nombre del evento: " + intent.getStringExtra("nombre"));
        tvFecha.setText("Fecha de evento: " + intent.getStringExtra("fecha"));
        tvTipo.setText("Tipo de evento: " + intent.getStringExtra("tipo"));
        tvCliente.setText("Nombre del cliente/empresa: " + intent.getStringExtra("cliente"));
        tvAsistentes.setText("Cantidad de asistentes: " + intent.getIntExtra("asistentes", 0));
        tvServicios.setText("Cantidad de servicios: " + intent.getIntExtra("servicios", 1));
        tvAV.setText("Incluir AVs en presupuesto: " + (intent.getBooleanExtra("incluyeAV", true) ? "Sí" : "No"));

        //boton siguiente que pasa a crear la reunion
        /*
        En la practica se pide en el main, pero se añade a esta ficha porque
        dentro de esta aplicación, solo tiene sentido uan reunion si el cliente
        de verdad esta interesado en el evento y en el presupuesto.
         */
        Button btnSiguiente = findViewById(R.id.btnSig);
        btnSiguiente.setOnClickListener(v -> startActivity(new Intent(this, Reunion.class)));

        // ATRAS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fichaEvento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}