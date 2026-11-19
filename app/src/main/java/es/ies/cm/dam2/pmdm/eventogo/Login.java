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

// login comprueba usuario y clave en la tabla usuarios de la bd
public class Login extends AppCompatActivity {

    // Claves de los extras con los datos del usuario que ha entrado, para que lo que muestren sepan quién es
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
                etUsuario.setError(getString(R.string.campo_obligatorio));
                ok = false;
            }
            if (clave.isEmpty()) {
                etClave.setError(getString(R.string.campo_obligatorio));
                ok = false;
            }
            if (!ok) return;

            // preguntamos a la bd de room en el hilo aparte
            // devuelve el id del usuario, o -1 si no existe
            AppDatabase.ejecutor.execute(() -> {
                int idUsuario = new EventosDB(this).comprobarLogin(usuario, clave);

                // lo que toca la pantalla vuelve al hilo principal
                runOnUiThread(() -> {
                    if (idUsuario == -1) {
                        Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
                        etClave.setText(""); // vaciamos la clave para que la vuelva a escribir
                        return;
                    }

                    Toast.makeText(this, getString(R.string.login_ok, usuario), Toast.LENGTH_SHORT).show();

                    // Vamos a Mis eventos, pasando quién ha entrado
                    Intent intent = new Intent(this, Coleccion.class);
                    intent.putExtra(EXTRA_USUARIO_ID, idUsuario);
                    intent.putExtra(EXTRA_USUARIO, usuario);
                    startActivity(intent);
                    finish(); // así al darle atrás desde Mis eventos vuelve al Main y no al login
                });
            });
        });

        // CREAR CUENTA abre el Dialog para registrar un usuario nuevo
        findViewById(R.id.btnCrearCuenta).setOnClickListener(v ->
                CuentaDialog.nuevo(CuentaDialog.MODO_REGISTRAR, -1)
                        .show(getSupportFragmentManager(), "crear_cuenta"));

        // ATRAS
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}