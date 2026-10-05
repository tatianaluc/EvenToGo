package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
            if (contadorInv > 0) {
                contadorInv--;
                txtInvitados.setText("Número de invitados: " + contadorInv);
            }

        });

        //sumar invitados
        btnSumInv.setOnClickListener((v -> {
            contadorInv++;
            txtInvitados.setText("Número de invitados: " + contadorInv);
        }));

        //Guardar nombre del evento
        EditText etEscribirNombre = findViewById(R.id.etEscribirNombre);
        TextView txtMostrarNombre = findViewById(R.id.txtMostrarNombre);
        Button btnGuardarNombre = findViewById(R.id.btnGuardarNombre);

        btnGuardarNombre.setOnClickListener(v -> {
            String nombreEv = etEscribirNombre.getText().toString();
            txtMostrarNombre.setText("Evento: " + nombreEv + " guardado.");
            etEscribirNombre.setText("");
        });

        //Desmarcar checkbox que incluye AVs en presu
        CheckBox cbConAv = findViewById(R.id.cbConAv);
        Button btnOwnAv = findViewById(R.id.btnOwnAv);
        TextView txtAv = findViewById(R.id.txtAv);

        cbConAv.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnOwnAv.setEnabled(!isChecked);
        });

        btnOwnAv.setOnClickListener(v -> {
            txtAv.setText("Botón sin AVs pulsado.");
        });

        // ATRAS
        btnAtras.setOnClickListener(v -> finish());

        // SIGUIENTE
        btnSig.setOnClickListener(v -> {
            Intent intent = new Intent(NuevoEvento.this, NumeroEventos.class);
            intent.putExtra("nombreEvento", txtMostrarNombre.getText().toString());
            intent.putExtra("numInvitados", contadorInv);
            intent.putExtra("incluyeAV", cbConAv.isChecked());
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}