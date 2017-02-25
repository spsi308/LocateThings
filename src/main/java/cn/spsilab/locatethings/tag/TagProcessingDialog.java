package cn.spsilab.locatethings.tag;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.spsilab.locatethings.R;

/**
 * Created by changrq on 17-2-22.
 */


public class TagProcessingDialog extends SelectTagModuleDialog {

    private String processingText;

    private TagModule targetTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set dialog not cancelable, only when button clicked, dialog dismiss.
        setCancelable(false);

        // get hint string.
        processingText = getArguments().getString("processingText");

        // get target tag mac.
        targetTag  = new TagModule();
        targetTag.setModuleMAC(getArguments().getString("targetTagMac"));

    }

    @Override
    public void onResume() {
        super.onResume();

        // start blink process.
        if (mTagMoudleService != null) {
            mTagMoudleService.blinkModule(targetTag);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tag_processing, container);

        // bind view component.
        Button mBackButton = (Button) view.findViewById(R.id.btn_tag_processing_back);
        TextView mHintTextView = (TextView) view.findViewById(R.id.text_tag_processing);

        // set hint text.
        mHintTextView.setText(processingText);

        // set button click.
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // stop processing.
                mTagMoudleService.clearRunningThead();
            }
        });
        return view;
    }
}
