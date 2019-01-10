package njnu.opengms.container.controller;

import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.controller.common.BaseController;
import njnu.opengms.container.dto.user.AddUserDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.User;
import njnu.opengms.container.service.UserServiceImp;
import njnu.opengms.container.utils.JwtUtils;
import njnu.opengms.container.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description todo
 * @Author sun_liber
 * @Date 2018/11/28
 * @Version 1.0.0
 */
@RestController
@RequestMapping (value = "/user")
public class UserController implements BaseController<User, AddUserDTO, Object, Object, Object, String, UserServiceImp> {

    @Autowired
    UserServiceImp userServiceImp;

    @Override
    public UserServiceImp getService() {
        return userServiceImp;
    }


    @RequestMapping (value = "/login", method = RequestMethod.POST)
    public JsonResult doLogin(@RequestBody User userIn) {
        User user = userServiceImp.findUserByUserName(userIn.getUsername());
        if (user == null) {
            throw new MyException(ResultEnum.NO_OBJECT);
        } else {
            if (user.getPassword().equals(userIn.getPassword())) {
                String jwtToken = JwtUtils.generateToken(user.getId(), userIn.getUsername(), userIn.getPassword());
                return ResultUtils.success("Bearer" + " " + jwtToken);
            } else {
                throw new MyException(ResultEnum.USER_PASSWORD_NOT_MATCH);
            }
        }
    }

    @RequestMapping (value = "/all-permission-tag", method = RequestMethod.POST)
    public JsonResult getPermission() {
        String[] permission = {"super_admin", "admin"};
        return ResultUtils.success(permission);
    }


}
