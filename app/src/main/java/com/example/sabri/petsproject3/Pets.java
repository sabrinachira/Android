package com.example.sabri.petsproject3;

/**
 * Created by jaydo on 3/20/2018.
 */

public class Pets {
     private String name;
    private String file;
    public Pets(String aName, String aFile){
        name = aName;
        file = aFile;
    }
    public String getName(){
        return name;
    }
    public String getFile(){
        return file;
    }
}
