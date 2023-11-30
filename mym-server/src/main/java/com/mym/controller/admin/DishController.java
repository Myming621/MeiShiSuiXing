package com.mym.controller.admin;

import com.mym.dto.DishDTO;
import com.mym.dto.DishPageQueryDTO;
import com.mym.entity.Dish;
import com.mym.result.PageResult;
import com.mym.result.Result;
import com.mym.service.DishService;
import com.mym.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author mingbb
 * @create 2023-09-21-0:50
 */

/**
 * 菜品管理
 */
@RestController
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清除添加菜品分类的缓存
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("开始进行菜品删除：{}", ids);
        dishService.deleteBatch(ids);

        //清除所有以dish_开头的菜品缓存key
        cleanCache("dish_*");

        return Result.success();
    }


    /**
     * 根据id查找菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查找菜品")
    public Result<DishVO> selectById(@PathVariable Long id) {
        log.info("根据id查找：{}", id);
        DishVO dishVO = dishService.selectById(id);
        return Result.success(dishVO);
    }


    @PutMapping
    @ApiOperation("修改菜品数据")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品数据：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //清除所有以dish_开头的菜品缓存key
        cleanCache("dish_*");

        return Result.success();
    }


    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("要操作的菜品id：{}，状态：{}", id, status);
        dishService.startOrStop(status, id);

        //清除所有以dish_开头的菜品缓存key
        cleanCache("dish_*");

        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId) {
        log.info("根据分类id：{}查询菜品",categoryId);
        List<Dish> dishes = dishService.selectByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 清除缓存的方法
     * @param pattern
     */
    public void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
