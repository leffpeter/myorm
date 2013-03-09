package com.leff.myorm.util.spring;

import com.leff.myorm.exception.internal.InternalException;
import com.leff.myorm.util.Util;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Clase utilidad para permitir acceder al contexto de Spring desde cualquier
 * punto de la aplicacion.
 */
@Component
public final class SpringContext implements ApplicationContextAware {

    /**
     * Mantiene el contexto de spring.
     */
    private static ApplicationContext applicationContext = null;

    /**
     * Permite establecer el contexto de spring
     *
     * @param ctx contexto.
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }

    /**
     * Devuelve el contexto de spring
     *
     * @return el contexto actual.
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Permite obtener un bean definido en el contexto de spring si este se ha
     * inicializado. En caso contrario devuelve null.
     *
     * @param name nombre del bean
     * @return instancia del bean recuperado del contexto.
     */
    public static Object getBean(String name) {
        if (applicationContext != null) {
            return applicationContext.getBean(name);
        }
        return null;
    }

    /**
     * Permite obtener un bean definido en el contexto de spring si este se ha
     * inicializado. En caso contrario devuelve null.
     *
     * @return instancia del bean recuperado del contexto.
     */
    public static <T> T getBean(Class<T> type) {
        if (applicationContext != null) {
            return applicationContext.getBean(type);
        }
        return null;
    }

    /**
     * Permite obtener un bean del contexto de spring sobre el que se ha creado
     * un proxy. En caso de no existir tal bean devuelve un null
     *
     * @param <T>  Clase del bean que se quiere recuperar
     * @param name Nombre del bean que se quiere recuperar
     * @return bean del contexto
     *         bean que tenga un proxy o que la clase sea incorrecta
     */
    public static <T> T getProxiedBean(String name, Class<T> type)
            throws InternalException {
        try {
            if (applicationContext != null) {
                Advised proxy = (Advised) applicationContext.getBean(name);
                if (proxy != null) {
                    return (T) proxy.getTargetSource().getTarget();
                }
            }
            return null;
        } catch (Exception ex) {
            throw new InternalException(ex);
        }
    }

    /**
     * Permite fijar un singleton target en un proxy del contexto
     *
     * @param proxyId Identificador del proxy en el contexto
     * @param target  Objeto de clase de tipo singleton que se fijara como target
     *                del proxy indicado
     */
    public static void setProxiedSingleton(String proxyId, Object target)
            throws InternalException {
        Advised proxy = (Advised) SpringContext.getBean(proxyId);
        if (Util.nullObject(proxy)) {
            throw new InternalException();
        }
        SingletonTargetSource ts = new SingletonTargetSource(target);
        proxy.setTargetSource(ts);
    }

    /**
     * Permite recuperar todos los beans del contexto.
     *
     * @return Mapa con todos los beans del contexto.
     */
    public static Map<String, Object> getBeans() {
        if (applicationContext != null) {
            return applicationContext.getBeansOfType(Object.class, false, true);
        }
        return null;
    }

    /**
     * Permite completar las las referencias de un objeto con los beans del
     * contexto
     *
     * @param object objeto a autocompletar.
     */
    public static void autowire(Object object) {
        if (applicationContext != null) {
            applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        }
    }
}
