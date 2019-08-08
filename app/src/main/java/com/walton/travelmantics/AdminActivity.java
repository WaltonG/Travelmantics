package com.walton.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AdminActivity extends AppCompatActivity {
    EditText dealTitleEditText;
    EditText dealDescriptionEditText;
    EditText dealPriceEditText;
    ImageView dealImageView;
    Button dealPictureButton;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TravelDeal travelDeal;
    private static final int PICTURE_RESULT = 42; //the answer to everything

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseDatabase = FirebaseUtility.mFireBaseDatabase;
        mDatabaseReference =FirebaseUtility.mDatabaseReference;
        dealTitleEditText = findViewById(R.id.title_editText);
        dealDescriptionEditText = findViewById(R.id.description_editText);
        dealPriceEditText = findViewById(R.id.price_editText);
        dealPictureButton = findViewById(R.id.travel_deal_image_button);
        dealImageView = findViewById(R.id.travel_deal_image);
        Intent intent = getIntent();
        TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("TravelDeal");
        if(travelDeal == null) {
            travelDeal = new TravelDeal();
        }
        this.travelDeal = travelDeal;
        dealTitleEditText.setText(travelDeal.getTitle());
        dealDescriptionEditText.setText(travelDeal.getDescription());
        dealPriceEditText.setText(travelDeal.getPrice());
        showImage(travelDeal.getImageURL());
        dealPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent,
                        "Insert Picture"), PICTURE_RESULT);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_activity_menu, menu);
        if(FirebaseUtility.isAdmin == true){
            menu.findItem(R.id.admin_delete_menu).setVisible(true);
            menu.findItem(R.id.admin_save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.admin_delete_menu).setVisible(false);
            menu.findItem(R.id.admin_save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.admin_save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                resetEditText();
                backToUserActivity();
                return true;
            case R.id.admin_delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToUserActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtility.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String url = task.getResult().toString();
                    String pictureName = task.getResult().getPath();
                   travelDeal.setImageURL(url);
                    travelDeal.setImageName(pictureName);
                    Log.d("Url: ", url);
                    Log.d("Name", pictureName);
                    showImage(url);
                }
            });


        }
    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(dealImageView);
        }
    }

    private void resetEditText() {
        dealTitleEditText.setText("");
        dealDescriptionEditText.setText("");
        dealPriceEditText.setText("");
        dealTitleEditText.requestFocus();
    }

    private void saveDeal() {
        travelDeal.setTitle(dealTitleEditText.getText().toString());
        travelDeal.setDescription (dealDescriptionEditText.getText().toString());
        travelDeal.setPrice(dealPriceEditText.getText().toString());
        if(travelDeal.getiD()== null){
        mDatabaseReference.push().setValue(travelDeal);
        }
        else {
            mDatabaseReference.child(travelDeal.getiD()).setValue(travelDeal);
        }
    }

    private void deleteDeal () {
        if (travelDeal == null) {
            Toast.makeText(this, "Please save the travel deal before deleting",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.child(travelDeal.getiD()).removeValue();
        if(travelDeal.getImageName() != null && travelDeal.getImageName().isEmpty() == false) {
            StorageReference picRef = FirebaseUtility.mStorage.getReference().child(travelDeal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image Successfully Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }
    }

    private void backToUserActivity () {

        startActivity(new Intent(this, UserActivity.class));
    }

    private void enableEditTexts (boolean isEnabled){
        dealTitleEditText.setEnabled(isEnabled);
        dealDescriptionEditText.setEnabled(isEnabled);
        dealPriceEditText.setEnabled(isEnabled);
        if(isEnabled)
            dealPictureButton.setVisibility(View.VISIBLE);
        else
            dealPictureButton.setVisibility(View.GONE);

        dealPictureButton.setEnabled(isEnabled);
    }
}