package com.campus.transfer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.transfer.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {

    /** 平台用户总数 */
    @Select("SELECT COUNT(*) FROM user")
    Long countTotal();
}
