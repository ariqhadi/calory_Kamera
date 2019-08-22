package com.example.kaloka3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.buttonKamera);
        Button btnGalery = findViewById(R.id.buttonGallery);
        imageView = findViewById(R.id.image_view);

        btnCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                fromCamera();
            }
        });

        btnGalery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                fromGalery();
            }
        });

    }

    private void fromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,1);
    }
    private void fromGalery(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Uri contentURI = data.getData();

        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://testing-image-40b84.appspot.com");
        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("mountains.jpg");

        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (requestCode == 0){
        try {
            Bitmap bitmap;
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] data1 = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data1);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });

            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
                                 }
            }else if(requestCode == 1){

            Bitmap bitmap = (Bitmap)data.getExtras().get("data");

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] data1 = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data1);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });

            imageView.setImageBitmap(bitmap);


        }

//        String path = saveImage(bitmap);
//        Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

    }


}
