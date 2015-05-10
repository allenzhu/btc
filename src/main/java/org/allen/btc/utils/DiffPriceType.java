package org.allen.btc.utils;

/**
 * @auther lansheng.zj
 */
public enum DiffPriceType {
    NON_DIF(0),

    SMALL_DIF_POS(1), // 小差价
    SMALL_DIF_NEGA(-1),

    NORMAL_DIF_POS(2), // 普通差价
    NORMAL_DIF_NEGA(-2),

    BIG_DIF_POS(3), // 大差价
    BIG_DIF_NEGA(-3),

    HUGE_DIF_POS(4), // 极大差价
    HUGE_DIF_NEGA(-4);

    public static DiffPriceType getDiffPriceTypeByValue(int v) {
        DiffPriceType dType = NON_DIF;
        switch (v) {
        case 0:
            dType = NON_DIF;
            break;
        case 1:
            dType = SMALL_DIF_POS;
            break;
        case 2:
            dType = NORMAL_DIF_POS;
            break;
        case 3:
            dType = BIG_DIF_POS;
            break;
        case 4:
            dType = HUGE_DIF_POS;
            break;
        case -1:
            dType = SMALL_DIF_NEGA;
            break;
        case -2:
            dType = NORMAL_DIF_NEGA;
            break;
        case -3:
            dType = BIG_DIF_NEGA;
            break;
        case -4:
            dType = HUGE_DIF_NEGA;
            break;
        default:
            throw new UnsupportedOperationException("unknown DiffPriceType value, v=" + v);
        }

        return dType;
    }

    private int value;


    private DiffPriceType(int v) {
        value = v;
    }


    public int getValue() {
        return value;
    }


    public int getAbsValue() {
        if (value < 0) {
            return Math.abs(value);
        }
        return value;
    }


    public DiffPriceType skateToNext() {
        int nextV = value - 1;
        switch (this) {
        case SMALL_DIF_POS:
        case NORMAL_DIF_POS:
        case BIG_DIF_POS:
        case HUGE_DIF_POS:
            // nextV = dType.getValue() - 1;
            break;

        case HUGE_DIF_NEGA:
        case BIG_DIF_NEGA:
        case NORMAL_DIF_NEGA:
        case SMALL_DIF_NEGA:
            nextV = value + 1;
            break;
        }

        DiffPriceType next = getDiffPriceTypeByValue(nextV);
        return next;
    }
}
