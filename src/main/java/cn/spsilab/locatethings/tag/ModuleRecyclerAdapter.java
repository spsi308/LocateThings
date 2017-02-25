package cn.spsilab.locatethings.tag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cn.spsilab.locatethings.R;

/**
 * Created by changrq on 17-2-20.
 */

public class ModuleRecyclerAdapter extends RecyclerView.Adapter<ModuleRecyclerAdapter.ModuleViewHolder> {

    private ArrayList<TagModule> mTagModuleArrayList;

    private ModuleSelectHandler mClickHandler;

    public ModuleRecyclerAdapter(ModuleSelectHandler handler) {
        mTagModuleArrayList = new ArrayList<>();

        if (handler instanceof ModuleSelectHandler) {
            mClickHandler = handler;
        } else {
            throw new RuntimeException(handler.getClass().toString()
                    + "must implement " + ModuleSelectHandler.class.toString());
        }
    }

    public interface ModuleSelectHandler {
        void onModuleSelect(TagModule tagModule);
        void onBinkModule(TagModule tagModule);
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder {
        private TextView mModuleInfoTextView;
        private Button mBlinkModuleButton;
        private Button mSelectModuleButton;

        public ModuleViewHolder(View itemView) {
            super(itemView);
            // bind view.
            mModuleInfoTextView = (TextView) itemView.findViewById(R.id.text_recyclerview_item_module_name);
            mBlinkModuleButton = (Button) itemView.findViewById(R.id.btn_recyclerview_item_blink_module);
            mSelectModuleButton = (Button) itemView.findViewById(R.id.btn_recyclerview_item_select_module);

            mSelectModuleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onModuleSelect(mTagModuleArrayList.get(getAdapterPosition()));
                }
            });

            mBlinkModuleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onBinkModule(mTagModuleArrayList.get(getAdapterPosition()));
                }
            });

        }

        private void setModuleInfo(TagModule module) {
            mModuleInfoTextView.setText(String.valueOf(module.getModuleId()) + "\n" +
                    module.getModuleMAC());
        }
    }

    @Override
    public ModuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item_tag_module, parent, false);

        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModuleViewHolder holder, int position) {
        holder.setModuleInfo(mTagModuleArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTagModuleArrayList == null ? 0 : mTagModuleArrayList.size();
    }

    public void addModule(TagModule tagModule) {
        mTagModuleArrayList.add(0, tagModule);

        notifyItemInserted(0);
    }
}
