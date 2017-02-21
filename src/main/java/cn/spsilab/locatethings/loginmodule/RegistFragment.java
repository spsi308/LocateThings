package cn.spsilab.locatethings.loginmodule;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import cn.spsilab.locatethings.NetworkService;
import cn.spsilab.locatethings.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "Regist";

    private EditText userNameEdit,
            phoneEdit,
            passwordEdit,
            passwordRepeatEdit;
    private TextView userNameWarringText,
            phoneWarringText,
            passwordWarringText,
            passwordRepeatWarringText;
/*    Button clearUserNameBtn,
            clearPhoneBtn,
            clearPasswordBtn,
            clearPasswordRepeatBtn,
            registBtn;*/

    public RegistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_regist, container, false);

        userNameEdit = (EditText) view.findViewById(R.id.edit_regist_user_name);
        phoneEdit = (EditText) view.findViewById(R.id.edit_regist_phone);
        passwordEdit = (EditText) view.findViewById(R.id.edit_regist_password);
        passwordRepeatEdit = (EditText) view.findViewById(R.id.edit_regist_password_repeat);

        userNameWarringText = (TextView) view.findViewById(R.id.text_regist_user_name_warring);
        phoneWarringText = (TextView) view.findViewById(R.id.text_regist_phone_warring);
        passwordWarringText = (TextView) view.findViewById(R.id.text_regist_password_warring);
        passwordRepeatWarringText = (TextView) view.findViewById(R.id.text_regist_password_repeat_warring);

        view.findViewById(R.id.btn_regist_clear_user_name).setOnClickListener(this);
        view.findViewById(R.id.btn_regist_clear_user_phone).setOnClickListener(this);
        view.findViewById(R.id.btn_regist_clear_user_password).setOnClickListener(this);
        view.findViewById(R.id.btn_regist_clear_user_password_repeat).setOnClickListener(this);
        view.findViewById(R.id.btn_regist).setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist_clear_user_name:
                userNameEdit.setText("");
                break;
            case R.id.btn_regist_clear_user_phone:
                phoneEdit.setText("");
                break;
            case R.id.btn_regist_clear_user_password:
                passwordEdit.setText("");
                passwordWarringText.setText("");
                passwordRepeatWarringText.setText("");
                break;
            case R.id.btn_regist_clear_user_password_repeat:
                passwordRepeatEdit.setText("");
                passwordWarringText.setText("");
                passwordRepeatWarringText.setText("");
                break;
            case R.id.btn_regist:
                if (checkTextFormat()) {
                    regist();
                }
            default: {
            }
        }
    }

    private boolean checkTextFormat() {

        if (userNameEdit.getText().length() == 0
                || phoneEdit.getText().length() == 0
                || passwordEdit.getText().length() == 0
                || passwordRepeatEdit.getText().length() == 0) {
            return false;
        }

        if (!passwordEdit.getText().toString().matches("^[a-zA-Z]\\w{5,17}$")) {
            passwordWarringText.setText("密码长度6-18,必须以字母开头只能包含字母，数字和下划线");
            return false;
        }
        if (!passwordRepeatEdit.getText().toString().equals(passwordEdit.getText().toString())) {
            passwordRepeatWarringText.setText("密码不相同");
            return false;
        }

        return true;
    }


    private void regist() {
        Log.d(TAG, "regist");
        String userName = userNameEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        NetworkService.getInstance().regist(userName, phone, password, getContext());

    }

}
