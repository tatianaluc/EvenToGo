package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Carga inicial de la bd Room (una sola vez; en el hilo aparte, no bloquea la pantalla)
        DatosIniciales.cargarSiHaceFalta(this);

        Button btnCatalogo = findViewById(R.id.btnCatalogo);
        Button btnSel = findViewById(R.id.btnSelectEvent);

        //ir a buscar evento con el catálogo
        btnCatalogo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BuscarEvento.class)));


        // Filtrado de boton para pasar directamente a eventos registrados
        //antes de ver Mis eventos, hay que iniciar sesión
        btnSel.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        });

        //Boton abrir web accion, no va a ninguna clase, abre un uri
        Button btnWeb = findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sassotsound.com/eventos/"));
            startActivity(i);
        });

        //para el drag and drop de acerca de
        /* LO que va a pasar es que, se va a poder arrastrar el boton de acerca de al text view
        y se va a cambiar al activity de acerca de la app. Y luego desde acerca de, arrastrando el
        texto con la información, nos devuelve a este main activity. Se hace para verificar la
        interacción de gestos con el "dedo" dentro de la app.
         */
        Button btnAcercaDe = findViewById(R.id.btnAcercaDe);
        TextView destino = findViewById(R.id.txtDestino);

        //Longlcik devuelve un boolean, asi que le decimos que devuelva true
        //EMpezamos el drag and drop
            /*esta funcion pide:
            1. un paquete de datos, que esta vez estará vacío
            2. una copia transparente del texto que arrastra
            3. un dato extra nulo que no necesitaremos esta vez
            4. y 0 de que no nefceistamos nada mas.
             */
        btnAcercaDe.setOnLongClickListener(v -> {
            v.startDragAndDrop(
                    ClipData.newPlainText("", ""),
                    new View.DragShadowBuilder(v),
                    null,
                    0);
            return true;
        });

        //aqui le decimos que ejecute el arrastre, que está tambien declarado en layout acerca de
        //si esta funcion, no acepta el arrastre entre layouts
        destino.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                startActivity(new Intent(this, AcercaDe.class));
            }
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}