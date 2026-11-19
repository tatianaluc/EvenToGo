package es.ies.cm.dam2.pmdm.eventogo;

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
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NuevoEvento extends ActividadConBarra {
    private int contServicios = 1;
    // Claves de lo que se guarda al girar la pantalla
    private static final String ESTADO_SERVICIOS = "contServicios";
    private static final String ESTADO_SIN_AV = "textoSinAv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_evento);
        //calendario para la fecha del evento: ahora es un DialogFragment propio (SelectorFechaDialog)
        EditText etFecha = findViewById(R.id.etFechaEvento);
        // Aquí llega la fecha cuando se acepta el calendario. Se registra en onCreate para que no se pierda al girar
        getSupportFragmentManager().setFragmentResultListener(SelectorFechaDialog.CLAVE_FECHA_EVENTO, this,
                (clave, resultado) -> {
                    etFecha.setText(resultado.getString(SelectorFechaDialog.RESULTADO_FECHA));
                    etFecha.setError(null);
                });
        etFecha.setOnClickListener(v -> SelectorFechaDialog.nuevo(SelectorFechaDialog.CLAVE_FECHA_EVENTO)
                .show(getSupportFragmentManager(), "fecha_evento"));

        // horas del evento, en cada campo se abre el reloj (SelectorHoraDialog), como con la fecha
        EditText etHoraInicio = findViewById(R.id.etHoraInicio);
        EditText etHoraFin = findViewById(R.id.etHoraFin);
        // una clave distinta para cada hora, así se sabe en qué campo escribir lo que devuelve el reloj
        getSupportFragmentManager().setFragmentResultListener(SelectorHoraDialog.CLAVE_HORA_INICIO, this,
                (clave, resultado) -> {
                    etHoraInicio.setText(resultado.getString(SelectorHoraDialog.RESULTADO_HORA));
                    etHoraInicio.setError(null);
                });
        getSupportFragmentManager().setFragmentResultListener(SelectorHoraDialog.CLAVE_HORA_FIN, this,
                (clave, resultado) -> {
                    etHoraFin.setText(resultado.getString(SelectorHoraDialog.RESULTADO_HORA));
                    etHoraFin.setError(null);
                });
        etHoraInicio.setOnClickListener(v -> SelectorHoraDialog.nuevo(SelectorHoraDialog.CLAVE_HORA_INICIO)
                .show(getSupportFragmentManager(), "hora_inicio"));
        etHoraFin.setOnClickListener(v -> SelectorHoraDialog.nuevo(SelectorHoraDialog.CLAVE_HORA_FIN)
                .show(getSupportFragmentManager(), "hora_fin"));

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

        // Los Spinner enseñan los textos traducidos (tipo_evento y lugares), pero en la bd se guarda siempre el valor en
        // español (tipo_evento_valor y lugares_valor, en la misma posición). Así la portada, que sale del tipo, y las
        // búsquedas no cambian aunque el móvil esté en otro idioma
        String[] tiposValor = getResources().getStringArray(R.array.tipo_evento_valor);
        String[] lugaresValor = getResources().getStringArray(R.array.lugares_valor);

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
            txtAv.setText(R.string.sin_avs);
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
            // el spinner: buscamos en que posición esta el tipo del evento (entre los valores guardados) y lo seleccionamos
            for (int i = 0; i < tiposValor.length; i++) {
                if (tiposValor[i].equals(editar.getTipo())) {
                    spTipoEvento.setSelection(i);
                }
            }
            // y lo mismo con el lugar
            for (int i = 0; i < lugaresValor.length; i++) {
                if (lugaresValor[i].equals(editar.getLugar())) {
                    spLugar.setSelection(i);
                }
            }
        }

        // GIRO DE PANTALLA: al girar, Android destruye la pantalla y la crea otra vez. Los EditText, el Spinner y el
        // CheckBox recuperan su estado solos porque tienen id, pero el contador de servicios (una variable) y el texto
        // de "Sin AVs." no, así que los recuperamos aquí. Va después del modo edición para que no los pise
        if (savedInstanceState != null) {
            contServicios = savedInstanceState.getInt(ESTADO_SERVICIOS, 1);
            txtNumServicios.setText(String.valueOf(contServicios));
            txtAv.setText(savedInstanceState.getString(ESTADO_SIN_AV, txtAv.getText().toString()));
        }

        // ENVIAR, comprobamos todos los campos
        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(v -> {
            boolean ok = true; // si algún campo falla, pasa a false
            //nombre del evento
            if (etNombre.getText().toString().trim().isEmpty()) {
                etNombre.setError(getString(R.string.campo_obligatorio));
                ok = false;
            }
            //debe seleccionar un  tipo de vento en el spinner
            if (spTipoEvento.getSelectedItemPosition() == 0) { // 0 = "Selecciona tipo"
                ok = false;
            }
            //escribir el nombre del client o empresa que organiza
            if (etCliente.getText().toString().trim().isEmpty()) {
                etCliente.setError(getString(R.string.campo_obligatorio));
                ok = false;
            }
            // si se pasa del rango
            if (!cantidadAsistentes(etAsistentes)) {
                ok = false;
            }
            // para que no deje la fecha vací
            if (etFecha.getText().toString().isEmpty()) {
                etFecha.setError(getString(R.string.elige_fecha));
                ok = false;
            }

            // las dos horas son obligatorias
            if (etHoraInicio.getText().toString().isEmpty()) {
                etHoraInicio.setError(getString(R.string.elige_hora_inicio));
                ok = false;
            }
            if (etHoraFin.getText().toString().isEmpty()) {
                etHoraFin.setError(getString(R.string.elige_hora_fin));
                ok = false;
            }
            //para que la hora de inicio no se igual a la defin
            if (!etHoraInicio.getText().toString().isEmpty()
                    && etHoraInicio.getText().toString().equals(etHoraFin.getText().toString())) {
                etHoraFin.setError(getString(R.string.hora_fin_igual));
                ok = false;
            }
            // y el lugar no puede quedarse en la opcion por defecto de "Selecciona"
            if (spLugar.getSelectedItemPosition() == 0) {
                ok = false;
            }

            if (!ok) {
                Toast.makeText(this, R.string.datos_incorrectos, Toast.LENGTH_SHORT).show();
                return;
            }

            // montamos el Evento entero con los datos del formulario
            Evento evento = new Evento(
                    etNombre.getText().toString().trim(),
                    tiposValor[spTipoEvento.getSelectedItemPosition()], // el valor en español que se guarda
                    etCliente.getText().toString().trim(),
                    Integer.parseInt(etAsistentes.getText().toString().trim()),
                    etFecha.getText().toString(),
                    etHoraInicio.getText().toString(),
                    etHoraFin.getText().toString(),
                    contServicios,
                    cbConAv.isChecked(),
                    lugaresValor[spLugar.getSelectedItemPosition()]);

            if (editar == null) {
                // El evento nuevo es de quien hizo login
                evento.setUsuarioId(getIntent().getIntExtra(Login.EXTRA_USUARIO_ID, -1));
                // Lo guardamos en Room, en el hilo aparte. insertar nos devuelve el id que le ha puesto
                AppDatabase.ejecutor.execute(() -> {
                    long idNuevo = AppDatabase.getInstance(this).eventoDao().insertar(evento);
                    // lo que toca pantallas se hace en el hilo principal
                    runOnUiThread(() -> {
                        evento.setId((int) idNuevo); // así luego se podrá editar o borrar por su id
                        terminar(evento, posicion);
                    });
                });
            } else {
                // editando se conserva el id y el dueño del original: el update de Room cambia todas las columnas
                // y si no, el evento se quedaría sin dueño
                evento.setId(editar.getId());
                evento.setUsuarioId(editar.getUsuarioId());
                terminar(evento, posicion);
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

    // Se ejecuta justo antes de que Android modifique la pantalla al girar: aquí guardamos lo que no se guarda solo
    @Override
    protected void onSaveInstanceState(@NonNull Bundle salida) {
        super.onSaveInstanceState(salida);
        salida.putInt(ESTADO_SERVICIOS, contServicios);
        TextView txtAv = findViewById(R.id.txtNoAv);
        salida.putString(ESTADO_SIN_AV, txtAv.getText().toString());
    }

    // método especial para los asistentes que usaremos para comprobar
    private boolean cantidadAsistentes(EditText etAsistentes) {
        String texto = etAsistentes.getText().toString().trim();
        if (texto.isEmpty()) { // si lo deja vacío, le obligamos a que introduzca número
            etAsistentes.setError(getString(R.string.campo_obligatorio)); //y utilizamos error para que aparezca en rojo
            return false;
        }
        int asistentes = Integer.parseInt(texto); //lo pasamos a txt porque es un edit text
        if (asistentes < 5 || asistentes > 300) { // y setteamos el mínimo a 5
            etAsistentes.setError(getString(R.string.rango_asistentes)); //y utilizamos error para que aparezca en rojo
            return false;
        }
        return true;
    }

    // Último paso del ENVIAR, cuando el evento ya está guardado, si nos abrió Coleccion esperando
    // respuesta se lo devolvemos con su posición, y si no, abrimos su ficha
    private void terminar(Evento evento, int posicion) {
        if (getCallingActivity() != null) {
            Intent data = new Intent();
            data.putExtra(Evento.EXTRA, evento);
            data.putExtra(Coleccion.EXTRA_POSICION, posicion); // -1 si es un evento nuevo
            setResult(RESULT_OK, data);
            finish();
        } else {
            // nos han abierto desde otro sitio. vamos a la ficha
            Intent i = new Intent(this, FichaEvento.class);
            i.putExtra(Evento.EXTRA, evento);
            startActivity(i);
        }
    }
}
