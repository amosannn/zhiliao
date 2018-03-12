package com.amosannn.mapper;

import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl.On;
import java.util.List;
import org.apache.ibatis.jdbc.SQL;

public class AnswerSqlProvider {

  public String listAnswerByAnswerId(final List<Integer> idList) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (Integer answerId : idList) {
      sb.append(" '" + answerId + "',");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")");
    System.out.println(sb.toString());
    return new SQL() {{
      SELECT(" a.answer_id, a.answer_content, a.liked_count, a.create_time, q.question_id, q.question_title, u.user_id, u.username, u.avatar_url, u.simple_desc ");
      FROM(" answer a ");
      JOIN(" question q on a.question_id = q.question_id ");
      JOIN(" user u on a.user_id = u.user_id ");
      WHERE(" a.answer_id in " + sb.toString());
    }}.toString();
  }

}
