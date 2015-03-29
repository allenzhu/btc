package org.allen.btc.utils;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;


/**
 * @auther lansheng.zj
 */
public class EncryptUtils {

    // TODO cache field

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
                                              'C', 'D', 'E', 'F' };


    public static String signStr(SortedMap<String, String> map) {
        StringBuilder sb = new StringBuilder("");
        boolean isFirst = true;
        for (Entry<String, String> entry : map.entrySet()) {
            if (isFirst) {
                isFirst = false;

            }
            else {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }

        return sb.toString();
    }


    /**
     * 大些字符MD5
     * 
     * @param str
     * @return
     */
    public static String md5UpperCase(String str) {
        try {
            if (str == null || str.trim().length() == 0) {
                return "";
            }
            byte[] bytes = str.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes);
            bytes = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(HEX_DIGITS[(bytes[i] & 0xf0) >> 4] + "" + HEX_DIGITS[bytes[i] & 0xf]);
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 小写字符MD5
     * 
     * @param str
     * @return
     */
    public static String md5LowerCase(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] b = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                int v = (int) b[i];
                v = v < 0 ? 0x100 + v : v;
                String cc = Integer.toHexString(v);
                if (cc.length() == 1)
                    sb.append('0');
                sb.append(cc);
            }
            return sb.toString();
        }
        catch (Exception e) {
        }
        return "";
    }


    public static <R> TreeMap<String, String> createRequestParam(R request) throws Exception {
        TreeMap<String, String> map = new TreeMap<String, String>();
        Field[] fields = request.getClass().getDeclaredFields();
        for (Field f : fields) {
            // cache field
            f.setAccessible(true);
            Object value = f.get(request);
            if (null != value) {
                map.put(f.getName(), (String) value);
            }
        }

        return map;
    }


    public static <R> void checkRequestNotNull(R request) throws IllegalArgumentException {
        // TODO
    }

}
