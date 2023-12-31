package com.example.anyang_setup.Resume.helper;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.anyang_setup.Resume.datamodel.Resume;

/**
 * Created by ibrahim on 1/19/18.
 */

abstract public class ResumeFragment extends Fragment {

    public static final String ARGUMENT_RESUME = "resume";

    public void setResume(Resume resume) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_RESUME, resume);
        setArguments(bundle);
    }

    public Resume getResume() {
        return getArguments().getParcelable(ARGUMENT_RESUME);
    }

    public abstract void onViewCreated(@NonNull LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState);
}
