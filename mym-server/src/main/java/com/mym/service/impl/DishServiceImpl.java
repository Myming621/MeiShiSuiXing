package com.mym.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mym.constant.MessageConstant;
import com.mym.constant.StatusConstant;
import com.mym.dto.DishDTO;
import com.mym.dto.DishPageQueryDTO;
import com.mym.entity.Dish;
import com.mym.entity.DishFlavor;
import com.mym.entity.Setmeal;
import com.mym.exception.DeletionNotAllowedException;
import com.mym.mapper.DishFlavorMapper;
import com.mym.mapper.DishMapper;
import com.mym.mapper.SetmealDishMapper;
import com.mym.mapper.SetmealMapper;
import com.mym.result.PageResult;
import com.mym.service.DishService;
import com.mym.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingbb
 * @create 2023-09-21-1:01
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品及口味
     *
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入一条菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        //获取insert语句的主键返回值
        Long dishId = dish.getId();
        //向菜品表插入n条口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //因为使用了pagehelper，返回类型是固定的
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断菜品状态是否为启售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品状态为启售
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否被套餐关联
        List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


//        //删除菜品中的数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }

        //根据ids删除菜品中的数据
        dishMapper.deleteByIds(ids);
        //根据ids删除菜品口味中的数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查找菜品和对应口味
     *
     * @param id
     */
    public DishVO selectById(Long id) {
        //根据菜品id查找菜品
        Dish dish = dishMapper.getById(id);
        //根据菜品id查找口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //封装到一块返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 修改菜品信息
     *
     * @param dishDTO
     */
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品表基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除口味信息
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //插入新的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // dishFlavorMapper.insertBatch(flavors);
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //如果是要停售的菜品，判断传入的菜品有没有关联套餐
        if (status == StatusConstant.DISABLE) {
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                //如果有关联套餐，将当前菜品的套餐停售
                for (Long setmealId : setmealIds) {
                    //延伸扩展性，更新整个套餐信息
                    Setmeal setmeal = new Setmeal();
                    setmeal.setId(setmealId);
                    setmeal.setStatus(StatusConstant.DISABLE);
                    setmealMapper.update(setmeal);
                }
            }
        }
        //如果是要起售或者已经停售套餐，停售当前菜品
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    public List<Dish> selectByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.getByCategoryId(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.getByCategoryId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
