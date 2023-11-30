package com.mym.controller.admin;

import com.mym.constant.MessageConstant;
import com.mym.result.Result;
import com.mym.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

/**
 * @author mingbb
 * @create 2023-09-20-17:13
 */

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件开始上传：{}", file);

        try {
            String originalFilename = file.getOriginalFilename();
            String newFilename = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            String filepath = aliOssUtil.upload(file.getBytes(), newFilename);
            return Result.success(filepath);
        } catch (IOException e) {
            log.info("文件上传失败:{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
