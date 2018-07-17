package com.example.ziv.stratego;

import android.widget.ImageView;

/**
 * Created by ZIV on 15/01/2016.
 */
public class ListModel {

    private String id;
    private String name;
    private Integer Image;

    /*********** Set Methods ******************/
    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setImage(Integer Image)
    {
        this.Image = Image;
    }


    /*********** Get Methods ****************/
    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public Integer getImage()
    {
        return this.Image;
    }

}