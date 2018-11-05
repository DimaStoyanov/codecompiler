package ru.tsystems.tchallenge.codemaster.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LocaleInterceptor implements HandlerInterceptor {
    private static List<String> supportedLocales = new ArrayList<>() {{
        add("en");
        add("ru");
    }};

    private static final Logger logger = LogManager.getLogger(LocaleInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        try {
            String localeStr = httpServletRequest.getHeader("Accept-Language");
            if (localeStr != null && !localeStr.isEmpty()) {
                Locale.LanguageRange.parse(localeStr).stream()
                        .sorted(Comparator.comparing(Locale.LanguageRange::getWeight).reversed())
                        .filter(locale -> supportedLocales.indexOf(locale.getRange()) > -1)
                        .map(range -> new Locale(range.getRange()))
                        .findFirst().ifPresent(Locale::setDefault);
            }
        } catch(Exception ex) {
            logger.warn("handle user locale " + httpServletRequest.getHeader("Accept-Language"), ex.getMessage(), "empty", null);//httpServletRequest.getUserPrincipal()
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
