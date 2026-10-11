package es.ies.cm.dam2.pmdm.eventogo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class NuevoEvento extends AppCompatActivity {
    private int contServicios = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_evento);
        //layout de calendario para la fecha del evento
        EditText etFecha = findViewById(R.id.etFechaEvento);
        etFecha.setOnClickListener(v -> {
            Calendar hoy = Calendar.getInstance(); //fecha de hoy: es la que sale seleccionada al abrir el calendario
            DatePickerDialog calendario = new DatePickerDialog(this,
                    (view, anio, mes, dia) -> //y lo que recibe el método
                            //formateamos de enteros a texto para que se muestre en el edit text
                            //importante aclarar el mes + 1 porque empiezan por defecto en 0
                    {
                        etFecha.setText(String.format("%02d/%02d/%d", dia, mes + 1, anio));
                        etFecha.setError(null);
                    },
                    hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));
            calendario.show();
        });

        //spinner d elos tipos de eventos
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
            if (contServicios < 10) { // máximo 10 servicios
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
            //vuelve a mostrar dd//mm/7aaaa
            etFecha.setText(etFecha.getHint());
        });

        // ENVIAR, comprobamos todos los campos
        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(v -> {
            boolean ok = true; // si algún campo falla, pasa a false
            //nombre del evento
            if (etNombre.getText().toString().trim().isEmpty()) {
                etNombre.setError("Campo Obligatorio");
                ok = false;
            }
            //debe seleccionar un  tipo de vento en el spinner
            if (spTipoEvento.getSelectedItemPosition() == 0) { // 0 = "Selecciona tipo"
                ok = false;
            }
            //escribir el nombre del client o empresa que organiza
            if (etCliente.getText().toString().trim().isEmpty()) {
                etCliente.setError("Campo Obligatorio");
                ok = false;
            }
            // si se pasa del rango
            if (!cantidadAsistentes(etAsistentes)) {
                ok = false;
            }
            // para que no deje la fecha vací
            if (etFecha.getText().toString().isEmpty()) {
                etFecha.setError("Elige una fecha");
                ok = false;
            }

            if (!ok) {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                return;
            }
            // Si todo sale bien, se genera el intent de pasar a la ficha al hacer click en enviar
            Intent i = new Intent(this, FichaEvento.class);
            // los valores que se van a guardar en la ficha
            i.putExtra("nombre", etNombre.getText().toString().trim());
            i.putExtra("tipo", spTipoEvento.getSelectedItem().toString());
            i.putExtra("cliente", etCliente.getText().toString().trim());
            i.putExtra("asistentes", Integer.parseInt(etAsistentes.getText().toString().trim()));
            i.putExtra("fecha", etFecha.getText().toString());
            i.putExtra("servicios", contServicios);
            i.putExtra("incluyeAV", cbConAv.isChecked());
            startActivity(i);
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

    //ahora nos vamos a crear un método especial para los asistentes que usaremos para comprobar
    private boolean cantidadAsistentes(EditText etAsistentes) {
        String texto = etAsistentes.getText().toString().trim();
        if (texto.isEmpty()) { // si lo deja vacío, le obligamos a que introduzca número
            etAsistentes.setError("Campo Obligatorio"); //y utilizamos error para que aparezca en rojo
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