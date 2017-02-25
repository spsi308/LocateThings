package cn.spsilab.locatethings;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class UserInfoFragment extends Fragment implements View.OnClickListener {

    //    private Button editBtn, editDoneBtn;
    private TextView idTextView, userNameTextView, phoneTextView;
    private EditText userNameEditText, phoneTextEdit;
    private ImageView headerImg;
    private View showLayout;
    private View editLayout;
    private User user;
    private StatusApplication statusApplication;
    private boolean editMode = false;
    private GifImageView gifLoader;
    private NetworkService.NetworkCallback networkCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        showLayout = view.findViewById(R.id.layout_info_show);
        editLayout = view.findViewById(R.id.layout_info_edit);

        idTextView = (TextView) view.findViewById(R.id.text_info_id);
        userNameTextView = (TextView) view.findViewById(R.id.text_info_user_name);
        phoneTextView = (TextView) view.findViewById(R.id.text_info_phone);
        userNameEditText = (EditText) view.findViewById(R.id.edit_info_user_name);
        phoneTextEdit = (EditText) view.findViewById(R.id.edit_info_phone);
        headerImg = (ImageView) view.findViewById(R.id.img_info_user_header);
        gifLoader = (GifImageView) view.findViewById(R.id.img_update_user_loader);

        view.findViewById(R.id.btn_info_edit).setOnClickListener(this);
        view.findViewById(R.id.btn_info_edit_done).setOnClickListener(this);
        headerImg.setOnClickListener(this);

        init();

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_info_edit:
                editMode = true;
                showLayout.setVisibility(View.INVISIBLE);
                editLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_info_edit_done:
                editMode = false;
                String name = userNameEditText.getText().toString();
                String phone = phoneTextEdit.getText().toString();
                if (!TextUtils.isEmpty(name)
                        && !TextUtils.isEmpty(phone)
                        && (!name.equals(user.getName())
                        || !phone.equals(user.getPhone()))) {
                    // update
                    User newUser = user;
                    newUser.setName(name);
                    newUser.setPhone(phone);
                    update(newUser);
                    gifLoader.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_info_user_header:
                if (editMode) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, getResources().getInteger(R.integer.SELECT_IMG));
                    gifLoader.setVisibility(View.VISIBLE);
                }
            default:
        }
    }

    /**
     * 当选择头像之后，自动上传
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {

        if (requestCode == getResources().getInteger(R.integer.SELECT_IMG) && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            final Bitmap imgBitMap = PictureUtil.getSmallBitmap(imgUri, 300, 300, getContext());
            headerImg.setImageBitmap(imgBitMap);
            Toast.makeText(getContext(), "select img", Toast.LENGTH_SHORT).show();
            // upload header
            NetworkService.getInstance().uploadImg(imgUri, user.getId(), getResources().getInteger(R.integer.TYPE_USER), getContext(), new NetworkService.NetworkCallback() {
                @Override
                public void onSuccess(ResponseResult result) {
                    gifLoader.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    String imgUrl = ((Map<String, Object>) result.getData()).get("url").toString();
                    user.setPhoto(imgUrl);
                    statusApplication.setUser(user);
                    String imgName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                    //save img in local
                    PictureUtil.saveBitMap(imgBitMap, imgName);
                    networkCallback.onSuccess(result);
                }

                @Override
                public void onFailure(ResponseResult result, Throwable t) {
                    Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    gifLoader.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    /**
     * 初始化操作
     * 1：检测是否登录
     * 2：设置id，设置用户名，手机号，头像
     * 3：加载头像
     */
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
        gifLoader.setVisibility(View.INVISIBLE);

        if (getActivity() instanceof NetworkService.NetworkCallback) {
            networkCallback = (NetworkService.NetworkCallback) getActivity();
        }

    }

    /**
     * update user not include photo
     */
    private void update(final User user) {
        APIService apiService = NetworkService.getInstance().getService(APIService.class);
        Call<ResponseResult<Map<String, Object>>> call = apiService.updateUserInfo(user);
        call.enqueue(new Callback<ResponseResult<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseResult<Map<String, Object>>> call, Response<ResponseResult<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    ResponseResult<Map<String, Object>> responseResult = response.body();
                    if (responseResult.getStatus() == getResources().getInteger(R.integer.UPDATE_SUCCESS)) {
                        Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                        // show text change
                        userNameTextView.setText(user.getName());
                        phoneTextView.setText(user.getPhone());
                        // save in statusApplication
                        StatusApplication statusApplication = (StatusApplication) getContext().getApplicationContext();
                        statusApplication.setUser(user);
                        showLayout.setVisibility(View.VISIBLE);
                        editLayout.setVisibility(View.INVISIBLE);
                        // main thread change the nav username
                        if (networkCallback == null) {
                            networkCallback.onSuccess(responseResult);
                        }
                    } else {
                        Toast.makeText(getContext(), responseResult.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "update failed, may cause by the server", Toast.LENGTH_SHORT).show();
                }
                gifLoader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseResult<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(getContext(), "update failed, may cause by network", Toast.LENGTH_SHORT).show();
                gifLoader.setVisibility(View.INVISIBLE);
            }
        });
    }



}
