package com.gso.serviceapilib;

public class ServiceResponse {
    private ServiceAction mAction;
    private ResultCode mCode;
    private Object mData;

    public ServiceResponse(ServiceAction action, Object data, ResultCode code) {
        mAction = action;
        mCode = code;
        mData = data;
    }

    public ServiceResponse(ServiceAction action, Object data) {
        this(action, data, ResultCode.Success);
    }

    public boolean isSuccess() {
        return (mCode == ResultCode.Success);
    }

    public ServiceAction getAction() {
        return mAction;
    }

    public ResultCode getCode() {
        return mCode;
    }

    public Object getData() {
        return mData;
    }
}
