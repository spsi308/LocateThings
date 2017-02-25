package cn.spsilab.locatethings;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cn.spsilab.locatethings.Data.LittleItem;
import cn.spsilab.locatethings.tag.TagModule;

/**
 * Created by changrq on 17-2-18.
 */

public class EditItemDialog extends AddItemDialog implements View.OnClickListener{
    private LittleItem specifyItem;
    private EditItemDoneHandler mEditDoneHandler;

    public interface EditItemDoneHandler {
        void onEditDone(LittleItem item);
    }

    private void fillCurrentItemInfo() {
        // set current item info.
        mInputNameEditText.setText(specifyItem.getItemName());

        TagModule bindModule = specifyItem.getBindTagModule();

        mInputModuleIdEditText.setText(String.valueOf(bindModule.getModuleId()));
        mInputModuleMacEditText.setText(bindModule.getModuleMAC());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        specifyItem = (LittleItem) getArguments().getSerializable("item");

        Fragment jumpFrom = this.getTargetFragment();
        if (jumpFrom instanceof EditItemDoneHandler) {
            mEditDoneHandler = (EditItemDoneHandler) jumpFrom;
        } else {
            throw new RuntimeException(jumpFrom.getClass().toString()
                    + "should implement EditDoneHandler");
        }
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
        View view = super.onCreateView(inflater, container, savedInstanceState);

        fillCurrentItemInfo();

        return view;
    }

    @Override
    public void onClick(View v) {
        int clickedId = v.getId();
        switch (clickedId) {
            case R.id.btn_add_or_edit_item_confirm:
                LittleItem updateItem = getUserInput();
                if (updateItem != null) {
                    dismiss();
                    mEditDoneHandler.onEditDone(updateItem);
                }

                break;

            case R.id.btn_add_or_edit_item_cancel:
                dismiss();
                break;

            case R.id.btn_add_or_edit_search_module:

        }

    }

}
