package com.tianxia.app.healthworld.model;

import com.tianxia.lib.baseworld.widget.DragListAdapter.IDragable;
import com.tianxia.lib.baseworld.widget.TagCloudInfo;

public class WishInfo extends TagCloudInfo implements IDragable {

    @Override
    public String getDisplay() {
        return title;
    }
}
