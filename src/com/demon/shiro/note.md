# Configuration 配置

可以使用多种形式对 shiro 进行配置，比如，XML（spring、JBoss 等），YAML，JSON，Groovy，INI
ini 只是shiro 一种基本的配置方式，使其可在任何环境中进行配置


# Detailed Architecture 详细架构

下面的图表展示了 Shiro 的核心架构思想，下面有简单的解释。
![](https://oscimg.oschina.net/oscnet/97215848578312ceb4bf28f455aff9c36bd.jpg)

* Subject (org.apache.shiro.subject.Subject)
正在与软件交互的一个特定的实体“view”（用户、第三方服务、时钟守护任务等）。

* SecurityManager (org.apache.shiro.mgt.SecurityManager)
如同上面提到的，SecurityManager 是 Shiro 的核心，它基本上就是一把“保护伞”用来协调它管理的组件使之平稳地一起工作，它也管理着 Shiro 中每一个程序用户的视图，所以它知道每个用户如何执行安全操作。

* Authenticator(org.apache.shiro.authc.Authenticator)
Authenticator 是一个组件，负责执行和反馈用户的认证（登录），如果一个用户尝试登录，Authenticator 就开始执行。Authenticator 知道如何协调一个或多个保存有相关用户/帐号信息的 Realm，从这些 Realm中获取这些数据来验证用户的身份以确保用户确实是其表述的那个人。

* Authentication Strategy(org.apache.shiro.authc.pam.AuthenticationStrategy)
如果配置了多个 Realm，AuthenticationStrategy 将会协调 Realm 确定在一个身份验证成功或失败的条件（例如，如果在一个方面验证成功了但其他失败了，这次尝试是成功的吗？是不是需要所有方面的验证都成功？还是只需要第一个？）

* Authorizer(org.apache.shiro.authz.Authorizer)
Authorizer 是负责程序中用户访问控制的组件，它是最终判断一个用户是否允许做某件事的途径，像 Authenticator 一样，Authorizer 也知道如何通过协调多种后台数据源来访问角色和权限信息，Authorizer 利用这些信息来准确判断一个用户是否可以执行给定的动作。

* SessionManager(org.apache.shiro.session.mgt.SessionManager)
SessionManager 知道如何创建并管理用户 Session 生命周期而在所有环境中为用户提供一个强有力的 Session 体验。这在安全框架领域是独一无二--Shiro 具备管理在任何环境下管理用户 Session 的能力，即使没有 Web/Servlet 或者 EJB 容器。默认情况下，Shiro 将使用现有的session（如Servlet Container），但如果环境中没有，比如在一个独立的程序或非 web 环境中，它将使用它自己建立的 session 提供相同的作用，sessionDAO 用来使用任何数据源使 session 持久化。

* SessionDAO(org.apache.shiro.session.mgt.eis.SessionDAO)
SessionDAO 代表 SessionManager 执行 Session 持久（CRUD）动作，它允许任何存储的数据挂接到 session 管理基础上。

* CacheManager(org.apache.shiro.cache.CacheManager)
CacheManager 为 Shiro 的其他组件提供创建缓存实例和管理缓存生命周期的功能。因为 Shiro 的认证、授权、会话管理支持多种数据源，所以访问数据源时，使用缓存来提高访问效率是上乘的选择。当下主流开源或企业级缓存框架都可以继承到 Shiro 中，来获取更快更高效的用户体验。

* Cryptography (org.apache.shiro.crypto.*)
Cryptography 在安全框架中是一个自然的附加产物，Shiro 的 crypto 包包含了易用且易懂的加密方式，Hashes（即digests）和不同的编码实现。该包里所有的类都亦于理解和使用，曾经用过 Java 自身的加密支持的人都知道那是一个具有挑战性的工作，而 Shiro 的加密 API 简化了 java 复杂的工作方式，将加密变得易用。

* Realms (org.apache.shiro.realm.Realm)
如同上面提到的，Realm 是 shiro 和你的应用程序安全数据之间的“桥”或“连接”，当实际要与安全相关的数据进行交互如用户执行身份认证（登录）和授权验证（访问控制）时，shiro 从程序配置的一个或多个Realm 中查找这些数据，你需要配置多少个 Realm 便可配置多少个 Realm（通常一个数据源一个），shiro 将会在认证和授权中协调它们。


# Remembered vs Authenticated 记住的 vs 已验证

* 记住的（Remembered）：一个被记住的 Subject 不会是匿名的，拥有一个已知的身份（也就是说subject.getPrincipals())返回非空）。它的身份被先前的认证过程所记住，并存于先前session中，一个被认为记住的对象在执行subject.isRemembered())返回true。
* 已验证（Authenticated）：一个被验证的 Subject 是成功验证后（如登录成功）并存于当前 session 中，一个被认为验证过的对象调用subject.isAuthenticated()) 将返回true。

两者是互斥的，一个标识值为真， 则另一个就为假

### 两者的区别

验证（authentication）有明显的证明含义，也就是说，需要担保Subject 已经被证明他们是谁。
当一个用户仅仅在上一次与程序交互时被记住，证明的状态已经不存在了：被记住的身份只是给系统一个信息这个用户可能是谁，但不确定，没有办法担保这个被记住的 Subject 是所要求的用户，一旦这个 Subject 被验证通过，他们将不再被认为是记住的因为他们的身份已经被验证并存于当前的session中。
所以尽管程序大部分情况下仍可以针对记住的身份执行用户特定的逻辑，比如说自定义的视图，但不要执行敏感的操作直到用户成功执行身份验证使其身份得到确定。
例如，检查一个 Subject 是否可以访问金融信息应该取决于是否被验证（isAuthenticated()）而不是被记住（isRemembered()），要确保该Subject 是所需的和通过身份验证的。

举个例子：假设你前一天登录了购物网站，并添加商品到了购物车，第二天过来时，还可以看到购物车内的商品，表明网站记住了你的身份，但是当你要付款等敏感操作时，网站会强制你登录来验证你的身份


# AuthenticationStrategy 认证策略

若配置了多个数据源Realm，则需要AuthenticationStrategy 组件来确定认证是否成功
* AtLeastOneSuccessfulStrategy  如果有一个或多个Realm验证成功，所有的尝试都被认为是成功的，如果没有一个验证成功，则该次尝试失败
* FirstSuccessfulStrategy 只有从第一个成功验证的Realm返回的信息会被使用，以后的Realm将被忽略，如果没有一个验证成功，则该次尝试失败
* AllSuccessfulStrategy   所有配置的Realm在全部尝试中都成功验证才被认为是成功，如果有一个验证不成功，则该次尝试失败。

多个realm 的认证顺序时根据配置文件中的顺序依次执行的，也可以配置securityManager.realms = $oneRealm, $twoRealm, $threeRealm 来指定顺序

### 认证流程

1. 程序代码调用 Subject.login 方法，向AuthenticationToken（认证令牌）实例的构造函数传递用户的身份和证明。
2. Subject 实例，通常是一个 DelegatingSubject（或其子类）通过调用 securityManager.login(token )将这个令牌转交给程序的 SecurityManager。
3. SecurityManager，基本的“安全伞”组件，得到令牌并通过调用 authenticator.authenticate(token))简单地将其转交它内部的 Authenticator 实例，大部分情况下是一个 ModularRealmAuthenticator 实例，用来支持在验证过程中协调一个或多个Realm实例。ModularRealmAuthenticator 本质上为 Apache Shiro（在 PAM 术语中每一个 Realm 称为一个“模块”）提供一个 PAM 类型的范例。
4. 如程序配置了多个 Realm，ModularRealmAuthenticator实例将使用其配置的 AuthenticationStrategy 开始一个 多 Realm 身份验证的尝试。在 Realm 被验证调用的整个过程中，AuthenticationStrategy（安全策略）被调用用来回应每个Realm结果。如果仅有一个 Realm 被配置，它直接被调用--在单 Realm 程序中不需要AuthenticationStrategy。
5. 每一个配置的 Realm 都被检验看其是否支持)提交的AuthenticationToken，如果支持，则该 Realm 的 getAuthenticationInfo) 方法随着提交的牌被调用，getAuthenticationInfo 方法为特定的 Realm 有效提供一次独立的验证尝试。

### shiro 提供的Realm

一般继承AuthorizingRealm（授权）即可；其继承了AuthenticatingRealm（即身份验证），而且也间接继承了CachingRealm（带有缓存实现）

* org.apache.shiro.realm.text.IniRealm：[users]部分指定用户名/密码及其角色；[roles]部分指定角色即权限信息
* org.apache.shiro.realm.text.PropertiesRealm： user.username=password,role1,role2指定用户名/密码及其角色；role.role1=permission1,permission2指定角色及权限信息
* org.apache.shiro.realm.jdbc.JdbcRealm：通过sql查询相应的信息，如 select password from users where username=? 获取用户密码，select password, password\_salt from users where username=? 获取用户密码及salt；select role\_name from user\_roles where username=? 获取用户角色；select permission from roles\_permissions where role\_name=? 获取角色对应的权限信息；也可以调用相应的api进行自定义sql
* 自定义的AuthenticationStrategy：继承org.apache.shiro.authc.pam.AbstractAuthenticationStrategy，实现具体的方法。beforeAllAttempts 在所有Realm 验证之前调用；beforeAttempt 在每个Realm 之前调用；afterAttempt 在每个Realm 之后调用；afterAllAttempts 在所有Realm 之后调用


# Authorization 授权

授权有三个核心元素，在 Shiro 中我们经常要用到它们：权限（permissions）、角色（roles）和用户（users）。

在 Shiro 中执行授权可以有三种途径：
* 程序代码--你可以在你的 JAVA 代码中执行用类似于 if 和 else 的结构来执行权限检查。
* JDK 注解--你可以在你的 JAVA 方法上附加权限注解
* JSP/GSP 标签--你可以基于角色和权限控制 JSP 或 GSP 页面输出内容。

### 授权流程

1. 程序或框架代码调用一个 Subject 的hasRole*、checkRole*、 isPermitted*或者 checkPermission*方法，传递所需的权限或角色。
2. Subject实例，通常是一个 DelegatingSubject（或子类），通过调用securityManager 与各 hasRole*、checkRole*、 isPermitted* 或 checkPermission* 基本一致的方法将权限或角色传递给程序的 SecurityManager(实现了 org.apache.shiro.authz.Authorizer 接口)
3. SecurityManager 作为一个基本的“保护伞”组件,接替/代表其内部 org.apache.shiro.authz.Authorizer 实例通过调用 authorizer 的各自的 hasRole*, checkRole* , isPermitted* ,或 checkPermission* 方法。 authorizer 默认情况下是一个实例 ModularRealmAuthorizer 支持协调一个或多个实例 Realm 在任何授权操作实例。
4. 检查每一个被配置的 Realm 是否实现相同的 Authorizer接口，如果是，Realm 自己的各 hasRole*、checkRole*、 isPermitted* 或 checkPermission* 方法被调用。

