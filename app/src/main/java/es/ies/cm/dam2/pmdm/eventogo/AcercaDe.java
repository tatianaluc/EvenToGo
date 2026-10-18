package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ClipData;
import android.os.Bundle;
import android.text.Layout;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AcercaDe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acerca_de);

        //Para el Drag and Drop
        TextView txtAcercaDe = findViewById(R.id.txtAcercaDe);
        ConstraintLayout raiz = findViewById(R.id.acerca_de);

        //Longlcik devuelve un boolean, asi que le decimos que devuelva true
        txtAcercaDe.setOnLongClickListener(v->{
            //EMpezamos el drag and drop
            /*esta funcion pide:
            1. un paquete de datos, que esta vez estará vacío
            2. una copia transparente del texto que arrastra
            3. un dato extra nulo que no necesitaremos esta vez
            4. y 0 de que no nefceistamos nada mas.
             */
            v.startDragAndDrop(ClipData.newPlainText("",""), new View.DragShadowBuilder(v), null, 0);
            return true;
        });
        //aqui le decimos que ejecute el arrastre por todo el layout, con true para que lo acepte
        //sin el true, no llega el soltar al main activity
        raiz.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                finish();
            }
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.acerca_de), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}