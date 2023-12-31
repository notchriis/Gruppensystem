package com.example.gruppensystem.Datenbank;

public class DatenbankError extends Exception{
    int errorCode;
    public DatenbankError(){
        super();
    }
    public DatenbankError(String s, int errorCode){
        super(s);
        this.errorCode = errorCode;
    }
    public int getErrorCode(){
        return this.errorCode;
    }
}
