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
public class CheckCodeFragment extends Fragment {


    public CheckCodeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_check_code, container, false);
    }

}
