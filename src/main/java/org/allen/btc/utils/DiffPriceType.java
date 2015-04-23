package org.allen.btc.utils;

/**
 * @auther lansheng.zj
 */
public enum DiffPriceType {
    NON_DIF,

    SMALL_DIF_POS, // 小差价
    SMALL_DIF_NEGA,

    NORMAL_DIF_POS, // 普通差价
    NORMAL_DIF_NEGA,

    BIG_DIF_POS, // 大差价
    BIG_DIF_NEGA,

    HUGE_DIF_POS, // 极大差价
    HUGE_DIF_NEGA
}
