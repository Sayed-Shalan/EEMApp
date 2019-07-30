package com.dasta.eemapp;

public class PanoramaModel {
    int drawable_id;
    String url;
    String departmentName;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public PanoramaModel() {
    }

    public PanoramaModel(int drawable_id, String url) {
        this.drawable_id = drawable_id;
        this.url = url;
    }

    public int getDrawable_id() {
        return drawable_id;
    }

    public void setDrawable_id(int drawable_id) {
        this.drawable_id = drawable_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
