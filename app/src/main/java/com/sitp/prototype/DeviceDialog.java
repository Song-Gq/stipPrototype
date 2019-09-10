package com.sitp.prototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static com.sitp.prototype.ScrollingActivity.PREFS_NAME;

public class DeviceDialog extends DialogFragment {

    private int devicePosition;
    private String deviceName;
    private List<Device> devices;
    private Device device;
    // Use this instance of the interface to deliver action events
    private DeviceDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.device_dialog, null);
        builder.setView(dialogView);

        // get args from the activity
        devicePosition = getArguments().getInt("devicePosition");
        // get device list from local data
        getDevices();

        EditText editText = dialogView.findViewById(R.id.deviceNameField);
        editText.setText(device.getName());
        TextView textView = dialogView.findViewById(R.id.statusContent);
        textView.setText(device.getStatusName());
        textView = dialogView.findViewById(R.id.modelContent);
        textView.setText(device.getModelName());

        builder.setMessage(R.string.device_dialog_title)
                .setPositiveButton(R.string.confirm_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = dialogView.findViewById(R.id.deviceNameField);
                        deviceName = editText.getText().toString();
                        updateDevices();

                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(DeviceDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(DeviceDialog.this);
                    }
                })
                .setNeutralButton(R.string.delete_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNeutralClick(DeviceDialog.this, devicePosition);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DeviceDialogListener {
        public void onDialogPositiveClick(DeviceDialog dialog);
        public void onDialogNegativeClick(DeviceDialog dialog);
        public void onDialogNeutralClick(DeviceDialog dialog, int position);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DeviceDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        // Send the negative button event back to the host activity
        mListener.onDialogNegativeClick(DeviceDialog.this);
    }

    private boolean updateDevices()
    {
        try
        {
            // what if device name empty?
            device.setName(deviceName);

            SharedPreferences deviceList = getActivity().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = deviceList.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(devices);//把对象写到流里
                String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                editor.putString("deviceList", temp);
                editor.commit();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return true;
        }
        catch (Exception e) {
            // construction fails
            return false;
        }
    }

    private boolean getDevices()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
        String temp = sharedPreferences.getString("deviceList", "");
        ByteArrayInputStream bais =  new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            devices = (List<Device>)ois.readObject();
            device = devices.get(devicePosition);
        }
        catch (IOException e)
        {
            return false;
        }
        catch(ClassNotFoundException e1)
        {
            return false;
        }
        return true;
    }

}
