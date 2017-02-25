package cn.spsilab.locatethings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.spsilab.locatethings.loginmodule.LoginActivity;

/**
 * Created by Feng on 2/17/2017.
 * navigation
 */
class NavMenuItemClickListener implements NavigationView.OnNavigationItemSelectedListener {

    private MainActivity mainActivity;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Button addBtn;


    public NavMenuItemClickListener(MainActivity mainActivity, DrawerLayout drawerLayout, RecyclerView recyclerView, Toolbar toolbar) {
        this.mainActivity = mainActivity;
        this.drawerLayout = drawerLayout;
        this.recyclerView = recyclerView;
        this.toolbar = toolbar;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.menu_user_login: {
                toolbar.setTitle("login");
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                mainActivity.startActivityForResult(intent, mainActivity.getResources().getInteger(R.integer.LOGIN_STATUS));
            }
            break;
            case R.id.menu_user_info: {
                toolbar.setTitle("user info");
                toolbar.setNavigationIcon(null);
                toolbar.getMenu().setGroupVisible(0, false);
                Fragment infoFragment = new UserInfoFragment();
                recyclerView.setVisibility(View.INVISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, infoFragment, null)
                        .addToBackStack(String.valueOf(item.getItemId()))
                        .commit();

            }
            break;
            case R.id.menu_app_about: {
                bundle.putString("show_text", item.getTitle().toString());
                Fragment showFragment = new ShowFragment();
                showFragment.setArguments(bundle);
                mainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, showFragment, null)
                        .addToBackStack(String.valueOf(item.getItemId()))
                        .commit();
            }
            case R.id.menu_user_logout: {
                NetworkService.getInstance().logout(mainActivity);
                mainActivity.logout();
            }
            break;
            default:
                return false;
        }

       // close this navigation view
        drawerLayout.closeDrawers();
        return true;
    }


}
