/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.quarkus.core.ioc;

import io.nop.api.core.ApiConstants;
import io.nop.api.core.ApiErrors;
import io.nop.api.core.exceptions.NopException;
import io.nop.api.core.ioc.IBeanContainer;
import io.nop.commons.util.StringHelper;
import io.nop.quarkus.core.QuarkusConstants;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InjectableBean;
import io.quarkus.arc.InjectableInstance;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.arc.impl.ArcContainerImpl;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class NopQuarkusBeanContainer implements IBeanContainer {

    @Override
    public String getId() {
        return "quarkus";
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    ArcContainer container() {
        return ArcContainerImpl.instance();
    }

    @Override
    public boolean containsBeanType(Class<?> clazz) {
        return container().instance(clazz).isAvailable();
    }

    @Override
    public String findAutowireCandidate(Class<?> beanType) {
        InstanceHandle<?> bean = container().instance(beanType);
        return getQuarkusBeanId(bean);
    }

    private String getQuarkusBeanId(InstanceHandle<?> bean) {
        if (bean.isAvailable()) {
            return QuarkusConstants.QUARKUS_ID_PREFIX + bean.getBean().getIdentifier();
        }
        return null;
    }

    @Override
    public boolean isRunning() {
        return container().isRunning();
    }

    @Override
    public boolean containsBean(String name) {
        InjectableBean<?> quarkusBean = getQuarkusBean(name);
        return quarkusBean != null;
    }

    @Nonnull
    @Override
    public Object getBean(String name) {
        InjectableBean<?> quarkusBean = getQuarkusBean(name);
        Object bean = quarkusBean == null ? null : Arc.container().instance(quarkusBean).get();
        if (bean == null)
            throw new NopException(ApiErrors.ERR_IOC_UNKNOWN_BEAN_FOR_NAME).param(ApiErrors.ARG_BEAN_NAME, name);
        return bean;
    }

    @Nonnull
    @Override
    public <T> T getBeanByType(Class<T> clazz) {
        T bean = container().instance(clazz).get();
        if (bean == null)
            throw new NopException(ApiErrors.ERR_IOC_UNKNOWN_BEAN_FOR_TYPE).param(ApiErrors.ARG_BEAN_TYPE, clazz);
        return bean;
    }

    @Override
    public <T> T tryGetBeanByType(Class<T> clazz) {
        T bean = container().instance(clazz).get();
        if (bean == null)
            return null;
        return bean;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        Map<String, T> ret = new HashMap<>();

        for (InstanceHandle<T> handle : Arc.container().select(clazz).handles()) {
            String beanId = getQuarkusBeanId(handle);
            ret.put(beanId, handle.get());
        }
        return ret;
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annClass) {
        Map<String, Object> ret = new HashMap<>();

        InjectableInstance<Object> handles = container().select(Object.class, new MarkerInterfaceAnnotation(annClass));

        for (InstanceHandle<Object> handle : handles.handles()) {
            String beanId = getQuarkusBeanId(handle);
            ret.put(beanId, handle.get());
        }
        return ret;
    }

    @Override
    public String getBeanScope(String name) {
        InjectableBean<?> bean = getQuarkusBean(name);
        if (bean == null)
            throw new NopException(ApiErrors.ERR_IOC_UNKNOWN_BEAN_FOR_NAME).param(ApiErrors.ARG_BEAN_NAME, name);

        Class<? extends Annotation> scope = bean.getScope();
        if (scope == Dependent.class || scope == ApplicationScoped.class)
            return ApiConstants.BEAN_SCOPE_SINGLETON;

        if (scope == RequestScoped.class)
            return ApiConstants.BEAN_SCOPE_REQUEST;

        return StringHelper.camelCaseToHyphen(scope.getSimpleName());
    }

    @Override
    public Class<?> getBeanClass(String name) {
        InjectableBean<?> bean = getQuarkusBean(name);
        if (bean == null)
            throw new NopException(ApiErrors.ERR_IOC_UNKNOWN_BEAN_FOR_NAME).param(ApiErrors.ARG_BEAN_NAME, name);
        return bean.getBeanClass();
    }

    private InjectableBean<?> getQuarkusBean(String name) {
        if (name.startsWith(QuarkusConstants.QUARKUS_ID_PREFIX))
            return Arc.container().bean(name.substring(QuarkusConstants.QUARKUS_ID_PREFIX.length()));
        return Arc.container().namedBean(name);
    }
}