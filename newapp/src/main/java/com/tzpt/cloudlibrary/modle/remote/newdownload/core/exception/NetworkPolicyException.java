package com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class NetworkPolicyException extends IOException{
    public NetworkPolicyException() {
        super("Only allows downloading this task on the wifi network type!");
    }
}
