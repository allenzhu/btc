package org.allen.btc;

/**
 * @auther lansheng.zj
 */
public interface Trading {

    public void start() throws Exception;


    public void shutdown() throws Exception;


    public <T> T getTicker(int timeout) throws Exception;


    public <T> T getDepths(int timeout) throws Exception;


    public <T> T trade(Object r, int timeout) throws Exception;


    public <T> T userFutureInfo(Object r, int timeout) throws Exception;
}
