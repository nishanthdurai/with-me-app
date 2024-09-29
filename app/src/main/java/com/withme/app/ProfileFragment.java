package com.withme.app;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private FragmentNavigationViewModel navigationViewModel;

    private FirebaseUser user;

    private ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        navigationViewModel = new ViewModelProvider(requireActivity()).get(FragmentNavigationViewModel.class);
        view.findViewById(R.id.edit_profile_button).setOnClickListener(v ->
                navigationViewModel.setFragmentToLoad(EditProfileFragment.class));

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (user.getDisplayName() == null) {
            ((TextView) view.findViewById(R.id.profile_name)).setText(user.getEmail());
        } else {
            ((TextView) view.findViewById(R.id.profile_name)).setText(user.getDisplayName());
        }

        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl())
                    .into((ImageView) view.findViewById(R.id.profile_image));
        }

        // fetch update bio if exists in realtime database
        firebaseDatabase.getReference("users").child(user.getUid()).child("bio")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String bio = task.getResult().getValue(String.class);
                        if (bio != null) {
                            ((TextView) view.findViewById(R.id.tv_user_bio)).setText(bio);
                        }
                    }
                }).addOnFailureListener((err) -> {
                    Toast.makeText(getActivity(), "Failed to fetch bio", Toast.LENGTH_SHORT).show();
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