package com.mym.mapper;

import com.mym.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author mingbb
 * @create 2023-09-21-21:07
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品ids查询套餐ids
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品关联表
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询关联菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> selectById(Long id);

    /**
     * 删除套餐id相关联的菜品
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    @Select("select dish_id from setmeal_dish where setmeal_id = #{id}")
    List<Long> getDishIdsBySetmealId(Long id);
}
