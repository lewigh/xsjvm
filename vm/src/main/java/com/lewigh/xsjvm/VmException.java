package com.lewigh.xsjvm;

public class VmException extends RuntimeException {

    public VmException() {
    }

    public VmException(String message) {
        super(message);
    }


    public VmException(String message, Throwable cause) {
        super(message, cause);
    }


    public VmException(Throwable cause) {
        super(cause);
    }
}
