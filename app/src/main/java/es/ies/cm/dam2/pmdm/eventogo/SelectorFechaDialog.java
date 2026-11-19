package es.ies.cm.dam2.pmdm.eventogo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Dialog propio con el calendario para elegir una fecha.
// Devuelve la fecha como texto dd/MM/aaaa a la pantalla que lo abrió, con la clave que le dieron
public class SelectorFechaDialog extends DialogFragment {

    private static final String ARG_CLAVE = "clave";
    public static final String RESULTADO_FECHA = "fecha";

    // Claves con las que cada pantalla reconoce la fecha que ha elegido
    public static final String CLAVE_FECHA_EVENTO = "fecha_evento";
    public static final String CLAVE_FECHA_REUNION = "fecha_reunion";

    // Se crea con este método y la clave va en los argus que Android conserva al girar la pantalla
    public static SelectorFechaDialog nuevo(String clave) {
        SelectorFechaDialog dialogo = new SelectorFechaDialog();
        Bundle args = new Bundle();
        args.putString(ARG_CLAVE, clave);
        dialogo.setArguments(args);
        return dialogo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String clave = requireArguments().getString(ARG_CLAVE);
        Calendar hoy = Calendar.getInstance(); //fecha de hoy: es la que sale seleccionada al abrir el calendario
        DatePickerDialog calendario = new DatePickerDialog(requireContext(),
                (view, anio, mes, dia) -> { //y lo que recibe el método cuando se acepta
                    Bundle resultado = new Bundle();
                    //formateamos de enteros a texto, importante aclarar el mes + 1 porque empiezan por defecto en 0
                    resultado.putString(RESULTADO_FECHA, String.format("%02d/%02d/%d", dia, mes + 1, anio));
                    getParentFragmentManager().setFragmentResult(clave, resultado);
                },
                hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));
        /* Para poder evitar que el usuario escoja una fecha previa al día de hoy, le decimos al date picker
        que recoja la fecha mínima, que será la hora actual en milisegundos.
        Y de esta manera no da opción a seleccionar antes de dicha fecha, aparecen las previas en gris.
         */
        calendario.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // - 1000 para evitar que se choque con otras versiones de Android
        return calendario;
    }
}
