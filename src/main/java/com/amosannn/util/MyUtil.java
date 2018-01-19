package com.amosannn.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyUtil {

  public static List<Integer> StringSetToIntegerList(Set<String> set){
    List<Integer> list = new ArrayList<>();
    for(String s : set) {
      list.add(Integer.parseInt(s));
    }
    return list;
  }
}
