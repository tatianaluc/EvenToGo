package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NuevoEvento extends AppCompatActivity {
    private int contServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_evento);
        //Empezamos con el spinner d elos tipos de eventos
        Spinner spTipoEvento = findViewById(R.id.spTipoEvento);
        // con el arrayadaptar de una secuencia de caracteres, porque no se puede strings
        //llamamos al resource que es el string-array de strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tipo_evento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //y lo añadimos al spinner drop down
        spTipoEvento.setAdapter(adapter);

        // servicios a presupuestar
        Button btnAumentar = findViewById(R.id.btnSumServ);
        Button btnDisminuir = findViewById(R.id.btnRestServ);
        TextView txtNumServicios = findViewById(R.id.txtCantServ);

        btnAumentar.setOnClickListener(v -> {
            if (contServicios <= 10) { // mientras sea menor o igual a 10, aumentsr
                contServicios++;
                txtNumServicios.setText(String.valueOf(contServicios));
            }
        });

        btnDisminuir.setOnClickListener(v -> {
            if (contServicios > 1) { //minimo 1 servicio
                contServicios--;
                txtNumServicios.setText(String.valueOf(contServicios));
            }
        });

        //Desmarcar checkbox que incluye AVs en presu
        CheckBox cbConAv = findViewById(R.id.cbConAv);
        Button btnOwnAv = findViewById(R.id.btnOwnAv);
        TextView txtAv = findViewById(R.id.txtNoAv);

        cbConAv.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnOwnAv.setEnabled(!isChecked);
        });

        btnOwnAv.setOnClickListener(v -> {
            txtAv.setText("Sin AVs.");
        });

        //llamamos al método para comprobar los asistentes
        EditText etAsistentes = findViewById(R.id.etNumInvitados);
        etAsistentes.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) cantidadAsistentes(etAsistentes);
        });

        //RESET, pasarlo todo a 0
        EditText etNombre = findViewById(R.id.etEscribirNombre);
        EditText etCliente = findViewById(R.id.etEscribirCliente);
        Button btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> {
            //los edit los pone en nada
            etNombre.setText("");
            etCliente.setText("");
            etAsistentes.setText("");
            etAsistentes.setError(null);
            //el spinner lo deja en seleccionar que es la posicion 0
            spTipoEvento.setSelection(0);
            //el contador de serv lo devuelve a 1
            contServicios = 1;
            txtNumServicios.setText("1");
            //y vuelve al check el incluir AVs
            cbConAv.setChecked(true);
        });

        // ATRAS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nuevoEvento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //ahora nos amos a crear un método especial para los asistentes
    private boolean cantidadAsistentes(EditText etAsistentes) {
        String texto = etAsistentes.getText().toString().trim();
        if (texto.isEmpty()) { // si lo deja vacío, le obligamos a que introduzca número
            etAsistentes.setError("Obligatorio"); //y utilizamos error para que aparezca en rojo
            return false;
        }
        int asistentes = Integer.parseInt(texto); //lo pasamos a txt porque es un edit text
        if (asistentes < 5 || asistentes > 300) { // y setteamos el mínimo a 5
            etAsistentes.setError("min. 5 y max. 300"); //y utilizamos error para que aparezca en rojo
            return false;
        }
        return true;
    }
}