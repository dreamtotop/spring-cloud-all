package org.top.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.top.model.User;
import org.top.vo.UserVO;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);


    @Mappings({@Mapping(source = "id", target = "userId"),
            @Mapping(source = "userName", target = "name")})
    UserVO convert(User user);

}
