package com.lsc.blog.dao;

import com.lsc.blog.po.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReposity extends JpaRepository<Comment,Long> {
    List<Comment> findByBlogIdAndParentCommentNull(Long id, Sort sort);
}
