package com.withme.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ProfileFragment extends Fragment {

    private FragmentNavigationViewModel navigationViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        navigationViewModel = new ViewModelProvider(requireActivity()).get(FragmentNavigationViewModel.class);
        view.findViewById(R.id.edit_profile_button).setOnClickListener(v ->
                navigationViewModel.setFragmentToLoad(EditProfileFragment.class));

        return view;
    }
}