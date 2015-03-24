package org.allen.btc.exception;

/**
 * @auther lansheng.zj
 */
public class RemoteException extends Exception {

    private static final long serialVersionUID = 1L;


    public RemoteException() {
        super();
    }


    public RemoteException(String message) {
        super(message);
    }


    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }


    public RemoteException(Throwable cause) {
        super(cause);
    }
}
