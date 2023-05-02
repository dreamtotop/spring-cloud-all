package org.top.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;
import org.top.constant.DBConstants;
import org.top.model.User;

@DS(DBConstants.DATASOURCE_USERS)
public interface UserMapper {


    void addUser(User user);

    User selectUserById(@Param("id") Long id);
}
