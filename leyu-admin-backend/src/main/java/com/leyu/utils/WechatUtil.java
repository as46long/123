package com.leyu.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class WechatUtil {

    private static final String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public WechatLoginResult code2Session(String appId, String appSecret, String code) {
        String url = WECHAT_LOGIN_URL + "?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return parseWechatResponse(response.toString());
        } catch (Exception e) {
            throw new RuntimeException("微信登录失败：" + e.getMessage());
        }
    }

    private WechatLoginResult parseWechatResponse(String response) {
        WechatLoginResult result = new WechatLoginResult();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("openid")) {
                result.setOpenid(jsonNode.get("openid").asText());
            }
            if (jsonNode.has("session_key")) {
                result.setSessionKey(jsonNode.get("session_key").asText());
            }
            if (jsonNode.has("errcode")) {
                result.setErrcode(jsonNode.get("errcode").asText());
            }
            if (jsonNode.has("errmsg")) {
                result.setErrmsg(jsonNode.get("errmsg").asText());
            }
        } catch (Exception e) {
            System.out.println("解析微信响应失败: " + e.getMessage());
        }
        return result;
    }

    public static class WechatLoginResult {
        private String openid;
        private String sessionKey;
        private String errcode;
        private String errmsg;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }

        public String getErrcode() {
            return errcode;
        }

        public void setErrcode(String errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public boolean isSuccess() {
            return openid != null && !openid.isEmpty();
        }
    }
}
