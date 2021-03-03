package com.example.mysecondapp.util;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MicrobitUtil
{
    public MicrobitUtil()
    {
    }



    public static short shortFromLittleEndianBytes(byte[] b)
    {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(b[0]);
        bb.put(b[1]);
        short shortVal = bb.getShort(0);

        return shortVal;
    }



    public static String compassPoint(short bearing)
    {
        String compassPoint = "";

        if(bearing < 45)
            compassPoint = "North";
        else if(bearing < 135)
            compassPoint = "West";
        else if(bearing < 225)
            compassPoint = "South";
        else if(bearing < 315)
            compassPoint = "East";
        else
            compassPoint = "North";

        return compassPoint;
    }
}
