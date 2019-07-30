package com.dasta.eemapp.fragment.Shop;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasta.eemapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed on 12/09/2017.
 */

public class Home extends Fragment {

    ViewPager viewPager;
    public static TabLayout tabLayout;
    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_shop_home, null, false);

        flag = 1;

        viewPager = (ViewPager) v.findViewById(R.id.viewShopPager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabShopLayout);
        tabLayout.setupWithViewPager(viewPager);

        //You tab icons
        int[] icons = {
                R.drawable.ic_data,
                R.drawable.ic_product,
                R.drawable.ic_offer,
                R.drawable.ic_appointment
        };

        int[] titles = {
                R.string.live,
                R.string.category,
                R.string.offer,
                R.string.appointment
        };

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }

        int tabIconColor = ContextCompat.getColor(getActivity(), R.color.tabRed);
        int tabIconColor1 = ContextCompat.getColor(getActivity(), R.color.white);

        if (LiveInfo.flag == 1) {
            selectPage(0);
            com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.live);
        } else if (Category.flag == 1) {
            selectPage(1);
            com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.category);
        } else if (Offer.flag == 1) {
            selectPage(2);
            com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.offer);
        } else if (Appointment.flag == 1) {
            selectPage(3);
            com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.appointment);
        }


        if (tabLayout.getTabAt(0).isSelected()) {
            tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(3).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
        } else if (tabLayout.getTabAt(1).isSelected()) {
            tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(3).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
        } else if (tabLayout.getTabAt(2).isSelected()) {
            tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(3).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
        } else if (tabLayout.getTabAt(3).isSelected()) {
            tabLayout.getTabAt(3).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor1, PorterDuff.Mode.SRC_IN);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getActivity(), R.color.tabRed);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                if (tab.getPosition() == 0 && LiveInfo.flag == 1) {
                    com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.live);
                } else if (tab.getPosition() == 1 && Category.flag == 1) {
                    com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.category);
                } else if (tab.getPosition() == 2 && Offer.flag == 1) {
                    com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.offer);
                } else if (tab.getPosition() == 3 && Appointment.flag == 1) {
                    com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.appointment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getActivity(), R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new LiveInfo(), getString(R.string.live));
        adapter.addFragment(new Category(), getString(R.string.category));
        adapter.addFragment(new Offer(), getString(R.string.offer));
        adapter.addFragment(new Appointment(), getString(R.string.appointment));

        viewPager.setAdapter(adapter);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
        viewPager.setCurrentItem(pageIndex);
    }
}
