package es.ies.cm.dam2.pmdm.eventogo;

import android.app.DatePickerDialog;
import android.content.Intent;

import java.util.Calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Reunion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reunion);

        EditText etNomCliente = findViewById(R.id.etEscribirCliente2);
        TextView txtConf = findViewById(R.id.txtConf);
        Button btnInvitar = findViewById(R.id.btnInvitar);

        //Fecha de la reunión
        EditText etFechaReu = findViewById(R.id.etFechaReu);
        etFechaReu.setOnClickListener(v -> {
            Calendar hoy = Calendar.getInstance(); //Para que al abrir calendario aparezca la fecha de hoy
            //para escogerla
            DatePickerDialog escogerFecha = new DatePickerDialog(this,
                    (view, anio, mes, dia) ->  //y lo que recibe el método
                            //formateamos de enteros a texto para que se muestre en el edit text
                            //importante aclarar el mes + 1 porque empiezan por defecto en 0
                    {
                        etFechaReu.setText(String.format("%02d/%02d/%d", dia, mes + 1, anio));
                        etFechaReu.setError(null);
                    },
                    hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));
               /* Para poder evitar que el usuario escoja una fecha pevia al dia de hoy, le decimos al date picker
            que recoja la fecha minima, que será la que esta denteo de sus parentesis currentime en milisegundos.
            Y de esta manera no da opción a seleccionar antes de dicha fecha, aparecen las previas en gris.
             */
            escogerFecha.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // - 1000 para evitar que se choque con otas versiones de Android
            escogerFecha.show();
        });

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
                        txtConf.setText("De acuerdo " + nombre + ", usted ha " + respuesta
                                + " la reunión con el project manager.");
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
                etNomCliente.setError("Campo Obligatorio.");
                ok = false;
            }
            if (etFechaReu.getText().toString().trim().isEmpty()) {
                etFechaReu.setError("Escoja una fecha.");
                ok = false;
            }
            if (grpLugar.getCheckedRadioButtonId() == -1) {
                TextView txtLugar = findViewById(R.id.txtLugar);
                txtLugar.setError("Debe seleccionar una opción.");
                ok = false;
            }

            if (!ok) {
                Toast.makeText(this, "Debe completar los campos\nantes de confirmar la reunión", Toast.LENGTH_SHORT).show();
                return;
            }
            // abrimos la pantalla de confirmación esperando una respuesta
            launcher.launch(new Intent(this, ConfirmarReunion.class));
        });

        //btn Invitar a reunion
        btnInvitar.setOnClickListener(v -> startActivity(new Intent(this, InvitacionCorreo.class)));

        // ATRAS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reunion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}