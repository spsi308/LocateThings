package cn.spsilab.locatethings;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import cn.spsilab.locatethings.module.ResponseResult;
import cn.spsilab.locatethings.module.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserInfoFragment extends Fragment implements View.OnClickListener {

    //    private Button editBtn, editDoneBtn;
    private TextView idTextView, userNameTextView, phoneTextView;
    private EditText userNameEditText, phoneTextEdit;
    private ImageView headerImg;
    private View showLayout;
    private View editLayout;
    private User user;
    private StatusApplication statusApplication;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        showLayout = view.findViewById(R.id.layout_info_show);
        editLayout = view.findViewById(R.id.layout_info_edit);

        view.findViewById(R.id.btn_info_edit).setOnClickListener(this);
        view.findViewById(R.id.btn_info_edit_done).setOnClickListener(this);

        idTextView = (TextView) view.findViewById(R.id.text_info_id);
        userNameTextView = (TextView) view.findViewById(R.id.text_info_user_name);
        phoneTextView = (TextView) view.findViewById(R.id.text_info_phone);

        userNameEditText = (EditText) view.findViewById(R.id.edit_info_user_name);
        phoneTextEdit = (EditText) view.findViewById(R.id.edit_info_phone);

        headerImg = (ImageView) view.findViewById(R.id.img_info_user_header);

        init();

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_info_edit:
                showLayout.setVisibility(View.INVISIBLE);
                editLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_info_edit_done:
                String name = userNameEditText.getText().toString();
                String phone = phoneTextEdit.getText().toString();
                if (!TextUtils.isEmpty(name)
                        && !TextUtils.isEmpty(phone)
                        && (!name.equals(user.getName())
                        || !phone.equals(user.getPhone()))) {
                    // update
                    user.setName(name);
                    user.setPhone(phone);
                    update();
                }
                break;
            default:
        }
    }


    private void init() {
        statusApplication = (StatusApplication) getContext().getApplicationContext();
        if (statusApplication.getLoginStatus() == getResources().getInteger(R.integer.LOGIN)) {
            user = statusApplication.getUser();
            if (user == null) {
                user = new User();
            }
            idTextView.setText("ID:" + String.valueOf(user.getId()));
            userNameTextView.setText("Name:" + user.getName());
            userNameEditText.setText(user.getName());
            phoneTextView.setText("Phone:" + user.getPhone());
            phoneTextEdit.setText(user.getPhone());
            NetworkService.getInstance().getPicture(user.getPhoto(), headerImg, R.drawable.ab_android);
        }
    }

    private void update() {
        APIService apiService = NetworkService.getInstance().getService(APIService.class);
        Call<ResponseResult<Map<String, Object>>> call = apiService.updateUserInfo(user);
        call.enqueue(new Callback<ResponseResult<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseResult<Map<String, Object>>> call, Response<ResponseResult<Map<String, Object>>> response) {
                ResponseResult<Map<String, Object>> responseResult = response.body();
                if (responseResult.getStatus() == getResources().getInteger(R.integer.UPDATE_SUCCESS)) {
                    Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                    userNameTextView.setText(user.getName());
                    phoneTextView.setText(user.getPhone());
                    showLayout.setVisibility(View.VISIBLE);
                    editLayout.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getContext(), responseResult.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(getContext(), "update failed, may cause by network", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
