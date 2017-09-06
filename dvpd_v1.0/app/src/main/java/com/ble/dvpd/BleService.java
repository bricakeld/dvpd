package com.ble.dvpd;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

/**
 * Created by intel on 8/19/2017.
 */

public class BleService extends Service {

    private static BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private static BluetoothGattService bluetoothGattService;
    private static BluetoothDevice bluetoothDevice;
    private static BluetoothGatt mBluetoothGatt;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            if(!CommonUtils.isNullOrEmptyString(intent.getAction())) {
                if(intent.getAction().equals(CommonConstants.INTENT_EXTRA_SET_SERVICE_DATA)) {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static void setBleData(BluetoothGattService bluetoothGattServiceOne, BluetoothGattCharacteristic bluetoothGattCharacteristicOne) {
        bluetoothGattCharacteristic = bluetoothGattCharacteristicOne;
        bluetoothGattService = bluetoothGattServiceOne;
    }

    public static void setBluetoothDevice(BluetoothDevice device, BluetoothGatt bluetoothGatt) {
        bluetoothDevice = device;
        mBluetoothGatt = bluetoothGatt;
    }

    public static void sendBulbData(int data) {
        byte[] valueByte = new byte[1];
        valueByte[0] = (byte) data;
        bluetoothGattCharacteristic.setValue(valueByte);
        String characteristicValue = CommonUtils.ByteArraytoHex(valueByte);
        mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }
}
