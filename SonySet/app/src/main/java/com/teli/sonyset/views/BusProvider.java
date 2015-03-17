package com.teli.sonyset.views;

import com.squareup.otto.Bus;

/**
 * Created by madhuri on 15/3/15.
 */
public class BusProvider {
    private static Bus BUS;

    public static Bus getInstance() {
        if(BUS !=null)
            return BUS;
        else {
            BUS = new Bus();
            return BUS;
        }
    }

    private BusProvider() {
    }
}
