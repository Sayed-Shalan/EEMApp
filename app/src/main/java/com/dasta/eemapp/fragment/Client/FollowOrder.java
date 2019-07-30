package com.dasta.eemapp.fragment.Client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dasta.eemapp.R;

/**
 * Created by Mohamed on 03/10/2017.
 */

public class FollowOrder extends Fragment {

    ImageView imgFollowOrder1, imgFollowOrder2;
    TextView txtFollowOrder;
    String orderid, orderstate, ordercode;
    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_follow_order, null);

        flag = 1;

        orderid = getArguments().getString("follow_order_id");
        ordercode = getArguments().getString("follow_order_code");
        orderstate = getArguments().getString("follow_order_state");

        txtFollowOrder = (TextView) view.findViewById(R.id.txtClientFollowOrder);

        txtFollowOrder.setText(getString(R.string.followOrderId) + " : " + orderid
                + "\n" + getString(R.string.followOrderCode) + " : " + ordercode + "\n"
                + getString(R.string.followOrderState) + " : " + orderstate);

        imgFollowOrder1 = (ImageView) view.findViewById(R.id.imgFollowOrder1);

        imgFollowOrder2 = (ImageView) view.findViewById(R.id.imgFollowOrder2);

        if (orderstate.equals("Waiting")) {
            imgFollowOrder1.setImageResource(R.drawable.step1);
        } else if (orderstate.equals("Cancel")) {
            imgFollowOrder1.setImageResource(R.drawable.step1);
            imgFollowOrder2.setImageResource(R.drawable.step2);
        } else if (orderstate.equals("Complete")) {
            imgFollowOrder1.setImageResource(R.drawable.step1);
            imgFollowOrder2.setImageResource(R.drawable.step3);
        }

        return view;
    }
}
