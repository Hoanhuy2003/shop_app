package com.project.shopapp.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;

@RequiredArgsConstructor
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
}
