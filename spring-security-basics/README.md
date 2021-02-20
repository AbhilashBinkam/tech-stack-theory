When we add spring-security dependency we get form based authentication as default auth mechanism.
with spring-security we can configure different types of authentications.

1. Enable Basic Auth with spring-security:

@EnableWebSecurity -- represents the annotation at class level and indicates the security configuration related to the
application. This is the starting point for the configuration.

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

}

we can override few methods from the parent class and configure the security as per the needs.
currently we've overridden configure(HttpSecurity http).

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();  --> //Defines that any request must be authenticated and the auth
                                   //should happen using httpBasic which is basic authentication.

    }
}

when we run the application we get a pop-up to provide the username and password instead of providing the details
in the form (webpage).

setback for basic-auth:
we cannot logout from the active user session

2. ANT MATCHERS

These are used to configure the security for the URLs that are to be whitelisted for specific matchers
that we define in the matchers pattern.

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/index.html", "/css/*", "/js/*") --> //matchers that we define to be whitelisted
                                                                        //from the security and add permitAll() method
                                                                        to permit all the URL's matched to the matchers
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

    }
}

3. IN MEMORY USER DETAILS MANAGER:

To create a user we to override the userDetailsService() method --> This method return
UserDetailsService which is an interface.

while constructing the method we need to define the username, password and role
of the user by using the spring security provided builder class User.java

Spring-security has few implementation classes that provided the implementation for UserDetailsService interface.
we can use one of the implementation, currently we're using InMemoryUserDetailsManager.java.

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails jamesUser = User.builder().username("james").password("password").roles("STUDENT").build();

        return new InMemoryUserDetailsManager(jamesUser);
    }

if we run the app with the above config in place and provided user name and password defined in the config then we get an exception.
IllegalArgumentException: There is no password encoder mapped for the id "null" or
o.s.s.c.bcrypt.BCryptPasswordEncoder     : Encoded password does not look like BCrypt

4. PASSWORD ENCODING WITH BCRYPT:

Create new class (PasswordConfig.java). we use the PasscodeEncoder interface provided by spring-security.
spring-security provides us with few implementation classes and the most popular class
to use is BCryptPasswordEncoder.java. we can pass the password strength as argument to the constructor(1-10)

Once we define the above config, we need to initialize and autowire the PasswordEncoder interface in the configuration file
and use encode method at the palce where we are defining the user name and password.

PasswordConfig.java

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

ApplicationConfigSecurity.java

    private PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
	
AT THIS STAGE WE SHOULD BE ABLE TO LOGIN TO THE APP WITH THE USERNAME AND PASSWORD DEFINED IN THE CONFIG

5. 











