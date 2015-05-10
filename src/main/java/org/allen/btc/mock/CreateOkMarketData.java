package org.allen.btc.mock;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Random;


/**
 * @auther lansheng.zj
 */
public class CreateOkMarketData {

    public static final MessageFormat TICKER_FORMAT =
            new MessageFormat(
                "\"buy\":{0},\"contract_id\":0,\"high\":0.00,\"last\":0.00,\"low\":0.00,\"sell\":{1},\"unit_amount\":0,\"vol\":0");
    public static final MessageFormat DEPTHS_FORMAT = new MessageFormat(
        "\"asks\": [[{0},{1}]],\"bids\": [[{2},{3}]]");


    public static void main(String[] args) throws Exception {
        PrintWriter tickerPw = new PrintWriter(new FileWriter("D:/test/btc_record/okticker.txt"));
        PrintWriter depthsPw = new PrintWriter(new FileWriter("D:/test/btc_record/okdepths.txt"));

        int nums = 100;
        int max = 1390;
        int min = 1350;
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("####.##");
        boolean isFirst = true;
        for (int i = 0; i < nums; i++) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                tickerPw.print("\n");
                depthsPw.print("\n");
            }

            // creat vc ticker
            float buy = random.nextFloat();
            int next = random.nextInt(max);
            buy = next % (max - min + 1) + min + buy;
            String buyStr = df.format(buy);
            String sellStr = df.format(buy + 0.3);
            String timeStr = System.currentTimeMillis() / 1000 + "";
            tickerPw.print("{\"date\":\"" + timeStr + "\",\"ticker\":" + "{"
                    + TICKER_FORMAT.format(new String[] { buyStr, sellStr, timeStr }) + "}" + "}");

            // create vc depths
            depthsPw.print("{" + DEPTHS_FORMAT.format(new String[] { sellStr, "30", buyStr, "30" }) + "}");
        }
        tickerPw.flush();
        depthsPw.flush();

        tickerPw.close();
        depthsPw.close();
    }
}
