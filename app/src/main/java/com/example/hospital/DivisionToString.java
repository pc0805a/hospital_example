package com.example.hospital;

/**
 * Created by User on 2015/10/21.
 */
public  class DivisionToString {
    protected static String translate(int division)
    {
        String divisionStr = "";
        switch(division)
        {
            case 1:
                divisionStr = "泌尿科";
                break;
            default:
                divisionStr = "這與我無關";
        }


        return divisionStr;
    }
}
