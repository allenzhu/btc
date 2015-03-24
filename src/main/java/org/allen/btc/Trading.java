package org.allen.btc;

/**
 * @auther lansheng.zj
 */
public interface Trading {

    public void start() throws Exception;


    public void shutdown() throws Exception;


    public <T> T getTicker(int timeout) throws Exception;

}
