package com.withme.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FragmentNavigationViewModel navigationViewModel;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        // login user when activity open.
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting user please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword("nishanthduraisamy@gmail.com", "Muthu@1234")
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("wmaError", "onCreate: ", task.getException());
                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });

        navigationViewModel = new ViewModelProvider(this).get(FragmentNavigationViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }

        // Find the BottomNavigationView by ID
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Set the OnNavigationItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.nav_home) {// Handle the "Home" action
                        loadFragment(new HomeFragment());
                        return true;
                    }
                    if (item.getItemId() == R.id.nav_profile) {// Handle the "Home" action
                        loadFragment(new ProfileFragment());
                        return true;
                    }
                    return false;
                });

        navigationViewModel.getFragmentToLoad().observe(this, fragmentClass -> {
            if (fragmentClass != null) {
                // Perform the fragment transaction
                try {
                    Fragment fragment = fragmentClass.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment)
                            .addToBackStack(null)
                            .commit();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // Load the fragment into the fragment container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}