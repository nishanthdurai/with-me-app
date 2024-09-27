package com.withme.app;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.withme.app.models.UserBio;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private EditText etName;
    private EditText etBio;
    private EditText etPassword;
    private Button btnUpdate;
    private CircleImageView ivProfileImage;
    private TextView profileName;
    private TextView tvChangeImage;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        etName = view.findViewById(R.id.et_name_display);
        etBio = view.findViewById(R.id.et_bio_display);
        etPassword = view.findViewById(R.id.et_password_display);
        btnUpdate = view.findViewById(R.id.btn_update);
        ivProfileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        tvChangeImage = view.findViewById(R.id.change_profile_picture);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("profile_pictures");
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Set display name or email as fallback
        if (user != null && user.getDisplayName() != null) {
            etName.setText(user.getDisplayName());
            profileName.setText(user.getDisplayName());
        } else {
            profileName.setText(Objects.requireNonNull(user).getEmail());
        }

        // Load current profile image
        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(ivProfileImage);
        }

        // Show progress dialog while fetching bio
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait while we load your profile");
        progressDialog.show();

        // Fetch user bio from Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                    progressDialog.dismiss();
                    if (documentSnapshot.exists()) {
                        UserBio userBio = documentSnapshot.toObject(UserBio.class);
                        etBio.setText(userBio.getBio());
                    }
                }).addOnFailureListener(err -> {
                    progressDialog.dismiss();
                    Log.e("WithMeErr", "Error fetching bio: ", err);
                    Toast.makeText(getActivity(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                });

        // Set up profile image click listener to select new image
        tvChangeImage.setOnClickListener(v -> openFileChooser());

        // Set up update button click listener
        btnUpdate.setOnClickListener(view1 -> validateAndUpdateProfile());

        return view;
    }

    // Open file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            ivProfileImage.setImageURI(imageUri); // Preview the selected image
        }
    }

    private void validateAndUpdateProfile() {
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        // Update display name if changed
        if (!etName.getText().toString().equals(user.getDisplayName())) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etName.getText().toString())
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    profileName.setText(user.getDisplayName()); // Refresh name
                }
            });
        }

        // Update password if the user wants to change it (with validation)
        if (!etPassword.getText().toString().isEmpty()) {
            if (etPassword.getText().toString().length() >= 6) {
                user.updatePassword(etPassword.getText().toString()).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Password update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
        }

        // Update bio in Firestore
        if (!etBio.getText().toString().isEmpty()) {
            UserBio userBio = new UserBio(user.getUid(), etBio.getText().toString());
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid()).set(userBio);
        }

        // Upload image if selected
        if (imageUri != null) {
            uploadImageToFirebaseStorage();
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Upload image to Firebase Storage and update profile
    private void uploadImageToFirebaseStorage() {
        StorageReference fileReference = storageReference.child(user.getUid() + ".jpg");

        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                updateProfileImageUri(uri.toString());
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    // Update the user's profile image URL in Firebase Authentication
    private void updateProfileImageUri(String uri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(uri))
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
