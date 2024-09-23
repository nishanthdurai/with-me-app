package com.withme.app;// FragmentNavigationViewModel.java
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FragmentNavigationViewModel extends ViewModel {

    private final MutableLiveData<Class<? extends Fragment>> fragmentToLoad = new MutableLiveData<>();

    public void setFragmentToLoad(Class<? extends Fragment> fragmentClass) {
        fragmentToLoad.setValue(fragmentClass);
    }

    public LiveData<Class<? extends Fragment>> getFragmentToLoad() {
        return fragmentToLoad;
    }
}
