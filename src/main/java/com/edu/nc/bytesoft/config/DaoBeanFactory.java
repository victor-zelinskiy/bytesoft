package com.edu.nc.bytesoft.config;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.dao.ObjectDao;
import com.edu.nc.bytesoft.dao.impl.ObjectDaoReflect;
import com.edu.nc.bytesoft.model.BaseEntity;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

@Configurable
public class DaoBeanFactory extends DefaultListableBeanFactory {


    private static final Log LOG = Log.get(DaoBeanFactory.class);

    public DaoBeanFactory(DefaultListableBeanFactory delegate) {
        super(delegate);
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor,
                                    String beanName, Set<String> autowiredBeanNames,
                                    TypeConverter typeConverter) throws BeansException {
        if (ObjectDao.class == descriptor.getDependencyType()) {
            return doResolveDaoDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
        } else {
            return ((DefaultListableBeanFactory) getParentBeanFactory()).resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
    }


    private Object doResolveDaoDependency(DependencyDescriptor descriptor, String beanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
        Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, beanName);
        if (result == null) {
            result = ((DefaultListableBeanFactory) getParentBeanFactory()).doResolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
        if (result instanceof ObjectDaoReflect) {
            ((ObjectDaoReflect) result).setObjectClass(getGenericType(descriptor));
        }
        return result;
    }

    private Class<?> getGenericType(DependencyDescriptor descriptor) {
        MethodParameter methodParameter = descriptor.getMethodParameter();
        Type genericType = null;
        if (methodParameter != null) {
            genericType = methodParameter.getGenericParameterType();
        }
        if (genericType == null) {
            Field field = descriptor.getField();
            if (field != null) {
                genericType = field.getGenericType();
            }
        }

        if (genericType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                return (Class<?>) actualTypeArguments[0];
            }
        }

        return BaseEntity.class;
    }
}
