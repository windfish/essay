package com.demon.shiro;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author xuliang
 * @since 2019年7月9日 下午2:48:11
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:com/demon/shiro/spring.xml")
public class Tutorial {

    private static final Logger logger = LoggerFactory.getLogger(Tutorial.class);
    
    public static void main(String[] args) {

        logger.info("first apache shiro app.");

        /*
         * SecurityManager 是shiro 的核心，每一个程序都有一个SecurityManager。
         */
        // 1. IniSecurityManagerFactory 加载 ini 配置文件，构造 SecurityManager 工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        // 2. 使用工厂构造 SecurityManager 实例
        SecurityManager manager = factory.getInstance();
        // 3. 将 SecurityManager 实例化为虚拟机的单独静态实例
        SecurityUtils.setSecurityManager(manager);

        // 获取当前执行用户
        Subject subject = SecurityUtils.getSubject();
        /*
         * Subject是一个安全术语意思是“当前运行用户的指定安全视图”
         * 在安全认证中，“Subject”可以认为是一个人，也可以认为是第三方进程、时钟守护任务、守护进程帐户或者其它
         */

        /*
         * Session 是 shiro 指定的一个实例，提供基本上所有 HttpSession 的功能，但具备额外的好处和不同：它不需要一个
         * HTTP 环境 这意味着你可以在任何程序中使用相同的 API，而根本不需要考虑发布环境
         */
        // 获取session
        Session session = subject.getSession();
        session.setAttribute("key", "testValue");
        String value = (String) session.getAttribute("key");
        logger.info("value is : {}", value);

        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken("root", "secret");
            token.setRememberMe(true);

            try {
                subject.login(token);
            } catch (Exception e) {
                logger.error("login error for this token, username:" + token.getUsername(), e);
            }
        }

        logger.info("User [{}] login success.", subject.getPrincipal());

        // 用户角色校验
        if (subject.hasRole("admin")) {
            logger.info("you have admin role.");
        } else {
            logger.info("you don't have admin role");
        }

        // 判断是否拥有某个特定动作或入口的权限
        if (subject.isPermitted("lightsaber:weild")) {
            logger.info("You may use a lightsaber ring.  Use it wisely.");
        } else {
            logger.info("Sorry, lightsaber rings are for schwartz masters only.");
        }
        
        // 检测用户是否具备访问某个类型特定实例的权限
        if ( subject.isPermitted( "winnebago:drive:eagle5" ) ) {
            logger.info("You are permitted to 'drive' the 'winnebago' with license plate (id) 'eagle5'.  " +
                        "Here are the keys - have fun!");
        } else {
            logger.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
        }
        
        subject.logout();

    }
    
    @Autowired
    private AuthorizationTest test;
    
    @Test
    public void testAuthorization(){
        test.signUp();
        
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken("usermanager", "12345");
            token.setRememberMe(true);

            try {
                subject.login(token);
            } catch (Exception e) {
                logger.error("login error for this token, username:" + token.getUsername(), e);
            }
        }
        test.createUser();
        test.updateUser();
    }
    
}
