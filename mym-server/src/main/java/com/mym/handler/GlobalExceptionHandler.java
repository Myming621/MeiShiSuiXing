package com.mym.handler;

import com.mym.exception.BaseException;
import com.mym.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }


    /**
     * 处理SQL异常
     *
     * @param ex
     * @return
     */
    //   Duplicate entry 'mym621' for key 'idx_username'
    @ExceptionHandler
    public Result sqlExceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) {
            String[] s = message.split(" ");
            String username = s[2];
            String mes = username + "已存在";
            return Result.error(mes);
        } else {
            return Result.error("未知错误");
        }
    }

}