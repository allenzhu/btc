package org.allen.btc;

/**
 * @auther lansheng.zj
 */
public class Constants {

    // schema
    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    // domain
    public static final String OKCOIN_DOMAIN = "www.okcoin.com";
    public static final String BITVC_MARKET_DOMAIN = "market.bitvc.com";
    public static final String BITVC_API_DOMAIN = "api.bitvc.com";

    // okcoin path
    public static final String PATH_OKCOIN_TICKET = "/api/v1/ticker.do"; // FIXME
                                                                         // necessary
                                                                         // ?
    public static final String PATH_OKCOIN_TRADE = "/api/v1/future_trade.do";
    public static final String PATH_OKCOIN_TICKET_WEEK = "/api/v1/future_ticker.do";
    public static final String PATH_OKCOIN_DEPTH_WEEK = "/api/v1/future_depth.do";
    public static final String PATH_OKCOIN_USER_FUTURE = "/api/v1/future_userinfo.do";

    // bitvc path
    public static final String PATH_BITVC_TICKET_WEEK = "/futures/ticker_btc_week.js";
    public static final String PATH_BITVC_DEPTH_WEEK = "/futures/depths_btc_week.js";
    public static final String PATH_BITVC_ORDER_SAVE = "/futures/order/save";
    public static final String PATH_BITVC_USER_FUTURE = "/futures/balance";

    // okcoin parameter
    public static final String PARAM_OKCOIN_SYMBOL = "symbol";
    public static final String PARAM_OKCOIN_SYMBOL_F_VALUE = "btc_usd";
    public static final String PARAM_OKCOIN_CONTRACT = "contract_type";
    public static final String PARAM_OKCOIN_CONTRACT_F_WEEK = "this_week";

    // bitvc parameter

}
