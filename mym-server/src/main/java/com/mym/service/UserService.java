package com.mym.service;

import com.mym.dto.UserLoginDTO;
import com.mym.entity.User;

/**
 * @author mingbb
 * @create 2023-09-26-22:09
 */
public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

}
