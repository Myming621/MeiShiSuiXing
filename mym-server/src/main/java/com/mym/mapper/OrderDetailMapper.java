package com.mym.mapper;

import com.mym.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingbb
 * @create 2023-10-09-22:08
 */
@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细表
     * @param orderDetails
     */
    void insertBatch(ArrayList<OrderDetail> orderDetails);


    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
