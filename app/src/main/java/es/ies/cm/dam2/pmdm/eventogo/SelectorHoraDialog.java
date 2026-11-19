package es.ies.cm.dam2.pmdm.eventogo;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// DialogFragment propio con el reloj para elegir una hora.
// Devuelve la hora como texto HH:mm a la pantalla que lo abrió, con la clave que le dieron
public class SelectorHoraDialog extends DialogFragment {

    private static final String ARG_CLAVE = "clave";
    public static final String RESULTADO_HORA = "hora";

    // Claves con las que Nuevo Evento distingue la hora de inicio de la de fin
    public static final String CLAVE_HORA_INICIO = "hora_inicio";
    public static final String CLAVE_HORA_FIN = "hora_fin";

    public static SelectorHoraDialog nuevo(String clave) {
        SelectorHoraDialog dialogo = new SelectorHoraDialog();
        Bundle args = new Bundle();
        args.putString(ARG_CLAVE, clave);
        dialogo.setArguments(args);
        return dialogo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String clave = requireArguments().getString(ARG_CLAVE);
        Calendar ahora = Calendar.getInstance(); // la hora actual es la que sale al abrir el reloj
        return new TimePickerDialog(requireContext(),
                (view, hora, minuto) -> {
                    Bundle resultado = new Bundle();
                    resultado.putString(RESULTADO_HORA, String.format("%02d:%02d", hora, minuto)); // %02d para que salga 00:00 y no 0:0
                    getParentFragmentManager().setFragmentResult(clave, resultado);
                },
                ahora.get(Calendar.HOUR_OF_DAY), ahora.get(Calendar.MINUTE), true); // formato de 24 horas
    }
}
