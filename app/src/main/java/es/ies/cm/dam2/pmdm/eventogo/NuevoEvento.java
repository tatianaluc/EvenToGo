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

public class NuevoEvento extends AppCompatActivity {

    private int contadorInv = 0;
    private TextView txtInvitados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_evento);
        Button btnRestInv = findViewById(R.id.btnRestInv);
        Button btnSumInv = findViewById(R.id.btnSumInv);
        Button btnAtras = findViewById(R.id.btnAtras);
        Button btnSig = findViewById(R.id.btnSiguiente);
        //restar invitados
        txtInvitados = findViewById(R.id.txtInvitados);
        btnRestInv.setOnClickListener(v -> {
            contadorInv--;
            txtInvitados.setText("Número de invitados: " + contadorInv);
        });

        //sumar invitados
        btnSumInv.setOnClickListener((v -> {
            contadorInv++;
            txtInvitados.setText("Número de invitados: " + contadorInv);
        }));

    btnAtras.setOnClickListener(v -> {Intent i = new Intent(NuevoEvento.this, MainActivity.class);
        startActivity(i);
    });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}