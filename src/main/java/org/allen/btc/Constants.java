package org.allen.btc;

/**
 * @auther lansheng.zj
 */
public class Constants {

    // schema
    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    // domain
    public static final String OKCOIN_DOMAIN = "www.okcoin.cn";
    public static final String BITVC_MARKET_DOMAIN = "market.bitvc.com";
    public static final String BITVC_API_DOMAIN = "api.bitvc.com";

    // okcoin path
    public static final String PATH_OKCOIN_TICKET = "/api/v1/ticker.do";
    public static final String PATH_OKCOIN_TRADE = "/api/v1/trade.do";

    // bitvc path
    public static final String PATH_BITVC_TICKET_WEEK = "/futures/ticker_btc_week.js";
    public static final String PATH_BITVC_ORDER_SAVE = "/futures/order/save";

    // okcoin parameter
    public static final String PARAM_OKCOIN_SYMBOL = "symbol";
    public static final String PARAM_OKCOIN_SYMBOL_VALUE = "btc_cny";
    // bitvc parameter

}
