package com.safe.passtrack.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.safe.passtrack.Database.AccountCategoriesDatabase;
import com.safe.passtrack.Database.AccountNamesDatabase;
import com.safe.passtrack.R;
import com.safe.passtrack.ViewHolder.ViewPagerAdapter;

public class HomePageFragment extends Fragment {
    private ImageView settings;
    private TabLayout tabLayoutHomePage;
    private AppBarLayout appBarLayout;
    private ViewPager viewPagerHomePage;
    private SharedPreferences sPref;
    private static int ADMIN_MODE = 0;
    private static int SET_DEFAULTS = 0;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        settings = view.findViewById(R.id.settings);
        tabLayoutHomePage = view.findViewById(R.id.tabLayoutHomePage);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        viewPagerHomePage = view.findViewById(R.id.viewPagerHomePage);
        sPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainActivity, new SettingsFragment()).addToBackStack("").commit();
            }
        });


        if (!sPref.getBoolean("adminMode", false)) {
            if(ADMIN_MODE == 0){
                adminModeDialog();
                ADMIN_MODE=1;
            }
        }

        if(SET_DEFAULTS == 0){
            setUpDefaults();
        }

        setUpTabs(viewPagerHomePage);
        return view;
    }

    private void setUpTabs(ViewPager viewPagerHomePage) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addTabs(new AccountCategoriesFragment(), "Accounts");
        adapter.addTabs(new TextFragment(), "Text");

        viewPagerHomePage.setAdapter(adapter);
        tabLayoutHomePage.setupWithViewPager(viewPagerHomePage);
    }

    private void adminModeDialog() {
        final Dialog enableSettings = new Dialog(getContext());
        enableSettings.setContentView(R.layout.admin_mode_layout);

        Button goToSettings;

        goToSettings = enableSettings.findViewById(R.id.goToSettings);

        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainActivity, new SettingsFragment()).addToBackStack(" ").commit();
                enableSettings.dismiss();
            }
        });
        enableSettings.show();
    }

    private void setUpDefaults(){
        AccountCategoriesDatabase defaultCategories = new AccountCategoriesDatabase(getContext());
        defaultCategories.addCategory("Social");
        defaultCategories.addCategory("Entertainment");
        defaultCategories.addCategory("Shopping");
        defaultCategories.addCategory("College/Work");
        defaultCategories.addCategory("Websites");
        defaultCategories.addCategory("Bank");

        AccountNamesDatabase defaultAccounts = new AccountNamesDatabase(getContext());
        defaultAccounts.addToList("Facebook","Social");
        defaultAccounts.addToList("Instagram","Social");
        defaultAccounts.addToList("Gmail","Social");
        defaultAccounts.addToList("Snapchat","Social");

        defaultAccounts.addToList("Netflix","Entertainment");
        defaultAccounts.addToList("Spotify","Entertainment");
        defaultAccounts.addToList("Prime Video","Entertainment");
        defaultAccounts.addToList("Hotstar","Entertainment");
        defaultAccounts.addToList("Pintrest","Entertainment");

        defaultAccounts.addToList("Amazon","Shopping");
        defaultAccounts.addToList("Flipkart","Shopping");
        defaultAccounts.addToList("Snapdeal","Shopping");
        defaultAccounts.addToList("Myntra","Shopping");

        SET_DEFAULTS=1;
    }

}