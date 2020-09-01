package com.lsc.blog.service;

import com.lsc.blog.dao.CommentReposity;
import com.lsc.blog.po.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private CommentReposity commentReposity;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort=Sort.by(Sort.Direction.ASC,"createTime");
        List<Comment> comments=commentReposity.findByBlogIdAndParentCommentNull(blogId,sort);
        return eachComment(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        Long parentCommentId=comment.getParentComment().getId();
        if (parentCommentId != -1){
            comment.setParentComment(commentReposity.getOne(parentCommentId));
        }else {
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        return commentReposity.save(comment);
    }


    private List<Comment> eachComment(List<Comment> comments){
        List<Comment> commentsView=new ArrayList<>();
        for (Comment comment : comments){
            Comment c=new Comment();
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        combineChildren(commentsView);
        return commentsView;
    }


    private void combineChildren(List<Comment> comments){
        for (Comment comment : comments){
            List<Comment> replys1=comment.getReplyComments();
            for (Comment reply1 :replys1){
                recursivery(reply1);
            }
            comment.setReplyComments(tempReplys);
            tempReplys=new ArrayList<>();
        }
    }

    private List<Comment> tempReplys=new ArrayList<>();



    private void recursivery(Comment comment){
        tempReplys.add(comment);
        if (comment.getReplyComments().size()>0){
            List<Comment> replys=comment.getReplyComments();
            for (Comment reply : replys){
                tempReplys.add(reply);
                if (reply.getReplyComments().size()>0){
                    recursivery(reply);
                }
            }
        }
    }
}
