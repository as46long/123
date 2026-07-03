package com.leyu.config;

import com.leyu.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security安全配置类
 * 配置接口访问权限、JWT过滤器、CORS跨域等
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * BCrypt密码编码器
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口 - 登录注册
                .requestMatchers("/api/user/login", "/api/user/register", "/api/user/wxLogin", "/api/user/wxLoginWithInfo", "/api/admin/login").permitAll()
                // 播放接口公开访问（用于记录播放次数）
                .requestMatchers(HttpMethod.POST, "/api/song/play/**").permitAll()
                // API 文档
                .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs/**").permitAll()
                // 歌曲相关 GET 接口公开访问
                .requestMatchers(HttpMethod.GET, "/api/song/**").permitAll()
                // 评论相关 GET 接口公开访问
                .requestMatchers(HttpMethod.GET, "/api/comment/**").permitAll()
                // 歌曲评论相关 GET 接口公开访问
                .requestMatchers(HttpMethod.GET, "/api/songComment/**").permitAll()
                // 系统配置相关 GET 接口公开访问（启动画面等）
                .requestMatchers(HttpMethod.GET, "/api/system/**").permitAll()
                // 管理员接口需要 ADMIN 角色
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 其他 API 需要认证
                .requestMatchers("/api/**").authenticated()
                // 其他请求
                .anyRequest().permitAll()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * CORS跨域配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
