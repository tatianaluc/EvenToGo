package es.ies.cm.dam2.pmdm.eventogo;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

        // Segunda parte, cantidad de servicios del 1-10
        //Declaramos edit y txt
        EditText editServ = findViewById(R.id.editServ);
        TextView txtError = findViewById(R.id.txtError);

        // Con el componente textwatcher chequeamos lo que escribe el usuario
        // y dispara el error si algo esta mal en las condiciones

        // se crean automáticamente sus 3 métodos, pero solo usamos el after
        // ya que es el que va actualizando el txt constatemente
        editServ.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                boolean validarTxt = false;

                // si no está vacío comprobamos
                if(!text.isEmpty()){
                    try{
                        int cantServ = Integer.parseInt(text);
                        validarTxt = (cantServ >= 1 && cantServ <= 10);
                    } catch (NumberFormatException e){
                        validarTxt = false;
                    }
                }
                // una vez comprobado, mostramos los mensajes
                // si validar sigue siendo false o el usuario lo deja vacío, lo dejamos igual
                if (validarTxt || text.isEmpty()){
                    txtError.setText("");
                } else { // cualquier otra cosa es incorrecta
                    txtError.setText("Cantidad de servicios incorrecta");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        // ahora los radio buttons que cambien el color del activity
        RadioGroup groupStat = findViewById(R.id.groupStat);
        // se usará para cambiar todo el fondo del act
        View actNumEv = findViewById(R.id.numEventos);
        Button btnLimpiar = findViewById(R.id.btnLimpiar);

        groupStat.setOnCheckedChangeListener((rGroup, rChecked) -> {
                if(rChecked == R.id.conf){
                    actNumEv.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                } else if(rChecked == R.id.pend){
                    actNumEv.setBackgroundColor(ContextCompat.getColor(this, R.color.org));
                } else if (rChecked == R.id.canc){
                actNumEv.setBackgroundColor(ContextCompat.getColor(this, R.color.red_claro));
            }
        });

        //le decimos a limpiar que lo ponga todo en orden a blanco
        btnLimpiar.setOnClickListener(v->{
            groupStat.clearCheck();
            actNumEv.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.numEventos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}