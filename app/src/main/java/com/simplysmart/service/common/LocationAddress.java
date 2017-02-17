package com.simplysmart.service.common;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {

    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.US);
                String _result = null;
                String _street = "";
                String _city = "";
                String _postal_code = "";
                String _state = "";
                String _country = "";

                StringBuilder sb = new StringBuilder();

                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);

                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(", ");
                        }
                        try {
                            _street = address.getSubLocality().trim();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            _city = address.getLocality().trim();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            _postal_code = address.getPostalCode().trim();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            _state = address.getAdminArea().trim();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            _country = address.getCountryName().trim();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        sb.append(address.getCountryName());
                        _result = sb.toString();
                    }
                } catch (IOException e) {
                    DebugLog.e("Unable connect to Geocoder" + e);

                } finally {

                    Message message = Message.obtain();
                    message.setTarget(handler);

                    if (_result != null) {

                        DebugLog.d("Latitude = " + latitude);
                        DebugLog.d("Longitude = " + longitude);
                        DebugLog.d("Address = " + _result);
                        DebugLog.d("CurrentCity = " + _city);
                        DebugLog.d("State = " + _state);
                        DebugLog.d("PostalCode = " + _postal_code);
                        DebugLog.d("Country = " + _country);

                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putDouble("Latitude", latitude);
                        bundle.putDouble("Longitude", longitude);
                        bundle.putString("Address", _result);
                        bundle.putString("PostalCode", _postal_code);
                        bundle.putString("CurrentCity", _city);
                        bundle.putString("State", _state);
                        bundle.putString("Country", _country);
                        bundle.putString("address", String.valueOf(sb));
                        message.setData(bundle);

                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        _result = "Latitude: " + latitude + " Longitude: " + longitude
                                + "\n Unable to get address for this lat-long.";
                        bundle.putString("address", _result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}