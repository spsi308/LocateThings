package cn.spsilab.locatethings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.spsilab.locatethings.Data.LittleItem;

/**
 * Created by changrq on 17-2-17.
 */

public class ItemListRecyclerAdapter extends RecyclerView.Adapter<ItemListRecyclerAdapter.ItemAdapterViewHolder> {

    private final ItemAdapterOnClickHandler mClickHandler;
    private ArrayList<LittleItem> mItemsArrayList;

    public ItemListRecyclerAdapter(ItemAdapterOnClickHandler handler) {
        mClickHandler = handler;
    }

    public interface ItemAdapterOnClickHandler {
        void listItemOnClick(int inListPosi);
    }

    public class ItemAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mItemDescTextView;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);
            mItemDescTextView = (TextView) itemView.findViewById(R.id.tv_item_desc_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int inListPosi = getAdapterPosition();
            mClickHandler.listItemOnClick(inListPosi);
        }
    }

    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate layout using parent context;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item_item_description, parent, false);

        // instantiate a ViewHoler.
        return  new ItemAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapterViewHolder holder, int position) {
        // set textView to display corresponding item's name.
        holder.mItemDescTextView.setText("now posi" + position + " " + mItemsArrayList.get(position).getItemName());
    }

    @Override
    public int getItemCount() {
        return mItemsArrayList == null? 0 : mItemsArrayList.size();
    }

    public void setItemsArrayList(ArrayList<LittleItem> mItemsArrayList) {
        this.mItemsArrayList = mItemsArrayList;
    }

    public ArrayList<LittleItem> getItemsArrayList() {
        return mItemsArrayList;
    }

    /**
     * insert item to arraylist and notify adapter data inserted.
     * @param item
     */
    public void adapterListAddItem(LittleItem item) {

        int insertPosi = mItemsArrayList.size();

        mItemsArrayList.add(item);
        notifyItemInserted(insertPosi);
    }

    /**
     * remove item in arrayList by position, and notify data removed.
     * @param posi
     */
    public void adapterListRemoveItem(int posi) {
        mItemsArrayList.remove(posi);

        notifyItemRemoved(posi);
    }

    /**
     * change item in arrayList by position, notify data changed.
     * @param posi
     * @param item
     */
    public void adapterListChangeItem(int posi, LittleItem item) {
        LittleItem afterChanged = mItemsArrayList.get(posi);

        // check if the item actually changed.
        if (item.getItemName() != null) {
            afterChanged.setItemName(item.getItemName());
        }

        if (item.getModuleId() != -1) {
            afterChanged.setModuleId(item.getModuleId());
        }

        notifyItemChanged(posi);
    }
}
