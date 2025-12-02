/*
 *
 *     Copyright (c) 2019 - forever, javaeer All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the haoheio.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * Author: javaeer (javaeer@aliyun.com)
 *
 */

package cn.net.yunlou.bole.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class BusinessExceptionHandler {

    /**
     * 业务员类错误，状态码依然是200 具体错误码 查询具体的code值 @Param @Return @Author javaeer(javaeer @ aliyun.com) @Date
     * 2019/7/4 17:35
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public BusinessResponse handleResponzeException(BusinessException e) {
        log.error(e.getMessage(), e);
        return BusinessResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 请求参数异常类 @Param @Return RestResult @Author javaeer(javaeer @ aliyun.com) @Date 2019/7/4 17:34
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        String errorMsg = "";

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            errorMsg += error.getDefaultMessage() + "  ";
        }
        log.error(errorMsg, e);
        return BusinessResponse.error(BusinessStatus.REQUEST_PARAM_BLANK.getValue(), errorMsg);
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessResponse handleConstraintViolationException(ConstraintViolationException e) {

        String errorMsg = "";

        for (ConstraintViolation constraintViolation : e.getConstraintViolations()) {
            errorMsg += constraintViolation.getMessage() + "  ";
        }
        log.error(errorMsg, e);
        return BusinessResponse.error(BusinessStatus.REQUEST_PARAM_BLANK.getValue(), errorMsg);
    }

    /** 方法不被允许 @Param @Return @Author javaeer(javaeer @ aliyun.com) @Date 2019/7/4 17:35 */
    @ResponseBody
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BusinessResponse handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return BusinessResponse.error(BusinessStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Unsupported Media Type @Param @Return @Author javaeer(javaeer @ aliyun.com) @Date 2019/7/4
     * 17:35
     */
    @ResponseBody
    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public BusinessResponse handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) {
        log.error(e.getMessage(), e);
        return BusinessResponse.error(BusinessStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Unsupported Media Type @Param @Return @Author javaeer(javaeer @ aliyun.com) @Date 2019/7/4
     * 17:35
     */
    @ResponseBody
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessResponse handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return BusinessResponse.error(BusinessStatus.REQUEST_PARAM_BLANK);
    }

    /**
     * Unsupported Media Type @Param @Return @Author javaeer(javaeer @ aliyun.com) @Date 2019/7/4
     * 17:35
     */
    @ResponseBody
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BusinessResponse handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage(), e);
        return BusinessResponse.error(BusinessStatus.FORBIDDEN_NO_PERMISSION);
    }
}
