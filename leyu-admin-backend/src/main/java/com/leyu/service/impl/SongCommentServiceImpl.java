package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongCommentDTO;
import com.leyu.entity.Song;
import com.leyu.entity.SongComment;
import com.leyu.entity.User;
import com.leyu.mapper.SongCommentMapper;
import com.leyu.mapper.SongMapper;
import com.leyu.mapper.UserMapper;
import com.leyu.service.SongCommentService;
import com.leyu.vo.SongCommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SongCommentServiceImpl implements SongCommentService {

    @Autowired
    private SongCommentMapper songCommentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SongMapper songMapper;

    @Override
    public Page<SongCommentVO> getSongComments(Long songId, int pageNum, int pageSize, Long userId) {
        Page<SongComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SongComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SongComment::getSongId, songId);
        wrapper.eq(SongComment::getStatus, 1);
        wrapper.orderByDesc(SongComment::getCreateTime);
        Page<SongComment> commentPage = songCommentMapper.selectPage(page, wrapper);
        Page<SongCommentVO> voPage = new Page<>(pageNum, pageSize, commentPage.getTotal());
        voPage.setRecords(commentPage.getRecords().stream().map(comment -> convertToVO(comment, userId)).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public void postSongComment(Long userId, SongCommentDTO dto) {
        try {
            Song song = songMapper.selectById(dto.getSongId());
            if (song == null) {
                throw new RuntimeException("歌曲不存在");
            }
            SongComment comment = new SongComment();
            comment.setSongId(dto.getSongId());
            comment.setUserId(userId);
            comment.setContent(dto.getContent());
            comment.setLikes(0);
            comment.setStatus(1);
            songCommentMapper.insert(comment);
        } catch (Exception e) {
            System.out.println("发布歌曲评论异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("发布评论失败：" + e.getMessage());
        }
    }

    @Override
    public void likeSongComment(Long commentId, Long userId) {
        SongComment comment = songCommentMapper.selectById(commentId);
        if (comment != null) {
            comment.setLikes(comment.getLikes() + 1);
            songCommentMapper.updateById(comment);
        }
    }

    @Override
    public void banSongComment(Long commentId) {
        SongComment comment = new SongComment();
        comment.setId(commentId);
        comment.setStatus(0);
        songCommentMapper.updateById(comment);
    }

    @Override
    public void unbanSongComment(Long commentId) {
        SongComment comment = new SongComment();
        comment.setId(commentId);
        comment.setStatus(1);
        songCommentMapper.updateById(comment);
    }

    @Override
    public Page<SongCommentVO> getAdminSongComments(int pageNum, int pageSize, String category, Integer status) {
        try {
            Page<SongComment> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<SongComment> wrapper = new LambdaQueryWrapper<>();
            if (status != null) {
                wrapper.eq(SongComment::getStatus, status);
            }
            if (category != null && !category.isEmpty()) {
                List<Long> songIds = songMapper.selectList(new LambdaQueryWrapper<Song>().eq(Song::getCategory, category))
                        .stream().map(Song::getId).collect(Collectors.toList());
                if (!songIds.isEmpty()) {
                    wrapper.in(SongComment::getSongId, songIds);
                } else {
                    wrapper.eq(SongComment::getSongId, -1L);
                }
            }
            wrapper.orderByDesc(SongComment::getCreateTime);
            Page<SongComment> commentPage = songCommentMapper.selectPage(page, wrapper);
            Page<SongCommentVO> voPage = new Page<>(pageNum, pageSize, commentPage.getTotal());
            voPage.setRecords(commentPage.getRecords().stream().map(comment -> convertToVO(comment, null)).collect(Collectors.toList()));
            return voPage;
        } catch (Exception e) {
            System.out.println("获取管理员歌曲评论列表异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("获取歌曲评论列表失败：" + e.getMessage());
        }
    }

    @Override
    public void deleteSongComment(Long commentId) {
        songCommentMapper.deleteById(commentId);
    }

    @Override
    public Page<SongCommentVO> getMySongComments(Long userId, int pageNum, int pageSize) {
        Page<SongComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SongComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SongComment::getUserId, userId);
        wrapper.orderByDesc(SongComment::getCreateTime);
        Page<SongComment> commentPage = songCommentMapper.selectPage(page, wrapper);
        Page<SongCommentVO> voPage = new Page<>(pageNum, pageSize, commentPage.getTotal());
        voPage.setRecords(commentPage.getRecords().stream().map(comment -> convertToVO(comment, userId)).collect(Collectors.toList()));
        return voPage;
    }

    private SongCommentVO convertToVO(SongComment comment, Long currentUserId) {
        try {
            SongCommentVO vo = new SongCommentVO();
            BeanUtils.copyProperties(comment, vo);

            // 获取用户信息
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setAvatar(user.getAvatar());
            }

            // 获取歌曲信息
            Song song = songMapper.selectById(comment.getSongId());
            if (song != null) {
                vo.setSongTitle(song.getTitle());
                vo.setSongArtist(song.getArtist());
                vo.setSongCategory(song.getCategory());
            }

            vo.setIsLiked(false);
            return vo;
        } catch (Exception e) {
            System.out.println("转换歌曲评论VO异常: " + e.getMessage());
            e.printStackTrace();
            // 即使转换失败，也返回基本的VO对象
            SongCommentVO vo = new SongCommentVO();
            BeanUtils.copyProperties(comment, vo);
            vo.setIsLiked(false);
            return vo;
        }
    }
}
