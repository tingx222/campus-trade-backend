package com.campus.trade.controller;

import com.campus.trade.common.ResultVO;
import com.campus.trade.entity.Dispute;
import com.campus.trade.service.DisputeService;
import com.campus.trade.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispute")
public class DisputeController {

    @Autowired
    private DisputeService disputeService;

    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserId(token);
            }
        }
        return null;
    }

    @PostMapping("/create")
    public ResultVO createDispute(@RequestBody Dispute dispute, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return ResultVO.fail(401, "请先登录");
        try {
            Dispute result = disputeService.createDispute(dispute.getOrderId(), dispute.getReason(), dispute.getType(), userId);
            return ResultVO.success(result);
        } catch (RuntimeException e) {
            return ResultVO.fail(400, e.getMessage());
        }
    }

    @PostMapping("/resolve")
    public ResultVO resolveDispute(@RequestParam Long id, @RequestParam String result, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return ResultVO.fail(401, "请先登录");
        try {
            Dispute d = disputeService.resolveDispute(id, result, userId);
            return ResultVO.success(d);
        } catch (RuntimeException e) {
            return ResultVO.fail(400, e.getMessage());
        }
    }

    @PostMapping("/close")
    public ResultVO closeDispute(@RequestParam Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return ResultVO.fail(401, "请先登录");
        try {
            Dispute d = disputeService.closeDispute(id, userId);
            return ResultVO.success(d);
        } catch (RuntimeException e) {
            return ResultVO.fail(400, e.getMessage());
        }
    }

    @GetMapping("/detail")
    public ResultVO getDetail(@RequestParam Long id) {
        Dispute dispute = disputeService.getById(id);
        if (dispute == null) return ResultVO.fail(404, "纠纷不存在");
        return ResultVO.success(dispute);
    }

    @GetMapping("/order")
    public ResultVO getByOrderId(@RequestParam Long orderId) {
        List<Dispute> list = disputeService.getByOrderId(orderId);
        return ResultVO.success(list);
    }

    @GetMapping("/my")
    public ResultVO getMyDisputes(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return ResultVO.fail(401, "请先登录");
        List<Dispute> list = disputeService.getMyDisputes(userId);
        return ResultVO.success(list);
    }

    @GetMapping("/all")
    public ResultVO getAllDisputes() {
        List<Dispute> list = disputeService.getAllDisputes();
        return ResultVO.success(list);
    }
}