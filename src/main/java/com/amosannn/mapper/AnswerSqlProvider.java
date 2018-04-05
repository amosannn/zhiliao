package com.amosannn.mapper;

import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl.On;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class AnswerSqlProvider {

  public String listAnswerByAnswerId(final Map<String, Object> map) {
    List<Integer> idList = (List<Integer>)map.get("idList");
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

  public String listAnswerByUserId(Map<String, Object> map) {
    String limitSql = " limit #{offset}, #{limit} ";
    return new SQL() {{
      SELECT(" a.answer_id, a.answer_content, a.liked_count, a.create_time, q.question_id, q.question_title, u.user_id, u.username, u.avatar_url, u.simple_desc ");
      FROM(" answer a ");
      JOIN(" question q on a.question_id = q.question_id ");
      JOIN(" user u on a.user_id = u.user_id ");
      WHERE(" u.user_id = #{userId} ");
    }}.toString() + limitSql;
  }

  public String listAnswerCountByQuestionId(final Map<String, Object> map) {
    List<Integer> questionIdList = (List<Integer>)map.get("questionIdList");
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (Integer questionId : questionIdList) {
      sb.append(" '" + questionId + "',");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")");
    System.out.println("31231232131231------------------------"+sb.toString());
    return new SQL() {{
      SELECT(" count(*) ");
      FROM(" answer a ");
      WHERE(" a.question_id in " + sb.toString());
    }}.toString();
  }

  /**
   * 获取热门回答（根据赞数排序
   * @param map
   * @return
   */
  public String listGoodAnswerByQuestionId(final Map<String, Object> map) {
    List<Integer> questionIdList = (List<Integer>)map.get("questionIdList");
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (Integer questionId : questionIdList) {
      sb.append(" '" + questionId + "',");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")");
    System.out.println(sb.toString());
    return new SQL() {{
      SELECT(" a.answer_id,a.answer_content,a.liked_count,a.create_time,\n"
          + "  q.question_id,q.question_title,u.user_id,u.username,u.avatar_url,u.simple_desc ");
      FROM(" answer a ");
      JOIN(" question q on a.question_id = q.question_id ");
      JOIN(" user u on a.user_id = u.user_id ");
      WHERE(" a.question_id in " + sb.toString());
    }}.toString() + " order by liked_count desc limit #{offset},#{limit}";
  }

  /**
   * 获取最近回答（根据时间排序
   * @param map
   * @return
   */
  public String listNewAnswerByQuestionId(final Map<String, Object> map) {
    List<Integer> questionIdList = (List<Integer>)map.get("questionIdList");
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (Integer questionId : questionIdList) {
      sb.append(" '" + questionId + "',");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")");
    System.out.println(sb.toString());
    return new SQL() {{
      SELECT(" a.answer_id,a.answer_content,a.liked_count,a.create_time,\n"
          + "  q.question_id,q.question_title,u.user_id,u.username,u.avatar_url,u.simple_desc ");
      FROM(" answer a ");
      JOIN(" question q on a.question_id = q.question_id ");
      JOIN(" user u on a.user_id = u.user_id ");
      WHERE(" a.question_id in " + sb.toString());
    }}.toString() + " order by a.create_time desc limit #{offset},#{limit}";
  }
}
