package com.withme.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.withme.app.models.UserBio;

public class ProfileFragment extends Fragment {

    private FragmentNavigationViewModel navigationViewModel;

    private FirebaseUser user;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        navigationViewModel = new ViewModelProvider(requireActivity()).get(FragmentNavigationViewModel.class);
        view.findViewById(R.id.edit_profile_button).setOnClickListener(v ->
                navigationViewModel.setFragmentToLoad(EditProfileFragment.class));

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait while do our job");
        progressDialog.show();

        if (user.getDisplayName() == null) {
            ((TextView) view.findViewById(R.id.profile_name)).setText(user.getEmail());
        } else {
            ((TextView) view.findViewById(R.id.profile_name)).setText(user.getDisplayName());
        }

        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl())
                    .into((ImageView) view.findViewById(R.id.profile_image));
        }

        // fetch update bio if exists in fire store
        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                    progressDialog.dismiss();
                    if (documentSnapshot.exists()) {
                        UserBio userBio = documentSnapshot.toObject(UserBio.class);
                        ((TextView) view.findViewById(R.id.tv_user_bio)).setText(userBio.getBio());
                    }
                }).addOnFailureListener((err) -> {
                    progressDialog.dismiss();
                    Log.e("WithMeErr", "onCreateView: ", err);
                });

        view.findViewById(R.id.logout_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            // TODO: take user to logout screen
            Toast.makeText(getActivity(), "Logout success", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}