package sc.simple.sso.biz.service;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import sc.simple.sso.biz.domain.model.UserInfo;
import sc.simple.sso.rpc.Result;
import sc.simple.sso.rpc.SsoUser;

@Service("userService")
public class UserService {

    private static List<UserInfo> userList;

    static {
        userList = new ArrayList<>();
        userList.add(new UserInfo(1, "管理员", "admin", "123456"));
    }

    public Result<SsoUser> login(String username, String password) {
        //todo  集成自定义用户登录接口
        for (UserInfo user : userList) {
            if (user.getUsername().equals(username)) {
                if (user.getPassword().equals(password)) {
                    return Result.createSuccess(new SsoUser(user.getId(), user.getUsername()));
                } else {
                    return Result.createError("密码有误");
                }
            }
        }
        return Result.createError("用户不存在");
    }
}
