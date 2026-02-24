package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.CommentDTO;
import com.leyu.vo.CommentVO;

import java.util.List;

public interface CommentService {
    Page<CommentVO> getPage(int pageNum, int pageSize, Integer status);
    List<CommentVO> getRecommend(Long userId, int num);
    void post(Long userId, CommentDTO dto);
    void audit(Long id, Integer status);
    void delete(Long id);
    void like(Long id, Long userId);
    Long countPending();
}
