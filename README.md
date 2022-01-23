# Getting Started

## Pre-requistics

---

1. Install / Run Redis Server

   ```bash
   $ wget http://download.redis.io/redis-stable.tar.gz 
   $ tar xvzf redis-stable.tar.gz 
   $ cd redis-stable 
   $ make

   # Install redis Server
   $ sudo make install
   
   # Start redis Server
   $ redis-server
   ```


## How To build
---

    ```bash
    
    $ maven clean install

    $ ls 
      -a----        2020-06-27   오후 2:59       45718327 money-0.0.1-SNAPSHOT.jar
      -a----        2020-06-27   오후 2:59          16118 money-0.0.1-SNAPSHOT.jar.original
    ```

## How To Run
---

    ```bash

    $ java -jar money-0.0.1-SNAPSHOT.jar
      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
    =========|_|==============|___/=/_/_/_/
    :: Spring Boot ::       (v2.4.0-SNAPSHOT)

    2020-06-27 15:10:53.408  INFO 4468 --- [           main] com.throwing.money.DemoApplication       : Starting DemoApplication v0.0.1-SNAPSHOT using Java 11.0.7 on Niny with PID 4468 (C:\Users\wonny\AppData\Local\Packages\CanonicalGroupLimited.UbuntuonWindows_79rhkp1fndgsc\LocalState\rootfs\home\wonny\throwingMoney\money\target\money-0.0.1-SNAPSHOT.jar started by wonny in C:\Users\wonny\AppData\Local\Packages\CanonicalGroupLimited.UbuntuonWindows_79rhkp1fndgsc\LocalState\rootfs\home\wonny\throwingMoney\money\target)
    2020-06-27 15:10:53.411  INFO 4468 --- [           main] com.throwing.money.DemoApplication       : The following profiles are active: local
    2020-06-27 15:10:54.329  INFO 4468 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
    2020-06-27 15:10:54.334  INFO 4468 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
    2020-06-27 15:10:54.554  INFO 4468 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 193 ms. Found 1 Redis repository interfaces.
    2020-06-27 15:10:56.651  INFO 4468 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
    2020-06-27 15:10:56.671  INFO 4468 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2020-06-27 15:10:56.672  INFO 4468 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.36]
    2020-06-27 15:10:56.769  INFO 4468 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2020-06-27 15:10:56.769  INFO 4468 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 3294 ms
    2020-06-27 15:10:58.243  INFO 4468 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
    2020-06-27 15:10:59.722  INFO 4468 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
    2020-06-27 15:10:59.736  INFO 4468 --- [           main] com.throwing.money.DemoApplication       : Started DemoApplication in 6.965 seconds (JVM running for 7.56)   
    ```

## API

--- 

Reference : target/openapi.json

1. 뿌리기 API
   > POST
   > ```/v1/api/service```
   
   > Body
   >```
   > RequestInfo{
   > targetMoney	integer($int32)
   > headCount	integer($int32)
   > }
   > ```

2. 받기 API
   > GET
   > ```/v1​/api​/money​/{token}```

3. 조회 API
   > GET
   > ```/v1/api/service/{token}```

## 구현 원리

---

1. Spring @RestController를 적용하여 ServiceController에 3가지의 RESET API 구현
   
   ```java
    @RestController
    @RequestMapping(value = "v1/api")
    public class ServiceController {}
   ```

2. Service Controller Return 값을 React 타입으로 Return
   
   ```java
   @RequestMapping(value = "/service", method = RequestMethod.POST)
    public Mono<String> createService(@RequestHeader(value = "X-USER-ID", required = true) int userID,
            @RequestHeader(value = "X-ROOM-ID", required = true) String roomID, @RequestBody RequestInfo requestInfo)

   @GetMapping(value = "/money/{token}")
   public Mono<Integer> getMoney(@RequestHeader(value = "X-USER-ID", required = true) int userID,
            @RequestHeader(value = "X-ROOM-ID", required = true) String roomID, @PathVariable("token") String token)

    @GetMapping(value = "/service/{token}")
    public Mono<ServiceInfo> getSerivce(@RequestHeader(value = "X-USER-ID", required = true) int userID,
            @RequestHeader(value = "X-ROOM-ID", required = true) String roomID, @PathVariable("token") String token)

   ```

3. DB 성능 개선을 위하여 Redis DB 사용

4. Token 예측이 불가능하도록 Random 함수를 이용하여 생성
   
   ```java
   public String newToken() {
        String token = "";
        final Random rnd = new Random();

        while (true) {
            for (int i = 0; i < 3; i++) {
                final int rIndex = rnd.nextInt(3);
                switch (rIndex) {
                    case 0:
                        // a-z
                        token = token.concat(String.valueOf((char) ((int) (rnd.nextInt(26)) + 97)));
                        break;
                    case 1:
                        // A-Z
                        token = token.concat(String.valueOf((char) ((int) (rnd.nextInt(26)) + 65)));
                        break;
                    case 2:
                        // 0-9
                        token = token.concat(String.valueOf((rnd.nextInt(10))));
                        break;
                }
            }

            if (dbService.getSerivce(token).isEmpty()) {
                logger.info("Token : {}", token);
                break;
            }
        }

        return token;
    }
   ```

### TODO

1. Dockerize
2. DB에서 Token 생성 이후, 7일이 지나면 삭제
