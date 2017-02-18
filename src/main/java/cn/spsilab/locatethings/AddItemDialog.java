package cn.spsilab.locatethings;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.spsilab.locatethings.Data.LittleItem;
import cn.spsilab.locatethings.Data.TestData;


/**
 * Created by changrq on 17-2-18.
 */

public class AddItemDialog extends DialogFragment implements View.OnClickListener{
    private final String TAG = AddItemDialog.class.toString();

    private LittleItem specifyItem;

    EditText mInputNameEditText;
    EditText mInputModuleIdEditText;

    private Button mConfirmButton;
    private Button mCancelButton;
    private Button mSelectModuleButton;

    private TextView mErrInputNameTextView;
    private TextView mErrInputModuleIdTextView;

    private boolean illegalModuleId;
    private boolean illegalItemName;

    private ItemOperateHandler mItemOperateHanler;

    protected LittleItem getUserInput() {
        long moduleId;
        String inputName = mInputNameEditText.getText().toString();
        String moduleIdStr = mInputModuleIdEditText.getText().toString();

        long tmpModuleId = -1;
        try {
            tmpModuleId = Long.parseLong(moduleIdStr);
        } catch (Exception e) {
            Log.e(TAG, "onClick: illegal module id.", e);
        }
        moduleId = tmpModuleId;

        if (moduleId == -1) {
            mErrInputModuleIdTextView.setText("模块ID输入有误！");
            mInputModuleIdEditText.setSelectAllOnFocus(true);
            mInputModuleIdEditText.clearFocus();
            illegalModuleId= true;
        }

        if (inputName.length() == 0) {
            mErrInputNameTextView.setText("物品名不能为空！");
            illegalItemName = true;
        }
        if (illegalModuleId || illegalItemName) {
            return null;
        }
        LittleItem item = new LittleItem();
        item.setUserId(TestData.fakeUserId);
        item.setModuleId(moduleId);
        item.setItemName(inputName);

        return item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_or_edit_item, container, false);

        // Bind view component.
        mInputNameEditText = (EditText) view.findViewById(R.id.edit_add_or_edit_item_name);
        mInputModuleIdEditText = (EditText) view.findViewById(R.id.edit_add_or_edit_module_id);

        mConfirmButton = (Button) view.findViewById(R.id.btn_add_or_edit_item_confirm);
        mCancelButton = (Button) view.findViewById(R.id.btn_add_or_edit_item_cancel);
        mSelectModuleButton = (Button) view.findViewById(R.id.btn_add_or_edit_select_module);

        mErrInputNameTextView = (TextView) view.findViewById(R.id.text_add_or_edit_item_err_input_name);
        mErrInputModuleIdTextView = (TextView) view.findViewById(R.id.text_add_or_edit_item_err_module_id);

        // set click.
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mSelectModuleButton.setOnClickListener(this);


        // If user had input illegal name or module id, and changed the input text latter, clear
        // alert message.
        mInputNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (illegalItemName) {
                    illegalItemName = false;
                    mErrInputNameTextView.setText("");
                }
            }
        });

        mInputModuleIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (illegalModuleId) {
                    illegalModuleId = false;
                    mErrInputModuleIdTextView.setText("");
                    mInputModuleIdEditText.setSelectAllOnFocus(false);
                }
            }
        });

        return view;
    }

    @Override
    public final void onAttach(Context context) {
        if (context instanceof ItemOperateHandler) {
            mItemOperateHanler = (ItemOperateHandler) context;
        } else {
            throw new RuntimeException(context.getClass().toString()
                    + "should implemnt ItemOperateHandler");
        }
        super.onAttach(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        if (activity instanceof ItemOperateHandler) {
            mItemOperateHanler = (ItemOperateHandler) activity;
        } else {
            throw new RuntimeException(activity.getClass().toString()
                    + "should implement ItemOperateHandler");
        }
        super.onAttach(activity);
    }

    @Override
    public void onClick(View v) {
        int clickedId = v.getId();

        switch (clickedId) {

            case R.id.btn_add_or_edit_item_confirm:
                LittleItem newItem = getUserInput();
                if (newItem != null) {
                    dismiss();
                    mItemOperateHanler.onAddItem(newItem);
                }
                break;

            case R.id.btn_add_or_edit_item_cancel:
                dismiss();
        }
    }
}
