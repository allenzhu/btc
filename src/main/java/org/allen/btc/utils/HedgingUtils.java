package org.allen.btc.utils;

import static org.allen.btc.utils.DiffPriceType.BIG_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.BIG_DIF_POS;
import static org.allen.btc.utils.DiffPriceType.HUGE_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.HUGE_DIF_POS;
import static org.allen.btc.utils.DiffPriceType.NON_DIF;
import static org.allen.btc.utils.DiffPriceType.NORMAL_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.NORMAL_DIF_POS;
import static org.allen.btc.utils.DiffPriceType.SMALL_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.SMALL_DIF_POS;

import org.allen.btc.HedgingConfig;


/**
 * @auther lansheng.zj
 */
public class HedgingUtils {

    public static DiffPriceResult computeDiff(float nowDiffPrice, HedgingConfig config) {
        DiffPriceResult result = new DiffPriceResult();

        // huge
        if (positiveDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getHugeDiffPrice())) {
            result.setType(HUGE_DIF_POS);
        }
        else if (negativeDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getHugeDiffPrice())) {
            result.setType(HUGE_DIF_NEGA);
        }
        // big
        else if (positiveDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getBigDiffPrice())) {
            result.setType(BIG_DIF_POS);
        }
        else if (negativeDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getBigDiffPrice())) {
            result.setType(BIG_DIF_NEGA);
        }
        // normal
        else if (positiveDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getNormalDiffPrice())) {
            result.setType(NORMAL_DIF_POS);
        }
        else if (negativeDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getNormalDiffPrice())) {
            result.setType(NORMAL_DIF_NEGA);
        }
        // small
        else if (positiveDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getSmallDiffPrice())) {
            result.setType(SMALL_DIF_POS);
        }
        else if (negativeDiffPrice(nowDiffPrice, config.getReturnPrice(), config.getSmallDiffPrice())) {
            result.setType(SMALL_DIF_NEGA);
        }
        // non
        else {
            result.setType(NON_DIF);
        }

        result.setDiffPrice(nowDiffPrice);
        return result;
    }


    // +
    public static boolean positiveDiffPrice(float nowDiffPrice, float returnPrice, float preDiff) {
        if (nowDiffPrice > (returnPrice + preDiff)) {
            return true;
        }
        return false;
    }


    // -
    public static boolean negativeDiffPrice(float nowDiffPrice, float returnPrice, float preDiff) {
        if (nowDiffPrice < (returnPrice - preDiff)) {
            return true;
        }
        return false;
    }


    public static boolean bigDifference(long l1, long l2) {
        if (Math.abs(l1 - l2) > 1) {
            return true;
        }
        return false;
    }
}
