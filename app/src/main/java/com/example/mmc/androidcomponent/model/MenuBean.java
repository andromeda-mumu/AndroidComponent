package com.example.mmc.androidcomponent.model;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by 16244 on 2019/3/9.
 */

public class MenuBean {
    public List<Menu> data;
    public String totalNum;
    public int pn;
    public int rn;

    public class Menu{
        public String id;
        public String imtro;
        public List<Step> steps;
    }

    public class Step{
        public String img;
        public String step;
    }

}
