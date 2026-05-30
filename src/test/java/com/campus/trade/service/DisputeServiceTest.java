package com.campus.trade.service;

import com.campus.trade.entity.Dispute;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.DisputeMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.impl.DisputeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 纠纷仲裁服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("纠纷仲裁服务 - 单元测试")
class DisputeServiceTest {

    @Mock
    private DisputeMapper disputeMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DisputeServiceImpl disputeService;

    private Dispute testDispute;
    private Order testOrder;
    private User testBuyer;
    private User testSeller;

    @BeforeEach
    void setUp() {
        testBuyer = new User();
        testBuyer.setId(300L);
        testBuyer.setNickname("买家小红");
        testBuyer.setPhone("13912345678");
        testBuyer.setCreditScore(70);
        testBuyer.setRole(1);

        testSeller = new User();
        testSeller.setId(200L);
        testSeller.setNickname("卖家小明");
        testSeller.setPhone("13812345678");
        testSeller.setCreditScore(80);
        testSeller.setRole(1);

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setOrderNo("ORD20260001");
        testOrder.setGoodsId(1000L);
        testOrder.setBuyerId(300L);
        testOrder.setSellerId(200L);
        testOrder.setStatus(3);
        testOrder.setAddress("1号宿舍楼101");

        testDispute = new Dispute();
        testDispute.setId(1L);
        testDispute.setOrderId(100L);
        testDispute.setApplicantId(300L);
        testDispute.setRespondentId(200L);
        testDispute.setReason("商品与描述不符");
        testDispute.setStatus(1);
        testDispute.setType(1);
        testDispute.setCreateTime(LocalDateTime.now());
    }

    // ==================== 创建纠纷测试 ====================

    @Test
    @DisplayName("【创建纠纷】订单不存在时抛异常")
    void testCreateDispute_OrderNotFound() {
        when(orderMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            disputeService.createDispute(999L, "原因", 1, 300L);
        });
    }

    @Test
    @DisplayName("【创建纠纷】非订单参与者抛异常")
    void testCreateDispute_NotParticipant() {
        when(orderMapper.findById(100L)).thenReturn(testOrder);

        assertThrows(RuntimeException.class, () -> {
            disputeService.createDispute(100L, "原因", 1, 999L);
        });
    }

    // ==================== 解决纠纷测试 ====================

    @Test
    @DisplayName("【仲裁纠纷】成功处理纠纷")
    void testResolveDispute_Success() {
        when(disputeMapper.findById(1L)).thenReturn(testDispute);
        when(disputeMapper.update(any(Dispute.class))).thenReturn(1);

        Dispute result = disputeService.resolveDispute(1L, "支持买家，退款处理", 1L);

        assertNotNull(result);
        verify(disputeMapper, times(1)).update(any(Dispute.class));
    }

    @Test
    @DisplayName("【仲裁纠纷】纠纷不存在抛异常")
    void testResolveDispute_NotFound() {
        when(disputeMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            disputeService.resolveDispute(999L, "处理结果", 1L);
        });
    }

    // ==================== 关闭纠纷测试 ====================

    @Test
    @DisplayName("【关闭纠纷】申请人成功撤回纠纷")
    void testCloseDispute_ByApplicant_Success() {
        when(disputeMapper.findById(1L)).thenReturn(testDispute);
        when(disputeMapper.update(any(Dispute.class))).thenReturn(1);

        Dispute result = disputeService.closeDispute(1L, 300L);

        assertNotNull(result);
        verify(disputeMapper, times(1)).update(any(Dispute.class));
    }

    @Test
    @DisplayName("【关闭纠纷】非申请人不能撤回")
    void testCloseDispute_NotApplicant() {
        when(disputeMapper.findById(1L)).thenReturn(testDispute);

        assertThrows(RuntimeException.class, () -> {
            disputeService.closeDispute(1L, 999L);
        });
    }

    // ==================== 查询纠纷测试 ====================

    @Test
    @DisplayName("【查询纠纷】根据ID查询")
    void testGetById_Success() {
        when(disputeMapper.findById(1L)).thenReturn(testDispute);

        Dispute result = disputeService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(disputeMapper, times(1)).findById(1L);
    }

    @Test
    @DisplayName("【查询纠纷】根据ID查询 - 不存在")
    void testGetById_NotFound() {
        when(disputeMapper.findById(999L)).thenReturn(null);

        Dispute result = disputeService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("【查询纠纷】根据订单ID查询")
    void testGetByOrderId_Success() {
        List<Dispute> mockDisputes = Arrays.asList(testDispute);
        when(disputeMapper.findByOrderId(100L)).thenReturn(mockDisputes);

        List<Dispute> result = disputeService.getByOrderId(100L);

        assertEquals(1, result.size());
        verify(disputeMapper, times(1)).findByOrderId(100L);
    }

    @Test
    @DisplayName("【查询纠纷】订单无纠纷返回空列表")
    void testGetByOrderId_NoDisputes() {
        when(disputeMapper.findByOrderId(999L)).thenReturn(Collections.emptyList());

        List<Dispute> result = disputeService.getByOrderId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("【查询纠纷】查询用户发起的纠纷")
    void testGetMyDisputes_AsApplicant() {
        List<Dispute> mockDisputes = Arrays.asList(testDispute);
        when(disputeMapper.findByApplicantId(300L)).thenReturn(mockDisputes);

        List<Dispute> result = disputeService.getMyDisputes(300L);

        assertEquals(1, result.size());
        verify(disputeMapper, times(1)).findByApplicantId(300L);
    }

    @Test
    @DisplayName("【查询纠纷】查询用户作为被申请人的纠纷")
    void testGetMyDisputes_AsRespondent() {
        List<Dispute> mockDisputes = Arrays.asList(testDispute);
        when(disputeMapper.findByRespondentId(200L)).thenReturn(mockDisputes);

        List<Dispute> result = disputeService.getMyDisputes(200L);

        assertEquals(1, result.size());
        verify(disputeMapper, times(1)).findByRespondentId(200L);
    }

    @Test
    @DisplayName("【查询纠纷】用户无纠纷记录")
    void testGetMyDisputes_NoDisputes() {
        when(disputeMapper.findByApplicantId(999L)).thenReturn(Collections.emptyList());
        when(disputeMapper.findByRespondentId(999L)).thenReturn(Collections.emptyList());

        List<Dispute> result = disputeService.getMyDisputes(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("【查询纠纷】管理员查询所有纠纷")
    void testGetAllDisputes_Success() {
        List<Dispute> mockDisputes = Arrays.asList(testDispute, new Dispute());
        when(disputeMapper.findAll()).thenReturn(mockDisputes);

        List<Dispute> result = disputeService.getAllDisputes();

        assertEquals(2, result.size());
        verify(disputeMapper, times(1)).findAll();
    }
}