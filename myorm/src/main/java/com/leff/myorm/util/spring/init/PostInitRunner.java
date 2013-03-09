package com.leff.myorm.util.spring.init;

import com.leff.myorm.util.spring.SpringContext;
import com.leff.myorm.util.spring.init.annotation.PostInit;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ContextRefresh listener that invokes methods, annotated with {@link PostInit}
 * in the given {@link PostInit#order()}. Those methods can't have parameteres.
 *
 * @author leff
 */
@Component
public final class PostInitRunner implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * Devuelve el target de un proxy.
     *
     * @param bean bean proxy.
     * @return target del proxy.
     */
    private Object deproxy(Object bean) {
        try {
            return ((Advised) bean).getTargetSource().getTarget();
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * Comprueba si un bean es un proxy.
     *
     * @param bean Bean a analizar.
     * @return True si es una instancia de Advised. False en caso contrario.
     */
    private boolean isProxy(Object bean) {
        return (bean instanceof Advised);
    }

    /**
     * Calls methods annotated with {@link PostInit} in the given {@link PostInit#order()}.
     * Those methods can't have parameteres.
     */
    private void doOnApplicationEvent() {
        Map<String, Object> beans = SpringContext.getBeans();
        List<PostInitMethod> postInitMethods = new LinkedList<PostInitMethod>();
        for (Entry<String, Object> entry : beans.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            Object targetBean = isProxy(bean) ? deproxy(bean) : bean;
            String errorMsg = "PostInit of bean " + beanName + " failed.";
            for (Method targetMethod : targetBean.getClass().getDeclaredMethods()) {
                PostInit postInit = getAnnotation(targetMethod, PostInit.class);
                if (postInit != null && targetMethod.getParameterTypes().length == 0) {
                    int order = postInit.order();
                    Method method = null;
                    try {
                        method = bean.getClass().getMethod(targetMethod.getName(), (Class[]) null);
                    } catch (NoSuchMethodException ex) {
                        throw new BeanCreationException(errorMsg, ex);
                    } catch (SecurityException ex) {
                        throw new BeanCreationException(errorMsg, ex);
                    }
                    postInitMethods.add(new PostInitMethod(method, bean, order, beanName));
                }
            }
        }
        invokeInOrder(postInitMethods);
    }

    /**
     * Invocación ordenada de la lista de métodos @PostInit.
     *
     * @param methodList lista de métodos a invocar.
     */
    private void invokeInOrder(List<PostInitMethod> methodList) {
        Collections.sort(methodList);
        for (PostInitMethod method : methodList) {
            method.invoke();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        doOnApplicationEvent();
    }

    /**
     * Recupera una anotación de un método, buscando en las superclases.
     *
     * @param <T>
     * @param method          método.
     * @param annotationClass anotación a recuperar.
     * @return La anotación encontrada o null en caso de no encontrarla.
     */
    private <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        if (method.isAnnotationPresent(annotationClass)) {
            return method.getAnnotation(annotationClass);
        } else {
            Method superMethod = getSuperMethod(method);
            return superMethod != null ? getAnnotation(superMethod, annotationClass) : null;
        }
    }

    /**
     * @param method
     * @return
     */
    private Method getSuperMethod(Method method) {
        Class<?> declaring = method.getDeclaringClass();
        if (declaring.getSuperclass() != null) {
            Class<?> superClass = declaring.getSuperclass();
            try {
                Method superMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
                if (superMethod != null) {
                    return superMethod;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Clase wrapper de un método @PostInit
     */
    private final class PostInitMethod implements Comparable<PostInitMethod> {

        /**
         * Método.
         */
        private Method method;
        /**
         * Nombre del bean.
         */
        private String beanName;
        /**
         * Instancia del bean.
         */
        private Object beanInstance;
        /**
         * orden de ejecución.
         */
        private int order;

        /**
         * constructor.
         *
         * @param method       método.
         * @param beanInstance Instancia del bean.
         * @param order        orden de ejecución.
         * @param beanName     Nombre del bean.
         */
        private PostInitMethod(Method method, Object beanInstance, int order, String beanName) {
            this.method = method;
            this.beanInstance = beanInstance;
            this.order = order;
            this.beanName = beanName;
        }

        /**
         * getter.
         *
         * @return the method.
         */
        public Method getMethod() {
            return method;
        }

        /**
         * getter.
         *
         * @return the beanInstance.
         */
        public Object getBeanInstance() {
            return beanInstance;
        }

        /**
         * getter.
         *
         * @return the beanName.
         */
        public String getBeanName() {
            return beanName;
        }

        @Override
        public int compareTo(PostInitMethod anotherPostInitializingMethod) {
            int thisVal = this.order;
            int anotherVal = anotherPostInitializingMethod.order;
            return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PostInitMethod that = (PostInitMethod) o;

            return order == that.order && !(beanName != null ? !beanName.equals(that.beanName) : that.beanName != null) && !(method != null ? !method.equals(that.method) : that.method != null);
        }

        @Override
        public int hashCode() {
            int result;
            result = (method != null ? method.hashCode() : 0);
            result = 31 * result + (beanInstance != null ? beanInstance.hashCode() : 0);
            result = 31 * result + order;
            result = 31 * result + (beanName != null ? beanName.hashCode() : 0);
            return result;
        }

        /**
         * invocación del método.
         */
        public void invoke() {
            String errorMsg = "PostInit of bean " + beanName + " failed.";
            try {
                this.method.invoke(beanInstance);
            } catch (IllegalAccessException ex) {
                throw new BeanCreationException(errorMsg, ex);
            } catch (IllegalArgumentException ex) {
                throw new BeanCreationException(errorMsg, ex);
            } catch (InvocationTargetException ex) {
                throw new BeanCreationException(errorMsg, ex);
            }
        }
    }
}