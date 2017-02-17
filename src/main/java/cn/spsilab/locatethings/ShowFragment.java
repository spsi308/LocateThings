package cn.spsilab.locatethings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Feng on 17/01/29.
 */

public class ShowFragment extends Fragment {

    private String showText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            showText = getArguments().getString("show_text");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show,container,false);
        TextView textView = (TextView) view.findViewById(R.id.show_text);
        textView.setText(showText);
        return view;
    }
}
