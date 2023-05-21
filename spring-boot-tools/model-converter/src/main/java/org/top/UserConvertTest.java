package org.top;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.top.convert.UserConvert;
import org.top.model.User;
import org.top.vo.UserVO;

public class UserConvertTest {

    public static void main(String[] args) {

//        1. mapStruct 方式实现java bean 属性复制

        User jerry = new User()
                .setId(1L).setUserName("jerry").setPassword("123456");

        UserVO userVO = UserConvert.INSTANCE.convert(jerry);

        System.out.print("mapStruct covert: ");
        System.out.println(userVO.toString());



//        2.  spring beanUtils 方式实现 (字段必须一致)

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(jerry, vo);

        System.out.print("spring beanUtils : ");
        System.out.println(vo);


//        3. fastjson  转换

//        String userJson = "{\n" +
//                "  \"id\": 3,\n" +
//                "  \"userName\": \"test_0bc9d018e678\",\n" +
//                "  \"password\": \"test_8be6b2e1e008\"\n" +
//                "}";
        String userJson = JSON.toJSONString(jerry);
        UserVO userVO1 = JSONObject.parseObject(userJson, UserVO.class);

        System.out.print("fastjson : ");
        System.out.println(userVO1);

//      4. jackson 转换

        ObjectMapper mapper = new ObjectMapper();
        try {
            String value = mapper.writeValueAsString(jerry);
            UserVO userVO2 = mapper.readValue(value, UserVO.class);
            System.out.print("Jackson : ");
            System.out.println(userVO2);
        } catch (JsonProcessingException ex){
            System.out.println(ex.getMessage());
        }
    }
}
