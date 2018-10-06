package com.ulfat.mytextrecognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.*;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

  /*
  requirements:  (url: https://console.firebase.google.com/project/my-text-recognizer/overview)
   1) download congfig file (app repository: google-services.json)
   2) Firebase SDK dependencies in both (build.gradle)
   3) ML kit SDK dependencies in (<app>build.gradle)  url: https://firebase.google.com/docs/ml-kit/android/recognize-text
   */

public class MainActivity extends AppCompatActivity {

    private Button snapBtn;
    private Button detectBtn;
    private ImageView imageView;
    private TextView txtView;
    private Bitmap imageBitmap;//make it a global variable so FireVersion can access it.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snapBtn = findViewById(R.id.snapBtn);
        detectBtn = findViewById(R.id.detectBtn);
        imageView = findViewById(R.id.imageView);
        txtView = findViewById(R.id.txtView);
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });

    }
     /* OUTSIDE onCreate() :  */

     //Code to snap from camera: (url: https://developer.android.com/training/camera/photobasics)
    // 2 Blocks of code :

    // 1) Intent Obj instance to comm with another app (i.e. camera) dnt 4get permission

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // 2) convert image to Bitmap Obj > display the image in imageView :
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            /*Global var*/ imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    // now the Bitmap image will be processed as FirebaseVision Obj:

    private void detectText()
    {   //create instance of Firebase Vision Image
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
      FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
      detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
          @Override
          public void onSuccess(FirebaseVisionText firebaseVisionText) {

            processText(firebaseVisionText);
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

          }
      });


    }

    private void processText(FirebaseVisionText text)
    {   //each element of the list will hold one block of text:
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0){
            Toast.makeText(MainActivity.this, "No Text Found", Toast.LENGTH_LONG).show();
            return;
        }
        //iterate through the list blocks (using that simple for loop) then return them:
        for (FirebaseVisionText.Block block : text.getBlocks()){
            String txt = block.getText();
            txtView.setTextSize(24);
            txtView.setText(txt);
        }
    }


}
//WORKS ALHAMDULILLAH <3