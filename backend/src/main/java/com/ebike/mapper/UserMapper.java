package com.ebike.mapper;

import com.ebike.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User selectByUsername(String username);
    User selectByPhone(String phone);
    User selectById(Long id);
    List<User> selectAll();
    List<User> selectByRole(@Param("role") Integer role);
    int insert(User user);
    int update(User user);
    int deleteById(Long id);
    long selectCount();
}
