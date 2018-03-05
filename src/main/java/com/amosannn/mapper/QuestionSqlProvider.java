package com.amosannn.mapper;

import java.util.List;
import org.apache.ibatis.jdbc.SQL;

public class QuestionSqlProvider {

  public String listQuestionByQuestionId(final List<Integer> idList) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (Integer questionId : idList) {
      sb.append(" '" + questionId + "',");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")");
    System.out.println(sb.toString());
    return new SQL() {{
      SELECT(" question_id,question_title,create_time ");
      FROM(" question ");
      WHERE(" question_id in " + sb.toString());
    }}.toString();
  }
}
