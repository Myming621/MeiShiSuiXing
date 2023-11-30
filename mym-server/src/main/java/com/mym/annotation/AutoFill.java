package com.mym.annotation;

/**
 * @author mingbb
 * @create 2023-09-20-13:07
 */

import com.mym.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行公共字段自动填充处理
 */
@Target(ElementType.METHOD)     //该注解加在方法上
@Retention(RetentionPolicy.RUNTIME)     //该注解在运行时生效
public @interface AutoFill {
    //数据库操作类型 UPDATE INSERT
    OperationType value();
}