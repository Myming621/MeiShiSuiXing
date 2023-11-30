package com.mym.mapper;

import com.github.pagehelper.Page;
import com.mym.dto.GoodsSalesDTO;
import com.mym.dto.OrdersPageQueryDTO;
import com.mym.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * @author mingbb
 * @create 2023-10-09-22:06
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单表
     * @param orders
     */
    void insert(Orders orders);


    /**************copy*********
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);


    /***********copy***************
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);


    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 统计规定时间内的销量排名前十
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin,LocalDateTime end);
}
