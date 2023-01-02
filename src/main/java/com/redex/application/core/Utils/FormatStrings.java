package com.redex.application.core.Utils;

public class FormatStrings {

    public static String[] SplitShipmentString(String line){
        //012345678901234567890123456789012345
        //UMMS000048277-20230203-10:59-ELLX:03
        String[] string = new String[6];
        string[0] = line.substring(0,4);  //origin
        string[1] = line.substring(4,13);     //id
        string[2] = line.substring(14,22);  //date
        string[3] = line.substring(23,28);  //time
        string[4] = line.substring(29,33);   //destination
        string[5] = line.substring(34); //quantity

        return string;
    }
}
