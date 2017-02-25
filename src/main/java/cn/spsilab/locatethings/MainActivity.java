package cn.spsilab.locatethings;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import cn.spsilab.locatethings.Data.LittleItem;
import cn.spsilab.locatethings.Data.LocateThingsDatabase;
import cn.spsilab.locatethings.Data.TestData;
import cn.spsilab.locatethings.bluetooth.SelectStationDialog;

public class MainActivity extends AppCompatActivity implements
        ItemListRecyclerAdapter.ItemAdapterOnClickHandler, ItemOperateHandler{
    private final static String TAG = MainActivity.class.toString();

    private DrawerLayout drawerLayout;

    public Toolbar toolbar;

    private NavigationView navigationView;
    private RecyclerView mRecycerView;

    private LinearLayoutManager mLayoutManager;
    private ItemListRecyclerAdapter mRecyclerViewAdapter;

    private LocateThingsDatabase mDatabase;

    private String title;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Bind view component.
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRecycerView = (RecyclerView) findViewById(R.id.recyclerview_item_list);
        title = (String) this.getTitle();

        // instantiate layoutManager and RecyclerView adapter.
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewAdapter = new ItemListRecyclerAdapter(this);

        // init database.
        mDatabase = LocateThingsDatabase.getInstance(this);
        // insert test data.
        mDatabase.insertTestData();

        // set adapter data.
        ArrayList<LittleItem> itemsArray = mDatabase.getItemsByUserId(TestData.fakeUserId);
        Log.d(TAG, "onCreate: "+ itemsArray.get(0).getBindTagModule().getModuleMAC());
        mRecyclerViewAdapter.setItemsArrayList(itemsArray);

        // enable RecyclerView.
        mRecycerView.setHasFixedSize(true);
        mRecycerView.setLayoutManager(mLayoutManager);
        mRecycerView.setAdapter(mRecyclerViewAdapter);


        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.on_drawer_open,R.string.on_drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        findViewById(R.id.btn_find).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"find",Toast.LENGTH_SHORT).show();
//            }
//        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavMenuItemClickListener(this, drawerLayout));
    }

    /**
     * when back press , close the drawerlayout
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: clicked");
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            Log.d(TAG, "onBackPressed: popbackstack!");
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_add :
                //Toast.makeText(this,"add",Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                AddItemDialog addDialog = new AddItemDialog();
                addDialog.show(fm, "AddItemDialog");
                break;
            case R.id.btn_setting :
                Toast.makeText(this,"setting",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_blue_test:
                new SelectStationDialog().show(getFragmentManager(), "bluetoothDialog");
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void listItemOnClick(int inListPosi) {
        FragmentManager fm = getFragmentManager();
        ItemDetailDialog itemDetailDialog = new ItemDetailDialog();

        // attach data.
        Bundle args = new Bundle();
        args.putInt("inListPosi", inListPosi);
        args.putSerializable("item", mRecyclerViewAdapter.getItemsArrayList().get(inListPosi));
        itemDetailDialog.setArguments(args);

        // show dialog.
        itemDetailDialog.show(fm, "itemDetailDialog");
    }

    @Override
    public void onItemChange(int inAdapterPosi, LittleItem item) {
        long itemId = mRecyclerViewAdapter.getItemsArrayList().get(inAdapterPosi).getItemId();
        // update sql
        mDatabase.updateItemById(itemId, item);
        // update recyclerView adapter
        mRecyclerViewAdapter.adapterListChangeItem(inAdapterPosi, item);
    }

    @Override
    public void onAddItem(LittleItem newItem) {
        // update sql
        long itemId = mDatabase.addItem(newItem);

        // set item id.
        newItem.setItemId(itemId);

        // update recyclerView adapter.
        mRecyclerViewAdapter.adapterListAddItem(newItem);
    }

    @Override
    public void onItemRemove(int inAdapterPosi) {
        long itemId = mRecyclerViewAdapter.getItemsArrayList().get(inAdapterPosi).getItemId();
        // update sql
        mDatabase.removeItemById(itemId);
        // update recyclerView adapter.
        mRecyclerViewAdapter.adapterListRemoveItem(inAdapterPosi);
    }
}