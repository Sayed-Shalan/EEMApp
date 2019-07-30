package com.dasta.eemapp.fragment.Shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mohamed on 03/10/2017.
 */

public class LiveInfo extends Fragment {

    public static int flag = 0;
    ViewPager vpLiveInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop_live_info, null, false);
        flag = 1;

        vpLiveInfo = (ViewPager) v.findViewById(R.id.vpLiveInfo);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.shop_adv);
        list.add(R.drawable.shop_adv1);
        list.add(R.drawable.shop_adv2);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), list);

        vpLiveInfo.setAdapter(viewPagerAdapter);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        return v;
    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (vpLiveInfo.getCurrentItem() == 0) {
                        vpLiveInfo.setCurrentItem(1);
                    } else if (vpLiveInfo.getCurrentItem() == 1) {
                        vpLiveInfo.setCurrentItem(2);
                    } else if (vpLiveInfo.getCurrentItem() == 2) {
                        vpLiveInfo.setCurrentItem(0);
                    }
                }
            });
        }
    }

}
