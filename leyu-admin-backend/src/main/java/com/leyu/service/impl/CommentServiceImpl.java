package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.CommentDTO;
import com.leyu.entity.Comment;
import com.leyu.entity.User;
import com.leyu.mapper.CommentMapper;
import com.leyu.mapper.UserMapper;
import com.leyu.service.CommentService;
import com.leyu.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言服务实现类
 * 实现留言的发布、审核、点赞、查询等功能
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<CommentVO> getPage(int pageNum, int pageSize, Integer status) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = commentMapper.selectPage(page, wrapper);
        Page<CommentVO> voPage = new Page<>(pageNum, pageSize, commentPage.getTotal());
        voPage.setRecords(commentPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<CommentVO> getRecommend(Long userId, int num) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getStatus, 1).orderByDesc(Comment::getCreateTime).last("LIMIT " + num);
        List<Comment> comments = commentMapper.selectList(wrapper);
        return comments.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public void post(Long userId, CommentDTO dto) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setLikes(0);
        comment.setStatus(1);
        commentMapper.insert(comment);
    }

    @Override
    public void audit(Long id, Integer status) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status);
        commentMapper.updateById(comment);
    }

    @Override
    public void delete(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    public void like(Long id, Long userId) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setLikes(comment.getLikes() + 1);
            commentMapper.updateById(comment);
        }
    }

    @Override
    public Long countPending() {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getStatus, 0);
        return commentMapper.selectCount(wrapper);
    }

    @Override
    public Page<CommentVO> getMyComments(Long userId, int pageNum, int pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getUserId, userId).orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = commentMapper.selectPage(page, wrapper);
        Page<CommentVO> voPage = new Page<>(pageNum, pageSize, commentPage.getTotal());
        voPage.setRecords(commentPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
            vo.setAvatar(user.getAvatar());
        }
        return vo;
    }
}
