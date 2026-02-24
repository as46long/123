package com.leyu.controller;

import com.leyu.service.CommentService;
import com.leyu.service.SongService;
import com.leyu.service.UserService;
import com.leyu.vo.DashboardVO;
import com.leyu.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Api(tags = "仪表盘")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private SongService songService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/stats")
    @ApiOperation("获取统计数据")
    public Result<DashboardVO> getStats() {
        DashboardVO stats = new DashboardVO();
        stats.setTotalUsers(100L);
        stats.setTodayNewUsers(10L);
        stats.setTotalSongs(50L);
        stats.setTodayPlayCount(1000L);
        stats.setPendingComments(commentService.countPending());
        stats.setHotSongs(songService.getHot(10));
        return Result.success(stats);
    }
}
