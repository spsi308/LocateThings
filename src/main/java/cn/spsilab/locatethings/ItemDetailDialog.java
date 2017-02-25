package cn.spsilab.locatethings;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cn.spsilab.locatethings.Data.LittleItem;
import cn.spsilab.locatethings.bluetooth.BluetoothConstants;
import cn.spsilab.locatethings.bluetooth.BluetoothService;
import cn.spsilab.locatethings.bluetooth.SelectStationDialog;
import cn.spsilab.locatethings.tag.TagModule;
import cn.spsilab.locatethings.tag.TagModuleOperateService;
import cn.spsilab.locatethings.tag.TagProcessingDialog;

/**
 * Created by changrq on 17-2-18.
 */

public class ItemDetailDialog extends DialogFragment implements
        View.OnClickListener,
        EditItemDialog.EditItemDoneHandler {

    private Button mDeleteItemButton;
    private Button mEditItemButton;
    private Button mCallItemButton;

    private TextView mItemNameTextView;
    private TextView mItemModuleIdTextView;
    private TextView mItemModuleMacTextView;

    private LittleItem specifyItem;
    private int inArrayListPosi;

    private ItemOperateHandler mItemOperateHandler;

    @Override
    public void onAttach(Context context) {

        if (context instanceof ItemOperateHandler ) {
            mItemOperateHandler = (ItemOperateHandler ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implements ItemOpertateHandler");
        }
        super.onAttach(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof ItemOperateHandler ) {
            mItemOperateHandler = (ItemOperateHandler ) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + "must implements ItemOperateHandler");
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        specifyItem = (LittleItem) getArguments().getSerializable("item");
        inArrayListPosi = getArguments().getInt("inListPosi");

        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.dialog_item_detail, container, false);

        // Bind view component.
        mDeleteItemButton = (Button) view.findViewById(R.id.btn_item_detail_delete);
        mEditItemButton = (Button) view.findViewById(R.id.btn_item_detail_edit);
        mCallItemButton = (Button) view.findViewById(R.id.btn_item_detail_call);

        mItemNameTextView = (TextView) view.findViewById(R.id.text_item_detail_item_name);
        mItemModuleIdTextView = (TextView) view.findViewById(R.id.text_item_detail_module_id);
        mItemModuleMacTextView = (TextView) view.findViewById(R.id.text_item_detail_module_mac);

        // set item info.
        mItemNameTextView.setText(specifyItem.getItemName());
        mItemModuleMacTextView.setText(specifyItem.getBindTagModule().getModuleMAC());
        mItemModuleIdTextView.setText(String.valueOf(specifyItem.getBindTagModule().getModuleId()));

        // set clickListener.
        mDeleteItemButton.setOnClickListener(this);
        mEditItemButton.setOnClickListener(this);
        mCallItemButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        int clickedId = v.getId();
        switch (clickedId) {
            case R.id.btn_item_detail_delete:
                // TODO: 17-2-18 need to add a alert dialog.
                dismiss();
                mItemOperateHandler.onItemRemove(inArrayListPosi);

                break;

            case R.id.btn_item_detail_edit:
                FragmentManager fm = getFragmentManager();
                EditItemDialog editItemDialog= new EditItemDialog();
                editItemDialog.setTargetFragment(this, 1);

                // attach data to editDialog.
                Bundle args = new Bundle();
                args.putSerializable("item", specifyItem);
                editItemDialog.setArguments(args);

                editItemDialog.show(fm, "itemDetailDialog");
                break;

            case R.id.btn_item_detail_call:
                TagProcessingDialog tagProcessingDialog = new TagProcessingDialog();
                args = new Bundle();
                args.putString("processingText" , "呼叫标签中");
                args.putString("targetTagMac" ,mItemModuleMacTextView.getText().toString());

                tagProcessingDialog.setArguments(args);
                tagProcessingDialog.show(getFragmentManager(), "tagProcessingDialog");
        }
    }

    @Override
    public void onEditDone(LittleItem item) {
        boolean hasChanged = false;

        // check if the item's name has been updated..
        if (specifyItem.getItemName().equals(item.getItemName())) {
            item.setItemName(null);
        } else {
            mItemNameTextView.setText(item.getItemName());
            hasChanged = true;
        }

        TagModule bindModule = item.getBindTagModule();
        // check if the item's module id has been updated.
        if (bindModule.getModuleMAC().equals(specifyItem.getBindTagModule().getModuleMAC())) {
            item.setBindTagModule(null);
        } else {
            mItemModuleMacTextView.setText(bindModule.getModuleMAC());
            hasChanged = true;
        }

        // if some info has changed, then invoke to handler to apply change in database.
        if (hasChanged) {
            mItemOperateHandler.onItemChange(inArrayListPosi, item);
        }
    }
}
