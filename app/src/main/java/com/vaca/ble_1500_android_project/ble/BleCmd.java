package com.vaca.ble_1500_android_project.ble;



import java.util.Date;

public class BleCmd {

//   public static int currentIndex=0;
//    public static int total=10000;
//
//    public static byte[] sendOtaData(byte[] data) {
//        int len = data.length;
//
//        byte[] cmd = new byte[7 + len];
//
//        byte[] n1=intToByteArray(currentIndex);
//        byte[] n2=intToByteArray(total);
//
//        System.arraycopy(n1, 0, cmd, 0, 3);
//        System.arraycopy(n2, 0, cmd, 3, 3);
//        System.arraycopy(data, 0, cmd, 6, len);
//        cmd[6+len]=oxr(cmd);
//        return cmd;
//    }

    public static byte[] sendOtaData(byte[] data,int currentIndex, int total) {
        int len = data.length;

        byte[] cmd = new byte[7 + len];

        byte[] n1=intToByteArray(currentIndex);
        byte[] n2=intToByteArray(total);

        for(int k=0;k<3;k++){
            cmd[k]=n1[k];
        }
        for(int k=0;k<3;k++){
            cmd[k+3]=n2[k];
        }
        for(int k=0;k<len;k++){
            cmd[k+6]=data[k];
        }
        cmd[6+len]=oxr(cmd);
        return cmd;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[3];
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    public static byte oxr(byte[] x) {
        byte result=x[0];
        for(int k=1;k<(x.length-1);k++){
            result= (byte) (result ^ x[k]);
        }
        return result;
    }
}
