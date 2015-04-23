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
    public static final String PATH_OKCOIN_TRADE_INFO = "/api/v1/future_order_info.do";
    public static final String PATH_OKCOIN_TICKET_WEEK = "/api/v1/future_ticker.do";
    public static final String PATH_OKCOIN_DEPTH_WEEK = "/api/v1/future_depth.do";
    public static final String PATH_OKCOIN_USER_FUTURE = "/api/v1/future_userinfo.do";

    // bitvc path
    public static final String PATH_BITVC_TICKET_WEEK = "/futures/ticker_btc_week.js";
    public static final String PATH_BITVC_DEPTH_WEEK = "/futures/depths_btc_week.js";
    public static final String PATH_BITVC_ORDER_SAVE = "/futures/order/save";
    public static final String PATH_BITVC_ORDER_QUERY = "/futures/order";
    public static final String PATH_BITVC_USER_FUTURE = "/futures/balance";

    // okcoin parameter
    public static final String PARAM_OKCOIN_SYMBOL = "symbol";
    public static final String PARAM_OKCOIN_SYMBOL_F_VALUE = "btc_usd";
    public static final String PARAM_OKCOIN_CONTRACT = "contract_type";
    public static final String PARAM_OKCOIN_CONTRACT_F_WEEK = "this_week";

    public static final int OKCOIN_ORDER_STATUS_PENDING = 0;
    public static final int OKCOIN_ORDER_STATUS_DONE_HALF = 1;
    public static final int OKCOIN_ORDER_STATUS_DONE = 2;
    public static final int OKCOIN_ORDER_STATUS_CANCEL = -1;
    public static final int OKCOIN_ORDER_STATUS_CANCEL_PENDING = 4;

    // bitvc parameter
    public static final String PARAM_BITVC_CONTRACTTYPE_WEEK = "week";
    public static final String PARAM_BITVC_COINTYPE_BTC = "1";

    public static final int BITVC_ORDER_STATUS_DONE = 2;
    public static final int BITVC_ORDER_STATUS_UNDONE = 0;
    public static final int BITVC_ORDER_STATUS_DONE_HALF = 1;
    public static final int BITVC_ORDER_STATUS_CANCEL = 3;
    public static final int BITVC_ORDER_STATUS_PENDING = 7;

    // max retry times
    public static final int MAX_RETRY_TIMES = 5;

}
