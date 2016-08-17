package com.arenas.droidfan.profile;

/**
 * Created by Arenas on 2016/8/11.
 */
public class ProfileEvent {

    private boolean load;
    private boolean refresh;

    public ProfileEvent(boolean load , boolean refresh){
        this.load = load;
        this.refresh = refresh;
    }

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
