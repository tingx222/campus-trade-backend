package com.campus.trade.mapper;

import com.campus.trade.entity.Dispute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DisputeMapper {

    int insert(Dispute dispute);

    int update(Dispute dispute);

    Dispute findById(@Param("id") Long id);

    List<Dispute> findByOrderId(@Param("orderId") Long orderId);

    List<Dispute> findByApplicantId(@Param("applicantId") Long applicantId);

    List<Dispute> findByRespondentId(@Param("respondentId") Long respondentId);

    List<Dispute> findAll();
}