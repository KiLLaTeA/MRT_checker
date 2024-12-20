package com.example.mrt_checker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private Button btnCapture, btnUpload, btnOpenGallery;
    private TextView textView;

    private String class_name_yolo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = findViewById(R.id.btnCapture);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        btnCapture = findViewById(R.id.btnCapture);
        btnUpload = findViewById(R.id.btnUpload);
        btnOpenGallery = findViewById(R.id.btnOpenGallery);
        imageView = findViewById(R.id.imageView);

        btnCapture.setOnClickListener(v -> dispatchTakePictureIntent());
        btnUpload.setOnClickListener(v -> uploadImage());
        btnOpenGallery.setOnClickListener(v -> openGallery());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String base64Image = bitmapToBase64(imageBitmap);
            sendImageToDetect(base64Image);
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void sendImageToDetect(String base64Image) {
        String url = "http://192.168.0.166:5000/api/detect";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    String processedBase64Image = null;
                    try {
                        processedBase64Image = jsonResponse.getString("image");
                        textView.setText(jsonResponse.getString("text") + jsonResponse.getString("class"));
                        class_name_yolo = jsonResponse.getString("class");

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("Base64", "Processed Base64: " + processedBase64Image);
                    Bitmap processedBitmap = base64ToBitmap(processedBase64Image);
                    imageView.setImageBitmap(processedBitmap);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    btnCapture.setText("Сделать новое фото");
                    btnOpenGallery.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    if (textView.getText().toString().equals("Ничего не найдено!")){
                        btnUpload.setVisibility(View.INVISIBLE);
                    }
                    else{
                        btnUpload.setVisibility(View.VISIBLE);
                    }

                    float heightInDp = 50f;
                    int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

                    btnCapture.getLayoutParams().height = heightInPx;
                    btnCapture.requestLayout();

                    btnOpenGallery.getLayoutParams().height = heightInPx;
                    btnOpenGallery.requestLayout();
                },
                error -> {
                    Log.d("Base64", "Original Base64: " + base64Image);
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", base64Image);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private Bitmap resizeImage(Bitmap originalBitmap, int maxWidth, int maxHeight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return originalBitmap;
        }

        float ratio = (float) width / height;
        if (ratio > 1) {
            width = maxWidth;
            height = (int) (width / ratio);
        } else {
            height = maxHeight;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }

    private void sendImageToGallery(String base64Image) {
        String url = "http://192.168.0.166:5000/api/upload";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.mrt);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        btnUpload.setVisibility(View.INVISIBLE);
                        btnOpenGallery.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.INVISIBLE);

                        float heightInDp = 75f;
                        int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

                        btnCapture.getLayoutParams().height = heightInPx;
                        btnCapture.requestLayout();

                        btnOpenGallery.getLayoutParams().height = heightInPx;
                        btnOpenGallery.requestLayout();

                        btnCapture.setText("Сделать фото");

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Toast.makeText(this, "Ошибка: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", base64Image);
                params.put("class", class_name_yolo);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private Bitmap base64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void uploadImage() {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();

        Bitmap resizedBitmap = resizeImage(bitmap, 800, 600);
        String base64Image = bitmapToBase64(resizedBitmap);

        sendImageToGallery(base64Image);
        Toast.makeText(this, "Изображение загружено", Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        Intent intent = new Intent(MainActivity.this, Gallery.class);
        startActivity(intent);

        Toast.makeText(this, "Открытие галереи", Toast.LENGTH_SHORT).show();
    }
}