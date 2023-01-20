package com.example.biometric;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.concurrent.Executor;



public class MainActivity extends AppCompatActivity {
    private Button bt1;
    private Button regb;
    private Button a;
    private static final int PICK_IMAGE_REQUEST =234;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Uri filepath;
    private StorageReference storageRef;
    private EditText editTextTextPersonName2;
    FirebaseStorage storage;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage=FirebaseStorage.getInstance();
         storageRef = storage.getReference();
        bt1 = (Button) findViewById(R.id.bt1);

        a = (Button) findViewById(R.id.auth);
editTextTextPersonName2=(EditText) findViewById(R.id.editTextTextPersonName2);
        executor = ContextCompat.getMainExecutor(this);

            biometricPrompt = new BiometricPrompt(MainActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                                    "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(),
                            "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for my app")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use account password")
                    .setConfirmationRequired(false)
                    .build();

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://biometric-f0717-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("Value");
                // Read from the database

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        float val=Float.parseFloat(value);
                        if((float)val>0.5){
                            openAuth();
                            //return;
                        }
                       //Log.d(TAG, "Value is: " + value);
                        else{
                            Toast.makeText(getApplicationContext(),"Cannot validate user",Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });


            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=editTextTextPersonName2.getText().toString();
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://biometric-f0717-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("Username");

                myRef.setValue(user);

                showFileChooser();
            }
        });



    }
    public void openAuth(){
        Intent intent = new Intent(this,Auth.class);
        startActivity(intent);
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select your File"),PICK_IMAGE_REQUEST);
       // uploadFile();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("STATE","Ruinning activity");
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        StorageReference mountainsRef = storageRef.child("predict/rec"+currentItem);
                        mountainsRef.putFile(imageUri);
                        currentItem = currentItem + 1;
                    }
                } else if (data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    Uri loc=Uri.fromFile(new File(imagePath));
                    StorageReference mountainImagesRef = storageRef.child("predict/rec.png");
                    mountainImagesRef.putFile(data.getData().normalizeScheme());
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
            }
        }
    }
}
