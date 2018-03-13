package com.amosannn.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class CollectionSqlProvider {

  public String listCollectionByCollectionId(final Map<String, Object> map) {
    List<Integer> idList = (List<Integer>)map.get("idList");
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (Integer collectionId : idList) {
      sb.append(" '" + collectionId + "',");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")");
    System.out.println(sb.toString());
    return new SQL() {{
      SELECT(" collection_id, collection_name, create_time, update_time, followed_count ");
      FROM(" collection ");
      WHERE(" collection_id in " + sb.toString());
    }}.toString();
  }

}
