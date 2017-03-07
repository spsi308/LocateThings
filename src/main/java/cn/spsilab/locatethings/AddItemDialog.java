package cn.spsilab.locatethings;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.spsilab.locatethings.Data.LittleItem;
import cn.spsilab.locatethings.Data.TestData;
import cn.spsilab.locatethings.tag.SelectTagModuleDialog;
import cn.spsilab.locatethings.tag.TagModule;


/**
 * Created by changrq on 17-2-18.
 */

public class AddItemDialog extends DialogFragment
        implements View.OnClickListener,
        SelectTagModuleDialog.SelectModuleDoneHandler{
    private static final int MAC_LENGTH = 16;
    private final String TAG = AddItemDialog.class.toString();

    private LittleItem specifyItem;

    EditText mInputNameEditText;
    EditText mInputModuleIdEditText;
    EditText mInputModuleMacEditText;


    private Button mConfirmButton;
    private Button mCancelButton;
    private Button mSearchModuleButton;
    private Button mSelectExistModuleButton;

    private TextView mErrInputNameTextView;
    private TextView mErrInputModuleIdTextView;

    private boolean illegalModuleId;
    private boolean illegalItemName;

    private ItemOperateHandler mItemOperateHanler;

    /**
     * get user input from editText, if input illegal, return null,
     * otherwise return a littleItem object.
     * @return
     */
    protected LittleItem getUserInput() {
        long moduleId;
        String inputName = mInputNameEditText.getText().toString();
        String moduleIdStr = mInputModuleIdEditText.getText().toString();
        String moduleMacStr = mInputModuleMacEditText.getText().toString();

        LittleItem item = new LittleItem();
        TagModule tag = new TagModule();



        // check tag module info.
        if (moduleIdStr.length() == 0 && moduleMacStr.length() == 0) {
            mErrInputModuleIdTextView.setText("模块ID 和 模块地址不能都空");
            illegalItemName = true;
        } else if (moduleIdStr.length() == 0) {
            // mac not null.
            if (moduleMacStr.length() != MAC_LENGTH) {
                mErrInputModuleIdTextView.setText("模块MAC应为16位");
                mInputModuleIdEditText.setSelectAllOnFocus(true);
                mInputModuleIdEditText.clearFocus();
                illegalItemName = true;
            }
        }

        // check item name.
        if (inputName.length() == 0) {
            mErrInputNameTextView.setText("物品名不能为空！");
            illegalItemName = true;
        }

        if (illegalModuleId || illegalItemName) {
            return null;
        }

        // set module. info.
        if(moduleIdStr.length() != 0) {
            tag.setModuleId(Long.parseLong(moduleIdStr));
        }

        tag.setModuleMAC(moduleMacStr);

        // set item info.
        item.setUserId(TestData.fakeUserId);
        item.setItemName(inputName);
        item.setBindTagModule(tag);

        return item;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_or_edit_item, container, false);

        // Bind view component.
        mInputNameEditText = (EditText) view.findViewById(R.id.edit_add_or_edit_item_name);
        mInputModuleIdEditText = (EditText) view.findViewById(R.id.edit_add_or_edit_module_id);
        mInputModuleMacEditText = (EditText) view.findViewById(R.id.edit_add_or_edit_module_mac);


        mConfirmButton = (Button) view.findViewById(R.id.btn_add_or_edit_item_confirm);
        mCancelButton = (Button) view.findViewById(R.id.btn_add_or_edit_item_cancel);
        mSelectExistModuleButton = (Button) view.findViewById(R.id.btn_add_or_edit_select_used_module);

        mSearchModuleButton = (Button) view.findViewById(R.id.btn_add_or_edit_search_module);

        mErrInputNameTextView = (TextView) view.findViewById(R.id.text_add_or_edit_item_err_input_name);
        mErrInputModuleIdTextView = (TextView) view.findViewById(R.id.text_add_or_edit_item_err_module_id);

        // set click.
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mSearchModuleButton.setOnClickListener(this);


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
        // handle illegal input reminder text.
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

            case R.id.btn_add_or_edit_search_module:

                SelectTagModuleDialog selectTagDialog = new SelectTagModuleDialog();
                selectTagDialog.setTargetFragment(this, 0);
                selectTagDialog.show(getFragmentManager(), "selectTagModuleDialog");
                break;
            case R.id.btn_add_or_edit_item_cancel:
                dismiss();
        }
    }

    @Override
    public void selectModuleDone(TagModule newTag) {
        mInputModuleMacEditText.setText(newTag.getModuleMAC());
    }
}
