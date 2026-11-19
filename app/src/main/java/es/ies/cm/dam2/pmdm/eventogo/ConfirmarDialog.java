package es.ies.cm.dam2.pmdm.eventogo;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

// Dialog propio para pedir confirmación antes de borrar que se usa con los eventos y con las reuniones.
// Al ser parte del sistema, lo recuerda y lo vuelve a poner si se gira el móvil con la ventana abierta.
// cuando se acepta, le devuelve a la pantalla el id y el dato que le dieron
public class ConfirmarDialog extends DialogFragment {

    // Claves de lo que se le pasa al crearlo
    private static final String ARG_CLAVE = "clave";
    private static final String ARG_TITULO = "titulo";
    private static final String ARG_MENSAJE = "mensaje";
    // Claves de lo que devuelve al aceptar
    public static final String RESULTADO_ID = "id";
    public static final String RESULTADO_DATO = "dato";

    // Claves con las que cada pantalla reconoce la respuesta de este diálogo
    public static final String CLAVE_BORRAR_EVENTO = "borrar_evento";
    public static final String CLAVE_BORRAR_REUNION = "borrar_reunion";
    public static final String CLAVE_BORRAR_CUENTA = "borrar_cuenta";

    // se crea con este método y los datos viajan en un Bundle que Android conserva al girar la pantalla
    public static ConfirmarDialog nuevo(String clave, String titulo, String mensaje, int id, String dato) {
        ConfirmarDialog dialogo = new ConfirmarDialog();
        Bundle args = new Bundle();
        args.putString(ARG_CLAVE, clave);
        args.putString(ARG_TITULO, titulo);
        args.putString(ARG_MENSAJE, mensaje);
        args.putInt(RESULTADO_ID, id);
        args.putString(RESULTADO_DATO, dato);
        dialogo.setArguments(args);
        return dialogo;
    }

    // Aquí se construye la ventana que se va a ver
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = requireArguments();
        return new AlertDialog.Builder(requireContext())
                .setTitle(args.getString(ARG_TITULO))
                .setMessage(args.getString(ARG_MENSAJE))
                .setPositiveButton(R.string.dialogo_eliminar, (dialog, which) -> {
                    // Al aceptar mandamos el resultado a la pantalla que nos abrió, con la clave de este diálogo
                    Bundle resultado = new Bundle();
                    resultado.putInt(RESULTADO_ID, args.getInt(RESULTADO_ID));
                    resultado.putString(RESULTADO_DATO, args.getString(RESULTADO_DATO));
                    getParentFragmentManager().setFragmentResult(args.getString(ARG_CLAVE), resultado);
                })
                .setNegativeButton(R.string.dialogo_cancelar, null) // cancelar solo cierra la ventana
                .create();
    }
}
