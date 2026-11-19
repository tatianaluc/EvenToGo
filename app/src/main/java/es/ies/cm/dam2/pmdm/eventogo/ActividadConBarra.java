package es.ies.cm.dam2.pmdm.eventogo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

// Clase base de las pantallas secundarias. Les pone la flecha de volver atrás en la barra superior,
// de esta manera  solo hay que heredar de esta clase cada vez que queremos repetir eestas acciaones
// La barra sale del Theme y del label de cada activity en el manifest
public class ActividadConBarra extends AppCompatActivity {

    // Se ejecuta cuando la pantalla ya está creada y ya existe la barra,
    // por eso esta en post create
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Al pulsar la flecha hace lo mismo que el botón atrás, cierra la pantalla actual
    // y vuelve a la anterior, simplificando la eficacia de su uso
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
