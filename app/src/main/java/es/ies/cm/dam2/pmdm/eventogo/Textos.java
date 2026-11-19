package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Context;

// Traduce los valores que se guardan en la bd en español al idioma del móvil para enseñarlos.
// En la bd se guarda el original, pero en la pantalla se ve en inglés.
public final class Textos {
    private Textos() {
    }

    // Tipo de evento
    public static String tipo(Context contexto, String valor) {
        return traducir(contexto, R.array.tipo_evento_valor, R.array.tipo_evento, valor);
    }

    // Lugar del evento
    public static String lugar(Context contexto, String valor) {
        return traducir(contexto, R.array.lugares_valor, R.array.lugares, valor);
    }

    // Modalidad de la reunión
    public static String modalidad(Context contexto, String valor) {
        if ("Online".equals(valor)) return contexto.getString(R.string.online);
        if ("Oficina".equals(valor)) return contexto.getString(R.string.oficina);
        return valor;
    }

    // Estado de la reunión
    public static String estado(Context contexto, String valor) {
        if (ReunionEvento.ACEPTADA.equals(valor)) return contexto.getString(R.string.estado_aceptado);
        if (ReunionEvento.CANCELADA.equals(valor)) return contexto.getString(R.string.estado_cancelado);
        return valor;
    }

    // Busca el valor en el array de valores y devuelve el texto de la misma posición del array traducido.
    // Si no lo encuentra devuelve el valor tal cual, así nunca se queda en blanco
    private static String traducir(Context contexto, int idValores, int idTextos, String valor) {
        String[] valores = contexto.getResources().getStringArray(idValores);
        String[] textos = contexto.getResources().getStringArray(idTextos);
        for (int i = 0; i < valores.length && i < textos.length; i++) {
            if (valores[i].equals(valor)) return textos[i];
        }
        return valor;
    }
}
