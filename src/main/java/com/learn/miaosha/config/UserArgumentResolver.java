package com.learn.miaosha.config;

import com.learn.miaosha.access.UserContext;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.service.impl.MiaoshaUserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();

        return clazz== MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {


        return UserContext.getUser();
    }
       /* HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String paramToken = request.getParameter(MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request,MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN);

        if(StringUtils.isEmpty(cookieToken)&&(StringUtils.isEmpty(paramToken))){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        if(token==null){
            return null;
        }
        else{
            MiaoshaUser user = miaoshaUserService.getByToken(response,token);
            return user;
        }

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
    }*/
}
