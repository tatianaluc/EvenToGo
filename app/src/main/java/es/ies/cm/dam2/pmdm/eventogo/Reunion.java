package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Reunion extends AppCompatActivity {

    // Clave del extra con el id del evento al que pertenece la reunión
    public static final String EXTRA_EVENTO_ID = "eventoId";
    // Claves de lo que se guarda al girar la pantalla
    private static final String ESTADO_MENSAJE = "mensajeConfirmacion";
    private static final String ESTADO_INVITAR = "invitarVisible";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reunion);

        // Id del evento al que pertenece la reunión (viene de la ficha). -1 si no llega
        int eventoId = getIntent().getIntExtra(EXTRA_EVENTO_ID, -1);

        EditText etNomCliente = findViewById(R.id.etEscribirCliente2);
        TextView txtConf = findViewById(R.id.txtConf);
        Button btnInvitar = findViewById(R.id.btnInvitar);

        //Fecha de la reunión: calendario en un DialogFragment propio (SelectorFechaDialog)
        EditText etFechaReu = findViewById(R.id.etFechaReu);
        // Aquí llega la fecha cuando se acepta el calendario. Se registra en onCreate para que no se pierda al girar
        getSupportFragmentManager().setFragmentResultListener(SelectorFechaDialog.CLAVE_FECHA_REUNION, this,
                (clave, resultado) -> {
                    etFechaReu.setText(resultado.getString(SelectorFechaDialog.RESULTADO_FECHA));
                    etFechaReu.setError(null);
                });
        etFechaReu.setOnClickListener(v -> SelectorFechaDialog.nuevo(SelectorFechaDialog.CLAVE_FECHA_REUNION)
                .show(getSupportFragmentManager(), "fecha_reunion"));

        //ahora escoger si presencial u online y settear un color dependiendo de lo que escoja
        RadioGroup grpLugar = findViewById(R.id.grpLugar);
        RadioButton online = findViewById(R.id.online);
        RadioButton ofi = findViewById(R.id.ofi);
        grpLugar.setOnCheckedChangeListener((group, checked) -> {
            if (checked == R.id.online) {
                online.setTextColor(ContextCompat.getColor(this, R.color.orange));
                ofi.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
            if (checked == R.id.ofi) {
                ofi.setTextColor(ContextCompat.getColor(this, R.color.red));
                online.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        });

        // declaramos el launcher que va a volver a esta pantalla desde confirmsr reunion
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // depndiendo de si escoge aceptar o cancelar, vuelve a esta pantalla
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String respuesta = result.getData()
                                .getStringExtra(ConfirmarReunion.EXTRA_ASISTENCIA);
                        String nombre = etNomCliente.getText().toString().trim();

                        // Guardamos la reunión en Room (hilo aparte), conectada al evento por su id
                        if (eventoId != -1) {
                            String fechaReunion = etFechaReu.getText().toString().trim();
                            String modalidad = grpLugar.getCheckedRadioButtonId() == R.id.online ? "Online" : "Oficina";
                            ReunionEvento reunion = new ReunionEvento(eventoId, nombre, fechaReunion, modalidad, respuesta);
                            AppDatabase.ejecutor.execute(() ->
                                    AppDatabase.getInstance(this).reunionDao().insertar(reunion));
                        }
                        txtConf.setText(getString(R.string.reunion_confirmada, nombre, Textos.estado(this, respuesta)));
                        if ("ACEPTADO".equals(respuesta)) {
                            btnInvitar.setVisibility(View.VISIBLE);
                        } else {
                            btnInvitar.setVisibility(View.GONE);
                        }
                    }
                });

        //COmprobar los campos y botón enviar
        Button btnConfirmar = findViewById(R.id.btnReunion);
        btnConfirmar.setOnClickListener(v -> {
            boolean ok = true; // si algún campo falla, pasa a false
            if (etNomCliente.getText().toString().trim().isEmpty()) {
                etNomCliente.setError(getString(R.string.campo_obligatorio));
                ok = false;
            }
            if (etFechaReu.getText().toString().trim().isEmpty()) {
                etFechaReu.setError(getString(R.string.escoja_fecha));
                ok = false;
            }
            if (grpLugar.getCheckedRadioButtonId() == -1) {
                TextView txtLugar = findViewById(R.id.txtLugar);
                txtLugar.setError(getString(R.string.debe_seleccionar));
                ok = false;
            }

            if (!ok) {
                Toast.makeText(this, R.string.completar_campos, Toast.LENGTH_SHORT).show();
                return;
            }
            // abrimos la pantalla de confirmación esperando una respuesta
            launcher.launch(new Intent(this, ConfirmarReunion.class));
        });

        //btn Invitar a reunion
        btnInvitar.setOnClickListener(v -> startActivity(new Intent(this, InvitacionCorreo.class)));

        // l mensaje de confirmación y el botón de invitar no se guardan solos, los recuperamos
        //al girar
        if (savedInstanceState != null) {
            txtConf.setText(savedInstanceState.getString(ESTADO_MENSAJE, ""));
            btnInvitar.setVisibility(savedInstanceState.getInt(ESTADO_INVITAR, btnInvitar.getVisibility()));
        }

        // ATRAS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reunion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Se ejecuta justo antes de que Android modifique la pantalla al girar
    @Override
    protected void onSaveInstanceState(@NonNull Bundle salida) {
        super.onSaveInstanceState(salida);
        TextView txtConf = findViewById(R.id.txtConf);
        Button btnInvitar = findViewById(R.id.btnInvitar);
        salida.putString(ESTADO_MENSAJE, txtConf.getText().toString());
        salida.putInt(ESTADO_INVITAR, btnInvitar.getVisibility());
    }
}
