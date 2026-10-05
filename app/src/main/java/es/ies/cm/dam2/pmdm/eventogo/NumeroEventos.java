package es.ies.cm.dam2.pmdm.eventogo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NumeroEventos extends AppCompatActivity {
    private TextView txtEvenSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_numero_eventos);
        //Llamar a los intents de la otra clase para que Siguiente funcione
        String nombreEvento = getIntent().getStringExtra("nombreEvento");
        int numInvitados = getIntent().getIntExtra("numInvitados", 0);
        boolean incluyeAV = getIntent().getBooleanExtra("incluyeAV", true);

        //Declaramos los 9 botones
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);

        //Mostramos en el txtview según el botón pulsado
        btn1.setOnClickListener(v->{
            txtEvenSave.setText(btn1 + "Evento/s Registrado/s");
        });
        btn2.setOnClickListener(v->{
            txtEvenSave.setText(btn2 + "Evento/s Registrado/s");
        });
        btn3.setOnClickListener(v->{
            txtEvenSave.setText(btn3 + "Evento/s Registrado/s");
        });
        btn4.setOnClickListener(v->{
            txtEvenSave.setText(btn4 + "Evento/s Registrado/s");
        });
        btn5.setOnClickListener(v->{
            txtEvenSave.setText(btn5 + "Evento/s Registrado/s");
        });
        btn6.setOnClickListener(v->{
            txtEvenSave.setText(btn6 + "Evento/s Registrado/s");
        });
        btn7.setOnClickListener(v->{
            txtEvenSave.setText(btn7 + "Evento/s Registrado/s");
        });
        btn8.setOnClickListener(v->{
            txtEvenSave.setText(btn8 + "Evento/s Registrado/s");
        });
        btn9.setOnClickListener(v->{
            txtEvenSave.setText(btn9 + "Evento/s Registrado/s");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}