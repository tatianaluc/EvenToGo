package es.ies.cm.dam2.pmdm.eventogo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

// Dialog propio para la cuenta del usuario, con dos modos:
//  - registrar, que crea un usuario nuevo
//  - cambiar clave del usuario que ha entrado (clave actual + clave nueva)
// Las dos cosas son consultas a la tabla usuarios
public class CuentaDialog extends DialogFragment {

    public static final int MODO_REGISTRAR = 0;
    public static final int MODO_CAMBIAR_CLAVE = 1;
    private static final String ARG_MODO = "modo";
    private static final String ARG_USUARIO_ID = "usuarioId";

    // Se crea con este método y los datos viajan en los argumentos, que Android conserva al girar la pantalla
    public static CuentaDialog nuevo(int modo, int usuarioId) {
        CuentaDialog dialogo = new CuentaDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_MODO, modo);
        args.putInt(ARG_USUARIO_ID, usuarioId);
        dialogo.setArguments(args);
        return dialogo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int modo = requireArguments().getInt(ARG_MODO);
        // El contenido de la ventana es un layout propio
        View vista = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cuenta, null);
        EditText etNombre = vista.findViewById(R.id.etNombreCuenta);
        EditText etClave = vista.findViewById(R.id.etClaveCuenta);
        EditText etClaveNueva = vista.findViewById(R.id.etClaveNueva);

        // Cada modo esconde los campos que no necesita
        if (modo == MODO_REGISTRAR) {
            etClaveNueva.setVisibility(View.GONE);
        } else {
            etNombre.setVisibility(View.GONE);
            etClave.setHint(R.string.cuenta_clave_actual);
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle(modo == MODO_REGISTRAR ? R.string.cuenta_crear_titulo : R.string.cuenta_cambiar_titulo)
                .setView(vista)
                // El listener del botón Aceptar se pone en onStart para que la ventana no se cierre
                // al pulsarlo, y se quede abierta cuando detecta un error
                .setPositiveButton(R.string.cuenta_aceptar, null)
                .setNegativeButton(R.string.dialogo_cancelar, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialogo = (AlertDialog) getDialog();
        if (dialogo == null) return;
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> aceptar(dialogo));
    }

    // Comprueba los campos y, si están bien, hace la consulta a la bd
    private void aceptar(AlertDialog dialogo) {
        int modo = requireArguments().getInt(ARG_MODO);
        int usuarioId = requireArguments().getInt(ARG_USUARIO_ID);
        EditText etNombre = dialogo.findViewById(R.id.etNombreCuenta);
        EditText etClave = dialogo.findViewById(R.id.etClaveCuenta);
        EditText etClaveNueva = dialogo.findViewById(R.id.etClaveNueva);
        String nombre = etNombre.getText().toString().trim();
        String clave = etClave.getText().toString().trim();
        String nueva = etClaveNueva.getText().toString().trim();

        // ningún campo visible puede quedar vacío
        boolean ok = true;
        if (modo == MODO_REGISTRAR && nombre.isEmpty()) {
            etNombre.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }
        if (clave.isEmpty()) {
            etClave.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }
        if (modo == MODO_CAMBIAR_CLAVE && nueva.isEmpty()) {
            etClaveNueva.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }
        if (!ok) return;

        FragmentActivity actividad = requireActivity();
        Context contexto = actividad.getApplicationContext();
        AppDatabase.ejecutor.execute(() -> {
            EventosDB bd = new EventosDB(contexto);
            boolean exito = (modo == MODO_REGISTRAR)
                    ? bd.registrarUsuario(nombre, clave)
                    : bd.cambiarClave(usuarioId, clave, nueva);
            // lo que toca la pantalla se hace en el hilo principal
            actividad.runOnUiThread(() -> {
                if (exito) {
                    Toast.makeText(actividad, modo == MODO_REGISTRAR ? R.string.cuenta_creada
                            : R.string.cuenta_clave_cambiada, Toast.LENGTH_SHORT).show();
                    dismiss(); // todo bien, cerramos la ventana
                } else if (modo == MODO_REGISTRAR) {
                    etNombre.setError(actividad.getString(R.string.cuenta_ya_existe));
                } else {
                    etClave.setError(actividad.getString(R.string.cuenta_clave_incorrecta));
                }
            });
        });
    }
}
