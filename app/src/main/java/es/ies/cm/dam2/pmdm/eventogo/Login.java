package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Pantalla de login: comprueba usuario y clave en la tabla usuarios de la bd
public class Login extends AppCompatActivity {

    // Claves de los extras con los datos del usuario que ha entrado, para que las pantallas siguientes sepan quién es
    public static final String EXTRA_USUARIO_ID = "usuarioId";
    public static final String EXTRA_USUARIO = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText etUsuario = findViewById(R.id.etUsuario);
        EditText etClave = findViewById(R.id.etClave);
        Button btnEntrar = findViewById(R.id.btnEntrar);
        Button btnAtras = findViewById(R.id.btnAtras);

        btnEntrar.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String clave = etClave.getText().toString().trim();

            // primero comprobamos que no deje nada vacío
            boolean ok = true;
            if (usuario.isEmpty()) {
                etUsuario.setError("Campo Obligatorio");
                ok = false;
            }
            if (clave.isEmpty()) {
                etClave.setError("Campo Obligatorio");
                ok = false;
            }
            if (!ok) return;

            // preguntamos a la bd: devuelve el id del usuario, o -1 si no existe esa combinación
            int idUsuario = new EventosDB(this).comprobarLogin(usuario, clave);

            if (idUsuario == -1) {
                Toast.makeText(this, "Usuario o clave incorrectos", Toast.LENGTH_SHORT).show();
                etClave.setText(""); // vaciamos la clave para que la vuelva a escribir
                return;
            }

            Toast.makeText(this, "Sesión iniciada como " + usuario, Toast.LENGTH_SHORT).show();

            // Por ahora vamos a Mis eventos (Coleccion), pasando quién ha entrado.
            // Más adelante cambiará el destino cuando tengamos la lista del usuario
            Intent intent = new Intent(this, Coleccion.class);
            intent.putExtra(EXTRA_USUARIO_ID, idUsuario);
            intent.putExtra(EXTRA_USUARIO, usuario);
            startActivity(intent);
            finish(); // así al darle atrás desde Mis eventos vuelve al Main y no al login
        });

        // ATRAS
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}