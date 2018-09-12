package com.psplog.dailycalli.item;

import java.util.ArrayList;

public class CalliLearn_Item {
    ArrayList<CalliLearn_SubItem> subItem;
    String guide_title;
    int guide_id;
    String guide_img;

    public String getGuide_file() {
        return guide_file;
    }

    String guide_file;
    public ArrayList<String> guide_tag;

    public ArrayList<CalliLearn_SubItem> getSubItem() {
        return subItem;
    }

    public String getGuide_title() {
        return guide_title;
    }

    public int getGuide_id() {
        return guide_id;
    }

    public String getGuide_img() {
        return guide_img;
    }

    public ArrayList<String> getGuide_tag() {
        return guide_tag;
    }

    public CalliLearn_Item(ArrayList<CalliLearn_SubItem> subItem, String guide_title, int guide_id, String guide_img, ArrayList<String> guide_tag, String guide_file) {
        this.subItem = subItem;
        this.guide_title = guide_title;
        this.guide_id = guide_id;
        this.guide_img = guide_img;
        this.guide_tag = guide_tag;
        this.guide_file = guide_file;
    }
}
