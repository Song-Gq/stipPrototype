package com.sitp.prototype;

import java.io.Serializable;

public class Device implements Serializable {
    // maybe a unique device code is needed for identification
    // private final int _deviceId;

    private String _name;
    private int _status;
    private final int _model;

    // 0: negative
    // 1: positive
    // 2: connection fails (maybe power off or offline)
    // 3: sensor abnormal
    // 4: unknown exception
    // 5: reserved
    private static int[] _statusRange = {0, 4};

    // 0: stove
    // 1: AC
    // 2: window
    // 3: door
    // 4: unknown device
    // 5: reserved
    private static int[] _modelRange = {0, 4};

    private static String[] _modelName = {"Stove", "Air Conditioner",
    "Window", "Door", "Unknown Device", "Erroneous Device"};

    public Device(int model, String name, int status) throws Exception
    {
        // model out of range
        // construction fails
        if(model < _modelRange[0] || model > _modelRange[1])
            throw new Exception("illegal model");
        _model = model;

        // status out of range
        // construction fails
        if(status < _statusRange[0] || status > _statusRange[1])
            throw new Exception("illegal status");
        _status = status;

        // name empty
        // assign default name
        if(name.length() == 0)
            _name = "New " + _modelName[_model];
        else
            _name = name;
    }

    public void setName(String newName)
    {
        // name empty
        // do nothing
        if(newName.length() == 0);

        _name = newName;
    }
    public void setStatus(int newStatus) throws Exception
    {
        // status out of range
        // construction fails
        if(newStatus < _statusRange[0] || newStatus > _statusRange[1])
            throw new Exception("illegal status");

        _status = newStatus;
    }

    public String getName() { return _name; }
    public int getModel() { return _model; }
    public String getModelName() { return _modelName[_model]; }
    public int getStatus() { return _status; }

    public int getMinModel() { return _modelRange[0]; }
    public int getMaxModel() { return _modelRange[1]; }
    public int getMinStatus() { return _statusRange[0]; }
    public int getMaxStatus() { return _statusRange[1]; }

}
