package cn.spsilab.locatethings.loginmodule;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.spsilab.locatethings.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistFragment extends Fragment {


    public RegistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_regist, container, false);
    }

}
