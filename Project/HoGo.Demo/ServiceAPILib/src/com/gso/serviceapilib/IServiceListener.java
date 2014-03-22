package com.gso.serviceapilib;


public interface IServiceListener {
    /*
     * Called when a request has been fisnished without canceled.
     */
    public void onCompleted(Service service, ServiceResponse result);
}
