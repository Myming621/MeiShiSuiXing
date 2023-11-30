package com.mym.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mym.constant.MessageConstant;
import com.mym.constant.StatusConstant;
import com.mym.dto.SetmealDTO;
import com.mym.dto.SetmealPageQueryDTO;
import com.mym.entity.Dish;
import com.mym.entity.Setmeal;
import com.mym.entity.SetmealDish;
import com.mym.exception.DeletionNotAllowedException;
import com.mym.exception.SetmealEnableFailedException;
import com.mym.mapper.DishMapper;
import com.mym.mapper.SetmealDishMapper;
import com.mym.mapper.SetmealMapper;
import com.mym.result.PageResult;
import com.mym.service.SetmealService;
import com.mym.vo.DishItemVO;
import com.mym.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author mingbb
 * @create 2023-09-22-17:16
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        //将setmealDto中的数据抽取到setmeal实体类中
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //插入套餐表
        setmealMapper.save(setmeal);
        //插入套餐菜品关联表
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //为每个setmealDish传入setmealId
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBatch(setmealDishes);

    }


    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //使用了PageHelper，返回值是固定的
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 根据id查询套餐
     */
    public SetmealVO selectById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }


    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        //删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        Long setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        //重新插入关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }


    /**
     * 批量删除套餐
     *
     * @param ids
     */
    public void deleteBatch(List<Long> ids) {
        //判断套餐是否为起售状态
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        ids.forEach(id -> {
            //删除套餐
            setmealMapper.deleteById(id);
            //删除套餐菜品关联数据
            setmealDishMapper.deleteBySetmealId(id);
        });
    }

    /**
     * 套餐起售停售
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断是否有未启售的菜品，如果有则不能起售
        Setmeal setmeal = setmealMapper.getById(id);
        if (status == StatusConstant.ENABLE) {
            //List<Dish> dishes = dishMapper.getBySetmealId(id);
            List<Long> dishIds = setmealDishMapper.getDishIdsBySetmealId(id);
            dishIds.forEach(dishId -> {
                Dish dish = dishMapper.getById(dishId);
                if (dish.getStatus() == StatusConstant.DISABLE) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        //起售停售
        Setmeal setmeal1 = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setmealMapper.update(setmeal1);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
