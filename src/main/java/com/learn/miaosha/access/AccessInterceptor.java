package com.learn.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.redis.AccesKey;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.result.Result;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.service.impl.MiaoshaUserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
    RedisService redisService;

   @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

       if(handler instanceof HandlerMethod){
           MiaoshaUser miaoshaUser = getUser(request,response);
           UserContext.setUser(miaoshaUser);
           HandlerMethod hm = (HandlerMethod)handler;
           AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
           if(accessLimit==null){
               return true;
           }


           int seconds= accessLimit.seconds();
           int maxCount = accessLimit.maxCount();
           boolean needLogin = accessLimit.needLogin();
           //查询访问次数
           AccesKey ak = AccesKey.withExpire(seconds);
           String uri = request.getRequestURI();//访问路径
           String key = uri+"_"+miaoshaUser.getId();
           Integer count = redisService.get(ak,key,Integer.class);
           if(count==null){
               redisService.set(ak,key,1);
           }else if(count<5){
               redisService.incr(ak,key);//不足五次自加1
           }else{
               render(response, CodeMsg.REQUEST_LIMIT);
           }
           //校验needLogin参数
           if(needLogin){
               if(miaoshaUser==null){
                  render(response, CodeMsg.SESSION_EMPTY);
                   return false;
               }
           }
       }

        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cmg) throws IOException {
       response.setContentType("application/json;charset=UTF-8");
       OutputStream out = response.getOutputStream();
       String str = JSON.toJSONString(cmg);

       out.write(str.getBytes("UTF-8"));
       out.flush();
       out.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response)
    {
        String paramToken = request.getParameter(MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request,MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken)&&(StringUtils.isEmpty(paramToken))){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoshaUserService.getByToken(response,token);
    }
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(cookieName)){
                    return cookie.getValue();
                }
            }
        }
        else{
            return null;
        }
        return null;
    }

}

