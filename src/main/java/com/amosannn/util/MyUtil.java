package com.amosannn.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MyUtil {

  public static List<Integer> StringSetToIntegerList(Set<String> set) {
    List<Integer> list = new ArrayList<>();
    for (String s : set) {
      list.add(Integer.parseInt(s));
    }
    return list;
  }

  public static String md5(String text) {
    byte[] secretBytes = null;
    try {
      secretBytes = MessageDigest.getInstance("md5").digest(text.getBytes());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("没有md5算法！");
    }
    String md5Code = new BigInteger(1, secretBytes).toString(16);
    // 若未满32位则前面加0补全
    for (int i = 0; i < 32 - md5Code.length(); i++) {
      md5Code = "0" + md5Code;
    }
    return md5Code;
  }

  public static String createRadomCode() {
    return System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "");
  }

  public static String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return sdf.format(date);
  }
}
