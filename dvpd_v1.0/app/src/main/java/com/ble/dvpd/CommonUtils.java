package com.ble.dvpd;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by intel on 8/19/2017.
 */

public class CommonUtils {

    public static boolean isNullOrEmptyString(String str) {
        return !(str != null && str.length() > 0);
    }

    public static String ByteArraytoHex(byte[] bytes) {
        if(bytes!=null){
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            return sb.toString();
        }
        return "";
    }

    public static String getValue(BluetoothGattCharacteristic characteristic) {
        byte array[] = characteristic.getValue();
        StringBuilder sb = new StringBuilder();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }
        String noOfNode = new String(sb);
        long result = Long.parseLong(noOfNode, 16);
        return String.valueOf(result);
    }

    public static String getValue(BluetoothGattDescriptor descriptor) {
        byte array[] = descriptor.getValue();
        StringBuilder sb = new StringBuilder();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }
        String noOfNode = new String(sb);
        long result = Long.parseLong(noOfNode, 16);
        return String.valueOf(result);
    }

}
