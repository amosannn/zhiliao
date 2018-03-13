package com.amosannn.service.impl;

import com.amosannn.mapper.CommentDao;
import com.amosannn.mapper.UserDao;
import com.amosannn.model.AnswerComment;
import com.amosannn.model.User;
import com.amosannn.service.CommentService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

  @Autowired
  CommentDao commentDao;
  @Autowired
  UserDao userDao;

  /**
   * 评论回答
   * @param answerId
   * @param commentContent
   * @param userId
   * @return
   */
  @Override
  public AnswerComment commentAnswer(Integer answerId, String commentContent, Integer userId) {
    AnswerComment comment = new AnswerComment();
    comment.setAnswerId(answerId);
    comment.setAnswerCommentContent(commentContent);
    comment.setUserId(userId);
    comment.setCreateTime(System.currentTimeMillis());
    comment.setLikedCount(0);

    Integer answerCommentId = commentDao.insertAnswerComment(comment);
    comment.setAnswerCommentId(answerCommentId);

    User user = userDao.selectUserInfoByUserId(userId);
    comment.setUser(user);

    return comment;
  }

  /**
   * 回复评论
   * @param reqMap
   * @param userId
   * @return
   */
  @Override
  public AnswerComment replyAnswerComment(Map<String, Object> reqMap, Integer userId) {
    AnswerComment answerComment = new AnswerComment();
//    answerComment.setAnswerCommentId(Integer.parseInt(reqMap.get("answerCommentId") + ""));
    answerComment.setAnswerCommentContent(String.valueOf(reqMap.get("answerCommentContent")));
    answerComment.setAnswerId(Integer.parseInt(reqMap.get("answerId") + ""));
    answerComment.setAtUserId(Integer.parseInt(reqMap.get("atUserId") + ""));
    answerComment.setAtUserName(String.valueOf(reqMap.get("atUserName")));

    answerComment.setLikedCount(0);
    answerComment.setCreateTime(System.currentTimeMillis());
    answerComment.setUserId(userId);

    Integer answerCommentId = commentDao.insertAnswerCommentReply(answerComment);
//    answerComment.setAnswerCommentId(answerCommentId);

    User user = userDao.selectUserInfoByUserId(userId);
    answerComment.setUser(user);

    return answerComment;
  }



}
