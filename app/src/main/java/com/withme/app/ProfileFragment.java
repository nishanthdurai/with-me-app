package com.withme.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentNavigationViewModel navigationViewModel;

    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        navigationViewModel = new ViewModelProvider(requireActivity()).get(FragmentNavigationViewModel.class);
        view.findViewById(R.id.edit_profile_button).setOnClickListener(v ->
                navigationViewModel.setFragmentToLoad(EditProfileFragment.class));

        if (user.getDisplayName() == null) {
            ((TextView) view.findViewById(R.id.profile_name)).setText(user.getEmail());
        } else {
            ((TextView) view.findViewById(R.id.profile_name)).setText(user.getDisplayName());
        }

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