package es.ies.cm.dam2.pmdm.eventogo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
            /* Para poder evitar que el usuario escoja una fecha pevia al dia de hoy, le decimos al date picker
            que recoja la fecha minima, que será la que esta denteo de sus parentesis currentime en milisegundos.
            Y de esta manera no da opción a seleccionar antes de dicha fecha, aparecen las previas en gris.
             */
            calendario.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // - 1000 para evitar que se choque con otas versiones de Android
            calendario.show();
        });

        // horas del evento, en cada campo se abre el reloj, como con la fecha
        EditText etHoraInicio = findViewById(R.id.etHoraInicio);
        EditText etHoraFin = findViewById(R.id.etHoraFin);
        etHoraInicio.setOnClickListener(v -> elegirHora(etHoraInicio));
        etHoraFin.setOnClickListener(v -> elegirHora(etHoraFin));

        //spinner d elos tipos de eventos
        Spinner spTipoEvento = findViewById(R.id.spTipoEvento);
        // con el arrayadaptar de una secuencia de caracteres, porque no se puede strings
        //llamamos al resource que es el string-array de strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tipo_evento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //y lo añadimos al spinner drop down
        spTipoEvento.setAdapter(adapter);

        // spinner del lugar, mismo sistema que el del tipo de evento pero con el array "lugares"
        Spinner spLugar = findViewById(R.id.spLugar);
        ArrayAdapter<CharSequence> adapterLugar = ArrayAdapter.createFromResource(
                this, R.array.lugares, android.R.layout.simple_spinner_item);
        adapterLugar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLugar.setAdapter(adapterLugar);

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
            etFecha.setText("");
            //vacía las horas y deja el lugar en "Selecciona"
            etHoraInicio.setText("");
            etHoraInicio.setError(null);
            etHoraFin.setText("");
            etHoraFin.setError(null);
            spLugar.setSelection(0);
        });

        // En modo ediciñon, si nos mandan un Evento, rellenamos el formulario con sus datos
        Evento editar = (Evento) getIntent().getSerializableExtra(Evento.EXTRA);
        int posicion = getIntent().getIntExtra(Coleccion.EXTRA_POSICION, -1);

        if (editar != null) {
            etNombre.setText(editar.getNombre());
            etCliente.setText(editar.getCliente());
            etAsistentes.setText(String.valueOf(editar.getAsistentes()));
            etFecha.setText(editar.getFecha());
            etHoraInicio.setText(editar.getHoraInicio());
            etHoraFin.setText(editar.getHoraFin());
            cbConAv.setChecked(editar.isIncluyeAV());
            contServicios = editar.getServicios();
            txtNumServicios.setText(String.valueOf(contServicios));
            // el spinner: buscamos en que posición esta el tipo del evento y lo seleccionamos
            for (int i = 0; i < spTipoEvento.getCount(); i++) {
                if (spTipoEvento.getItemAtPosition(i).toString().equals(editar.getTipo())) {
                    spTipoEvento.setSelection(i);
                }
            }
            // y lo mismo con el lugar
            for (int i = 0; i < spLugar.getCount(); i++) {
                if (spLugar.getItemAtPosition(i).toString().equals(editar.getLugar())) {
                    spLugar.setSelection(i);
                }
            }
        }

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

            // las dos horas son obligatorias
            if (etHoraInicio.getText().toString().isEmpty()) {
                etHoraInicio.setError("Elige la hora de inicio");
                ok = false;
            }
            if (etHoraFin.getText().toString().isEmpty()) {
                etHoraFin.setError("Elige la hora de fin");
                ok = false;
            }
            //para que la hora de inicio no se igual a la defin
            if (!etHoraInicio.getText().toString().isEmpty()
                    && etHoraInicio.getText().toString().equals(etHoraFin.getText().toString())) {
                etHoraFin.setError("La hora de fin no puede ser igual a la de inicio");
                ok = false;
            }
            // y el lugar no puede quedarse en la opcion por defecto de "Selecciona"
            if (spLugar.getSelectedItemPosition() == 0) {
                ok = false;
            }

            if (!ok) {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                return;
            }

            // montamos el Evento entero con los datos del formulario
            Evento evento = new Evento(
                    etNombre.getText().toString().trim(),
                    spTipoEvento.getSelectedItem().toString(),
                    etCliente.getText().toString().trim(),
                    Integer.parseInt(etAsistentes.getText().toString().trim()),
                    etFecha.getText().toString(),
                    etHoraInicio.getText().toString(),
                    etHoraFin.getText().toString(),
                    contServicios,
                    cbConAv.isChecked(),
                    spLugar.getSelectedItem().toString());

            if (editar == null) {
                // evento nuevo  lo guardamos en la bd, que nos devuelve el id que le ha puesto, o -1 si falla
                long idNuevo = new EventosDB(this).insertarEvento(evento);
                if (idNuevo == -1) {
                    Toast.makeText(this, "No se ha podido guardar el evento", Toast.LENGTH_SHORT).show();
                    return;
                }
                evento.setId((int) idNuevo); // así luego se podrá editar o borrar por su id
            } else {
                // editando se conserva el id del original
                evento.setId(editar.getId());
            }

            if (getCallingActivity() != null) {
                // nos ha abierto Coleccion esperando respuesta: devolvemos el evento y su posición
                Intent data = new Intent();
                data.putExtra(Evento.EXTRA, evento);
                data.putExtra(Coleccion.EXTRA_POSICION, posicion); // -1 si es un evento nuevo
                setResult(RESULT_OK, data);
                finish();
            } else {
                // nos han abierto desde otro sitio (como en la 4): vamos a la ficha
                Intent i = new Intent(this, FichaEvento.class);
                i.putExtra(Evento.EXTRA, evento);
                startActivity(i);
            }
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

    // Abre el reloj para elegir una hora y la escribe en el EditText con formato HH:mm
    private void elegirHora(EditText etHora) {
        Calendar ahora = Calendar.getInstance(); // la hora actual es la que sale al abrir el reloj
        TimePickerDialog reloj = new TimePickerDialog(this,
                (view, hora, minuto) -> {
                    etHora.setText(String.format("%02d:%02d", hora, minuto)); // %02d para que salga 00:00 y no 0:0
                    etHora.setError(null);
                },
                ahora.get(Calendar.HOUR_OF_DAY), ahora.get(Calendar.MINUTE), true); // formato de 24 horas
        reloj.show();
    }


}