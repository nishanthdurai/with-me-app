package com.withme.app;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private TextView userName;
    private EditText etDescription;
    private CircleImageView ivProfileImage;
    private Button btnPost;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        etDescription = view.findViewById(R.id.et_name_display);
        ivProfileImage = view.findViewById(R.id.iv_profile_image);
        btnPost = view.findViewById(R.id.btn_post);
        userName = view.findViewById(R.id.tv_user_name);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("profile_pictures");
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Set display name or email as fallback
        if (user != null && user.getDisplayName() != null) {
            userName.setText(user.getDisplayName());
        } else {
            userName.setText(Objects.requireNonNull(user).getEmail());
        }

        // Load current profile image
        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(ivProfileImage);
        }

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
