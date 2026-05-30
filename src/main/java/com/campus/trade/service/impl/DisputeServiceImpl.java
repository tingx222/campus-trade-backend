package com.campus.trade.service.impl;

import com.campus.trade.entity.Dispute;
import com.campus.trade.entity.Order;
import com.campus.trade.mapper.DisputeMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.service.DisputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DisputeServiceImpl implements DisputeService {

    @Autowired
    private DisputeMapper disputeMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public Dispute createDispute(Long orderId, String reason, Integer type, Long userId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() == 5) {
            throw new RuntimeException("订单已取消，无法发起纠纷");
        }

        Dispute dispute = new Dispute();
        dispute.setOrderId(orderId);
        dispute.setGoodsId(order.getGoodsId());
        dispute.setReason(reason);
        dispute.setType(type);
        dispute.setStatus(1);

        if (type == 1) {
            if (!order.getBuyerId().equals(userId)) {
                throw new RuntimeException("只有买家可以发起此纠纷");
            }
            dispute.setApplicantId(order.getBuyerId());
            dispute.setRespondentId(order.getSellerId());
        } else {
            if (!order.getSellerId().equals(userId)) {
                throw new RuntimeException("只有卖家可以发起此纠纷");
            }
            dispute.setApplicantId(order.getSellerId());
            dispute.setRespondentId(order.getBuyerId());
        }

        disputeMapper.insert(dispute);
        return disputeMapper.findById(dispute.getId());
    }

    @Override
    @Transactional
    public Dispute resolveDispute(Long id, String result, Long handlerId) {
        Dispute dispute = disputeMapper.findById(id);
        if (dispute == null) {
            throw new RuntimeException("纠纷不存在");
        }
        dispute.setStatus(3);
        dispute.setResult(result);
        dispute.setHandlerId(handlerId);
        disputeMapper.update(dispute);
        return disputeMapper.findById(id);
    }

    @Override
    @Transactional
    public Dispute closeDispute(Long id, Long userId) {
        Dispute dispute = disputeMapper.findById(id);
        if (dispute == null) {
            throw new RuntimeException("纠纷不存在");
        }
        if (!dispute.getApplicantId().equals(userId) && !dispute.getRespondentId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        if (dispute.getStatus() == 3 || dispute.getStatus() == 4) {
            throw new RuntimeException("纠纷已处理，无法关闭");
        }
        dispute.setStatus(4);
        disputeMapper.update(dispute);
        return disputeMapper.findById(id);
    }

    @Override
    public Dispute getById(Long id) {
        return disputeMapper.findById(id);
    }

    @Override
    public List<Dispute> getByOrderId(Long orderId) {
        return disputeMapper.findByOrderId(orderId);
    }

    @Override
    public List<Dispute> getMyDisputes(Long userId) {
        // 查询作为申请人 或 作为被申请人的所有纠纷
        List<Dispute> asApplicant = disputeMapper.findByApplicantId(userId);
        List<Dispute> asRespondent = disputeMapper.findByRespondentId(userId);

        // 合并两个列表并去重
        List<Dispute> allDisputes = new ArrayList<>();
        if (asApplicant != null) allDisputes.addAll(asApplicant);
        if (asRespondent != null) {
            for (Dispute d : asRespondent) {
                boolean exists = false;
                for (Dispute existing : allDisputes) {
                    if (existing.getId().equals(d.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) allDisputes.add(d);
            }
        }

        // 按创建时间倒序排序
        allDisputes.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));

        return allDisputes;
    }

    @Override
    public List<Dispute> getAllDisputes() {
        return disputeMapper.findAll();
    }
}