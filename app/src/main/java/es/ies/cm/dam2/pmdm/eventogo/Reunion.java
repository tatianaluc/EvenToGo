package es.ies.cm.dam2.pmdm.eventogo;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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
        //Fecha de la reunión
        EditText etFechaReu = findViewById(R.id.etFechaReu);
        etFechaReu.setOnClickListener(v->{
            Calendar hoy = Calendar.getInstance(); //Para que al abrir calendario aparezca la fecha de hoy
            //para escogerla
            DatePickerDialog escogerFecha = new DatePickerDialog(this,
                    (view, anio, mes, dia) ->  //y lo que recibe el método
                            //formateamos de enteros a texto para que se muestre en el edit text
                            //importante aclarar el mes + 1 porque empiezan por defecto en 0
                    {
                        etFechaReu.setText(String.format("%02d/%02d/%d", dia, mes +1, anio));
                        etFechaReu.setError(null);
                    },
                    hoy.get(java.util.Calendar.YEAR), hoy.get(java.util.Calendar.MONTH), hoy.get(java.util.Calendar.DAY_OF_MONTH));
            escogerFecha.show();
        });

        //ahora escoger si presencial u online y settear un color dependiendo de lo que escoja
        RadioGroup grpLugar = findViewById(R.id.grpLugar);
        RadioButton online = findViewById(R.id.online);
        grpLugar.setOnCheckedChangeListener((group, checked) ->{
            if(checked == R.id.online){
                online.setTextColor(ContextCompat.getColor(this,R.color.orange));
            }
            if (checked == R.id.ofi){
                online.setTextColor(ContextCompat.getColor(this,R.color.red));
            }
        });

        //COmprobar los campos y botón enviar
        Button btnConfirmar = findViewById(R.id.btnReunion);
        btnConfirmar.setOnClickListener(v->{
            boolean ok = true; // si algún campo falla, pasa a false
            if(etNomCliente.getText().toString().trim().isEmpty()){
                etNomCliente.setError("Campo Obligatorio.");
                ok = false;
            }
            if(etFechaReu.getText().toString().trim().isEmpty()){
                etFechaReu.setError("Escoja una fecha.");
                ok = false;
            }
            if(grpLugar.getCheckedRadioButtonId() == -1){
                TextView txtLugar = findViewById(R.id.txtLugar);
                txtLugar.setError("Debe seleccionar una opción.");
                ok = false;
            }

            if (!ok) {
                Toast.makeText(this, "Debe completar los campos\nantes de confirmar la reunión", Toast.LENGTH_SHORT).show();
                return;
            }
//        ActivityResultLauncher<Intent> intentResult;
//        intentResult = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult o) {
//                        if(o.getResultCode() == RESULT_OK) {
//                            if(o.getData() != null && o.getData().getExtras() != null) {
//                                Log.wtf(activity_confirmar_reunion, o.getData().getStringExtra("Resultado"));
//                            }
//                        }
//                    }
//                }
//        );
//        Intent i = new Intent(this, ConfirmarReunion.class);
//        intentResult.launch(i);


        });

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