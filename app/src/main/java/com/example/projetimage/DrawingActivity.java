package com.example.projetimage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawingActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Button buttonClear;
    private Button buttonBack;
    private Button buttonSave;
    private Spinner spinnerThickness;
    private Spinner spinnerColor;
    private int currentColor = Color.BLACK; // Couleur de départ
    private float currentThickness = 5f; // Épaisseur de départ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingView = findViewById(R.id.drawing_view);
        buttonClear = findViewById(R.id.button_clear);
        buttonBack = findViewById(R.id.button_back);
        buttonSave = findViewById(R.id.button_save);
        spinnerThickness = findViewById(R.id.spinner_thickness);
        spinnerColor = findViewById(R.id.spinner_color);

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clearDrawing();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDrawing();
            }
        });

        ArrayAdapter<CharSequence> thicknessAdapter = ArrayAdapter.createFromResource(this, R.array.thickness_options, android.R.layout.simple_spinner_item);
        thicknessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThickness.setAdapter(thicknessAdapter);

        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, R.array.col, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedColor = parentView.getItemAtPosition(position).toString();
                switch (selectedColor.toLowerCase()) {
                    case "noir":
                        currentColor = Color.BLACK;
                        break;
                    case "rouge":
                        currentColor = Color.RED;
                        break;
                    case "orange":
                        currentColor = Color.parseColor("#FFA500");
                        break;
                    case "jaune":
                        currentColor = Color.YELLOW;
                        break;
                    case "vert":
                        currentColor = Color.GREEN;
                        break;
                    case "bleu":
                        currentColor = Color.BLUE;
                        break;
                    case "violet":
                        currentColor = Color.parseColor("#800080");
                        break;
                }
                drawingView.setPaintColor(currentColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ne rien faire si rien n'est sélectionné
            }
        });

        spinnerThickness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedThickness = parentView.getItemAtPosition(position).toString();
                switch (selectedThickness.toLowerCase()) {
                    case "fin":
                        currentThickness = 2f;
                        break;
                    case "normale":
                        currentThickness = 5f;
                        break;
                    case "épais":
                        currentThickness = 10f;
                        break;
                }
                drawingView.setPaintThickness(currentThickness);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ne rien faire si rien n'est sélectionné
            }
        });
    }

    private void saveDrawing() {
        Bitmap bitmap = Bitmap.createBitmap(drawingView.getWidth(), drawingView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawingView.draw(canvas);

        try {
            FileOutputStream fos = openFileOutput("drawing.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            // Afficher un message de confirmation
            Toast.makeText(DrawingActivity.this, "Dessin enregistré", Toast.LENGTH_SHORT).show();

            // Enregistrer l'indicateur de dessin enregistré dans les préférences partagées
            SharedPreferences sharedPreferences = getSharedPreferences("drawings", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isDrawingSaved", true);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DrawingView extends View {

    private Paint paint;
    private List<Path> paths;
    private List<Float> thicknesses; // Ajout d'une liste pour stocker les épaisseurs de trait
    private List<Integer> colors;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
        paths = new ArrayList<>();
        thicknesses = new ArrayList<>(); // Initialisation de la liste des épaisseurs de trait
        colors = new ArrayList<>();
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND); // Définir le style de terminaison du trait
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < paths.size(); i++) {
            paint.setColor(colors.get(i));
            paint.setStrokeWidth(thicknesses.get(i)); // Définir l'épaisseur de trait pour chaque chemin
            canvas.drawPath(paths.get(i), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startNewPath(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                addPoint(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        return true;
    }

    private void startNewPath(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        paths.add(path);
        colors.add(paint.getColor());
        thicknesses.add(paint.getStrokeWidth()); // Ajouter l'épaisseur de trait actuelle à la liste
    }

    private void addPoint(float x, float y) {
        if (paths.size() > 0) {
            Path path = paths.get(paths.size() - 1);
            path.lineTo(x, y);
        }
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    public void setPaintThickness(float thickness) {
        paint.setStrokeWidth(thickness);
    }

    public void clearDrawing() {
        paths.clear();
        colors.clear();
        thicknesses.clear(); // Effacer également la liste des épaisseurs de trait
        invalidate();
    }
}