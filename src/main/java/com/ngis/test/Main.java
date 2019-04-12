package com.ngis.test;

import io.jsonwebtoken.Claims;
import njnu.opengms.container.utils.JwtUtils;

/**
 * @ClassName Main
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/6
 * @Version 1.0.0
 */

public class Main {
    public static void main(String[] args) throws Exception {

        String jwtToken = "Bearer " + JwtUtils.generateToken("5ca1eae9e11f18365443676c", "sunlingzhi", "e10adc3949ba59abbe56e057f20f883e");
        Claims claims = JwtUtils.parseJWT(jwtToken);


    }
}
