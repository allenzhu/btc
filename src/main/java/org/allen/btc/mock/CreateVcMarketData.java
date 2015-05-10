package org.allen.btc.mock;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Random;


/**
 * @auther lansheng.zj
 */
public class CreateVcMarketData {

    public static final MessageFormat TICKER_FORMAT =
            new MessageFormat(
                "\"high\":\"0.00\",\"low\":\"0.00\",\"buy\":\"{0}\",\"sell\":\"{1}\",\"last\":\"0.00\",\"vol\":\"0.00\",\"hold\":\"0.00\",\"open\":\"0.00\",\"limit_highest_price\":\"0.00\",\"limit_lowest_price\":\"0.00\",\"contract_type\":\"week\",\"contract_id\":\"0\",\"time\":{2}");
    public static final MessageFormat DEPTHS_FORMAT = new MessageFormat(
        "\"asks\": [[\"{0}\",\"{1}\",\"{2}\"]],\"bids\": [[\"{3}\",\"{4}\",\"{5}\"]]");


    public static void main(String[] args) throws Exception {
        PrintWriter tickerPw = new PrintWriter(new FileWriter("D:/test/btc_record/vcticker.txt"));
        PrintWriter depthsPw = new PrintWriter(new FileWriter("D:/test/btc_record/vcdepths.txt"));

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
            tickerPw.print("{" + TICKER_FORMAT.format(new String[] { buyStr, sellStr, timeStr }) + "}");

            // create vc depths
            depthsPw.print("{"
                    + DEPTHS_FORMAT.format(new String[] { sellStr, "30", df.format((buy + 0.3) * 30), buyStr,
                                                         "30", df.format(buy * 30) }) + "}");
        }
        tickerPw.flush();
        depthsPw.flush();

        tickerPw.close();
        depthsPw.close();
    }
}
