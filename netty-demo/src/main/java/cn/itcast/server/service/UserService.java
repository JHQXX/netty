package cn.itcast.server.service;

/**
 * 用户管理接口
 */
public interface UserService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 true, 否则返回 false
     */
    boolean login(String username, String password);


    /**
     * 登出
     * @param username 用户名
     * @param password 密码
     * @return 登出成功返回 true, 否则返回 false
     */
    boolean loginOut(String username, String password);



}
