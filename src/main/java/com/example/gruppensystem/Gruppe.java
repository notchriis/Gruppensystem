package com.example.gruppensystem;

import java.time.LocalDateTime;

public class Gruppe {
    private int gruppe_id;
    private String name;
    private String prefix;


    public Gruppe(int gruppe_id, String name, String prefix){
        this.gruppe_id = gruppe_id;
        this.name = name;
        this.prefix = prefix;
    }

    public int getId(){
        return this.gruppe_id;
    }
    public String getName(){
        return this.name;
    }
    public String getPrefix(){
        return this.prefix;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Gruppe)){
            return false;
        }
        if(obj == this){
            return true;
        }
        Gruppe neueGr = (Gruppe)obj;
        return this.gruppe_id == neueGr.getId();
    }
}
