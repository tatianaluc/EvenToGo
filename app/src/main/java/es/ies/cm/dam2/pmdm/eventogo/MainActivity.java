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

public class MainActivity extends AppCompatActivity {
    private int contador = 0;
    private TextView txtContador;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnNuevoEvento = findViewById(R.id.btnNuevoEvento);
        txtContador = findViewById(R.id.txtContador);
        Button btnReg = findViewById(R.id.btnReg);

        //incrementar eventos y cambiar de ventana con el intent
        btnNuevoEvento.setOnClickListener(v -> {
            contador++;
            txtContador.setText("Eventos Registrados: " + contador);
            Intent intent = new Intent(MainActivity.this, NuevoEvento.class);
            startActivity(intent);
        });

        Button btnReset = findViewById(R.id.btnReset);
        //resetear contador a 0
        btnReset.setOnClickListener(v -> {
            contador = 0;
            txtContador.setText("Eventos Registrados: " + contador);
        });

        // Filtrado de boton para pasar directamente a eventos registrados
        btnReg.setOnClickListener(v->{
            Intent i = new Intent(MainActivity.this, NumeroEventos.class);
            startActivity(i);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}