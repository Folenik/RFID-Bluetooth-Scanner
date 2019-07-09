package com.kamil.rfid;

import android.Manifest;

public final class Const {

    public static final int REQUEST_CODE_BLUETOOTH = 1000;
    public static final String DEVICE_ADDRESS = "device_address";

    public static final String[] REQUIRED_BLUETOOTH_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };
}
