package com.campus.trade.service;

import com.campus.trade.entity.Dispute;

import java.util.List;

public interface DisputeService {

    Dispute createDispute(Long orderId, String reason, Integer type, Long userId);

    Dispute resolveDispute(Long id, String result, Long handlerId);

    Dispute closeDispute(Long id, Long userId);

    Dispute getById(Long id);

    List<Dispute> getByOrderId(Long orderId);

    List<Dispute> getMyDisputes(Long userId);

    List<Dispute> getAllDisputes();
}