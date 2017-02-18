package cn.spsilab.locatethings;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.spsilab.locatethings.Data.LittleItem;
import cn.spsilab.locatethings.Data.TestData;

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
        mInputModuleIdEditText.setText(String.valueOf(specifyItem.getModuleId()));
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

            case R.id.btn_add_or_edit_select_module:

        }

    }

}
