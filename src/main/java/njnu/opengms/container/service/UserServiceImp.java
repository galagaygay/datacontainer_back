package njnu.opengms.container.service;

import njnu.opengms.container.dto.user.AddUserDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.User;
import njnu.opengms.container.repository.UserRepository;
import njnu.opengms.container.service.common.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName UserServiceImp
 * @Description todo
 * @Author sun_liber
 * @Date 2018/11/28
 * @Version 1.0.0
 */
@Service
public class UserServiceImp implements BaseService<User, AddUserDTO, Object, Object, Object, String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public void add(AddUserDTO addDTO) {
        if (userRepository.findUserByUsername(addDTO.getUsername()) != null) {
            throw new MyException(ResultEnum.EXIST_OBJECT);
        }
        User user = new User();
        BeanUtils.copyProperties(addDTO, user);
        user.setCreateDate(new Date());
        userRepository.insert(user);
    }

    @Override
    public void remove(String id) {
        //TODO
    }

    @Override
    public Page<Object> list(Object findDTO) {
        //TODO
        return null;
    }

    @Override
    public User get(String s) {
        return userRepository.findById(s).get();
    }

    @Override
    public User getByExample(User user) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void update(String id, Object updateDTO) {
        //TODO
    }

    /**
     * 根据用户名查找用户
     *
     * @param userName 用户名
     *
     * @return
     */
    public User findUserByUserName(String userName) {
        return userRepository.findUserByUsername(userName);
    }
}
