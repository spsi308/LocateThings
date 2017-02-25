package cn.spsilab.locatethings.tag;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.spsilab.locatethings.LocateThings;
import cn.spsilab.locatethings.R;
import cn.spsilab.locatethings.bluetooth.BluetoothConstants;
import cn.spsilab.locatethings.bluetooth.BluetoothService;
import cn.spsilab.locatethings.bluetooth.SelectStationDialog;

/**
 * Created by changrq on 17-2-20.
 */


public class SelectTagModuleDialog extends DialogFragment implements
        ModuleRecyclerAdapter.ModuleSelectHandler,
        TagModuleOperateService.AddTagHandler{

    private final String TAG = SelectTagModuleDialog.class.toString();

    private TextView mProcessingTextView;
    private ProgressBar mProcessingProgressBar;
    private Button mRetryButton;

    private RecyclerView mModuleListRecyclerView;
    private ModuleRecyclerAdapter mModuleListAdapter;

    protected TagModuleOperateService mTagMoudleService;

    private SelectModuleDoneHandler mSelectDoneHandler;

    private boolean firstOpen;

    private BroadcastReceiver searchFinishedBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mProcessingProgressBar.getVisibility() == View.VISIBLE) {
                mProcessingTextView.setText("未能找到模块");
                mRetryButton.setVisibility(View.VISIBLE);
                mProcessingProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    };
    public  interface SelectModuleDoneHandler {
        void selectModuleDone(TagModule newTag);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getClass() == SelectTagModuleDialog.class) {
            Fragment targetFragment = this.getTargetFragment();
            if (targetFragment instanceof SelectModuleDoneHandler) {
                mSelectDoneHandler = (SelectModuleDoneHandler) this.getTargetFragment();
            } else {
                throw new RuntimeException(targetFragment.getClass().toString()
                        + "should implement "+ SelectModuleDoneHandler.class.toString());
            }
        }

        firstOpen = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(searchFinishedBroadCastReceiver);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: can't unregisterReceiver", e);
        }

        if (mTagMoudleService != null) {
            mTagMoudleService.closeService();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        LocateThings app = (LocateThings) getActivity().getApplication();
        int bluetoothStatus = app.getBluetoothStatus();

        if (bluetoothStatus != BluetoothConstants.STATUS_CONNECTED_TO_RELAY_STATION) {
            Log.d(TAG, "onResume: not bluetooth station.");

            // if bluetooth already exists.
            if (app.getBluetoothService() != null) {
                app.getBluetoothService().stop();
            }

            if (firstOpen) {

                firstOpen = false;
                // start a new station select dialog.
                SelectStationDialog selectStationDialog = new SelectStationDialog();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.remove(this);
                transaction.add(selectStationDialog, "selectStationDialog");
                transaction.addToBackStack(null);


                // Commit the transaction
                transaction.commit();

                Log.d(TAG, "onResume: will select station.");
            } else {

                this.dismiss();
            }

            //selectStationDialog.show(getFragmentManager(), "selectStationDialog");

        } else {

            // get new tagModuleService and start a query.
            BluetoothService mBlueService = ((LocateThings) getActivity().getApplication()).getBluetoothService();
            mTagMoudleService = new TagModuleOperateService(mBlueService, this);
            mTagMoudleService.queryAvailableModule();

            // register a broadcastReceiver to receive timeout broadcast.
            IntentFilter filter = new IntentFilter();
            filter.addAction("ACTION_FINISH_SEARCH_TAG");

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(searchFinishedBroadCastReceiver, filter);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_tag_module, container);
        // bind view.
        mModuleListRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_select_module_tag_module_list);
        mProcessingProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_select_module_processing);
        mProcessingTextView = (TextView) view.findViewById(R.id.text_select_module_processing);
        mRetryButton = (Button) view.findViewById(R.id.btn_select_module_retry);

        // config recyclerView.
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mModuleListAdapter = new ModuleRecyclerAdapter(this);

        mModuleListRecyclerView.setLayoutManager(mLinearLayoutManager);
        mModuleListRecyclerView.setAdapter(mModuleListAdapter);


        // set processing.
        mProcessingTextView.setVisibility(View.VISIBLE);
        mProcessingProgressBar.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessingTextView.setText("寻找标签中.");
                mProcessingProgressBar.setVisibility(View.VISIBLE);
                mRetryButton.setVisibility(View.INVISIBLE);

                mTagMoudleService.queryAvailableModule();
            }
        });

        return view;
    }

    @Override
    public void onAddTag(TagModule newTag) {
        // if the tag is the first tag that to be added in adapter.
        if (mProcessingProgressBar.getVisibility() == View.VISIBLE) {
            mProcessingTextView.setVisibility(View.INVISIBLE);
            mProcessingProgressBar.setVisibility(View.INVISIBLE);
        }

        Log.d(TAG, "onAddTag: adapter add tag!");
        mModuleListAdapter.addModule(newTag);
    }

    /**
     * this method will be invoked when user select a module.
     * and tag mac will be set in moduleId editText. and exit this dialog.
     * @param tagModule
     */
    @Override
    public void onModuleSelect(TagModule tagModule) {
        mSelectDoneHandler.selectModuleDone(tagModule);
        dismiss();
    }

    /**
     * this method will be invoked when user clicked blink button.
     * open a new dialog to indicate blink is in processing.
     * @param tagModule
     */
    @Override
    public void onBinkModule(TagModule tagModule) {

        // start a new thread in tagModuleServiece,
        // blink query will be continuously sending inside the thread
        mTagMoudleService.blinkModule(tagModule);

        // open a processing dialog.
        TagProcessingDialog blinkTagDialog = new TagProcessingDialog();

        // pass hint message.
        Bundle args = new Bundle();
        args.putString("processingText", "标签闪烁中");
        blinkTagDialog.setArguments(args);

        // set tagetFragment.  In order to set FinshHandler to processing dialog.
        blinkTagDialog.setTargetFragment(this, 0);
        blinkTagDialog.show(getFragmentManager(), "blinkTagDialog");
    }
}
