package com.example.vestackainteligencija2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

import java.util.List;


public class AnimalCustomModelFragment extends Fragment {

    public static final int  PICK_PHOTO=1;
    private Button chooseImage;
    private TextView textView;
    private ImageView imageView;
    private Bitmap photo;

    public AnimalCustomModelFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_animal_custom_model, container, false);
        this.chooseImage=v.findViewById(R.id.Image_classification_image_from_gallery_button2);
        this.imageView=v.findViewById(R.id.image_classification_image_view2);
        this.textView=v.findViewById(R.id.custommodel_text_view);
        this.chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_PHOTO);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage=null;
        if(data!=null){
            selectedImage=data.getData();

            try {
                photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            }catch (Exception e){
                Log.d("greska","greska");
                return;
            }
        }
        try{
            ExifInterface exif=new ExifInterface(getActivity().getContentResolver().openInputStream(selectedImage));
            int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            Matrix matrix=new Matrix();
            if(orientation==6){
                matrix.postRotate(90);
            }
            else if(orientation==3){
                matrix.postRotate(180);
            }
            else if(orientation==8){
                matrix.postRotate(270);
            }
            photo =Bitmap.createBitmap(photo,0,0, photo.getWidth(),photo.getHeight(),matrix,true);

        }catch(Exception e){

        }
        this.imageView.setImageBitmap(photo);
        this.imageView.setVisibility(View.VISIBLE);
        setUpFirebase();
    }

    public void setUpFirebase(){
        AutoMLImageLabelerLocalModel localModel=
                new AutoMLImageLabelerLocalModel.Builder()
                .setAssetFilePath("model2/manifest.json")
                .build();

        AutoMLImageLabelerOptions autoMLImageLabelerOptions=new AutoMLImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.05f)
                .build();

        ImageLabeler imageLabeler= ImageLabeling.getClient(autoMLImageLabelerOptions);
        InputImage image=InputImage.fromBitmap(photo,0);

        imageLabeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> imageLabels) {
                        textView.setVisibility(View.VISIBLE);
                        for(ImageLabel label:imageLabels){
                            textView.setText(textView.getText().toString()+"\n"+label.getText()+"-confidence:"+label.getConfidence());
                        }
                        textView.setText(textView.getText().toString()+Integer.toString(imageLabels.size()));


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textView.setText("Doslo je do greske!");
                    }
                });

    }
}