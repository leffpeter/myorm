package com.leff.myorm.util.reflection;

import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.util.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utilidades que hacen uso de la reflexión.
 */
public final class UtilReflection {

    /**
     * Array de Class vacío
     */
    public static final Class[] CLASS_ARRAY_EMPTY = new Class[]{};
    /**
     * Array de Objetc vacío
     */
    public static final Object[] OBJECT_ARRAY_EMPTY = new Object[]{};
    /**
     * Recursividad desactivada.
     */
    public static final int NO_RECURSIVE_DEPTH = 0;
    /**
     * Recursividad infinita (sin límite de profundidad).
     */
    public static final int INFINITE_RECURSIVE_DEPTH = -1;

    private UtilReflection() {
    }

    /**
     * Metodo que obtiene un conjunto fields anotados
     *
     * @param clazz           clase donde se buscan los fields
     * @param baseClazz       clase base que marca el nivel donde se parara la
     *                        busqueda
     * @param annotationClass clase de la anotacion a buscar
     * @return conjunto de fields con la anotacion
     */
    public static List<Field> findFieldByAnnotation(Class<?> clazz, Class<?> baseClazz,
                                                    Class<? extends Annotation> annotationClass) {
        Class<?> actualClazz = clazz;
        List<Field> result = new ArrayList<Field>();
        boolean stop = false;
        while (!stop) {
            Field[] fields = actualClazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
            actualClazz = actualClazz.getSuperclass();
            stop = actualClazz.equals(baseClazz);
        }
        return result;
    }

    /**
     * Obtiene el método GET del atributo indicado. (siguiendo Java Code
     * Conventions).
     *
     * @param clazz clase objetivo.
     * @param field campo objetivo.
     * @return Método getter "estándar", es decir "get" + nombre del field con
     *         la primera letra en Mayúscula.
     * @throws NoSuchMethodException cuando no se encuentra el método.
     */
    public static Method getGetter(Class<?> clazz, Field field) throws NoSuchMethodException {
        if (clazz == null || field == null) {
            return null;
        }
        Method result = null;
        String methodName = "get" + Util.firstUp(field.getName());
//        result = clazz.getMethod(methodName, (Class<?>[]) null);
        result = clazz.getMethod(methodName, CLASS_ARRAY_EMPTY);
        return result;
    }

    /**
     * Obtiene el método GET del atributo con el nombre indicado. (siguiendo
     * Java Code Conventions).
     *
     * @param clazz     clase objetivo.
     * @param fieldName campo objetivo.
     * @return Método getter "estándar", es decir "get" + nombre del field con
     *         la primera letra en Mayúscula.
     * @throws NoSuchMethodException cuando no se encuentra el método.
     */
    public static Method getGetter(Class<?> clazz, String fieldName) throws NoSuchMethodException {
        if (clazz == null || fieldName == null) {
            return null;
        }
        Method result = null;
        String methodName = "get" + Util.firstUp(fieldName);
//        result = clazz.getMethod(methodName, (Class<?>[]) null);
        result = clazz.getMethod(methodName, CLASS_ARRAY_EMPTY);
        return result;
    }

    /**
     * Obtiene el método SET del atributo indicado. (siguiendo Java Code
     * Conventions).
     *
     * @param clazz clase objetivo.
     * @param field campo objetivo.
     * @return Método setter "estándar", es decir "set" + nombre del field con
     *         la primera letra en Mayúscula.
     * @throws NoSuchMethodException cuando no se encuentra el método.
     */
    public static Method getSetter(Class<?> clazz, Field field) throws NoSuchMethodException {
        if (clazz == null || field == null) {
            return null;
        }
        Method result = null;
        String methodName = "set" + Util.firstUp(field.getName());
        result = clazz.getMethod(methodName, field.getType());
        return result;
    }

    /**
     * Obtiene el método SET del atributo con el nombre indicado. (siguiendo
     * Java Code Conventions).
     *
     * @param clazz     clase objetivo.
     * @param fieldName campo objetivo.
     * @return Método setter "estándar", es decir "set" + nombre del field con
     *         la primera letra en Mayúscula.
     * @throws NoSuchMethodException cuando no se encuentra el método.
     * @throws NoSuchFieldException  cuando no se encuentra el field.
     */
    public static Method getSetter(Class<?> clazz, String fieldName) throws NoSuchMethodException, NoSuchFieldException {
        if (clazz == null || fieldName == null) {
            return null;
        }
        return getSetter(clazz, getField(clazz, fieldName));
    }

    /**
     * Extrae un valor: intenta hacer un get del field, y en caso de obtener un
     * null, intenta obtenerlo mediante el getter.
     *
     * @param <T>
     * @param <V>
     * @param field  field que se quiere obtener.
     * @param target Objecto del cual se desea extraer el valor.
     * @return Valor obtenido al extraer.
     * @throws ReflectException Cuando ocurre algún error al acceder al target
     *                          mediante reflect.
     */
    @SuppressWarnings("unchecked")
    public static <T, V> V getValue(Field field, T target) throws ReflectException {
        if (field == null || target == null) {
            return null;
        }
        V entity = null;
        try {
            field.setAccessible(true);
            entity = (V) field.get(target);
            if (entity == null) {
                Method getter = getGetter(target.getClass(), field);
//                entity = (V) getter.invoke(target, (Object[]) null);
                entity = (V) getter.invoke(target, OBJECT_ARRAY_EMPTY);
            }
        } catch (InvocationTargetException ex) {
            throw new ReflectException(ex);
        } catch (NoSuchMethodException ex) {
            throw new ReflectException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectException(ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectException(ex);
        }
        return entity;
    }

    /**
     * Devuelve todos los fields y los heredados.
     *
     * @param clazz clase.
     * @return Lista de fields heredados.
     */
    public static List<Field> getInheritedFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        if (clazz != null) {
            for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
                fields.addAll(Arrays.asList(c.getDeclaredFields()));
            }
        }
        return fields;
    }

    /**
     * Devuelve un field por su nombre. Soporte herencia, es decir, es capaz de
     * devolver un Field de cualquier superclase de la que hereda clazz.
     *
     * @param clazz     clase.
     * @param fieldName
     * @return Field con el nombre indicado.
     * @throws NoSuchFieldException cuando no se encuentra el field indicado.
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        if (clazz == null || fieldName == null) {
            return null;
        }
        Field field = null;
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            try {

                field = c.getDeclaredField(fieldName);

                break;
            } catch (NoSuchFieldException ex) {

                continue;
            } catch (SecurityException ex) {

                continue;
            }
        }
        if (field == null) {
            throw new NoSuchFieldException(fieldName + " on " + clazz.getName());
        }
        return field;
    }

    /**
     * Extrae un valor de forma simple: intenta obtenerlo mediante el getter , y
     * en caso de obtener un null, intenta hacer un get del field.
     *
     * @param <T>
     * @param <V>
     * @param fieldName field que se quiere obtener.
     * @param target    Objecto del cual se desea extraer el valor.
     * @return Valor obtenido al extraer.
     * @throws ReflectException Cuando ocurre algún error al acceder al target
     *                          mediante reflect.
     */
    @SuppressWarnings("unchecked")
    public static <T, V> V getValueSimple(String fieldName, T target) throws ReflectException {
        if (fieldName == null || target == null) {
            return null;
        }
        V value = null;
        try {

            Method getter = getGetter(target.getClass(), fieldName);
//                entity = (V) getter.invoke(target, (Object[]) null);
            value = (V) getter.invoke(target, OBJECT_ARRAY_EMPTY);
        } catch (InvocationTargetException ex) {
            value = null;
        } catch (NoSuchMethodException ex) {
            value = null;
        } catch (IllegalArgumentException ex) {
            value = null;
        } catch (IllegalAccessException ex) {
            value = null;
        }
        if (value == null) {
            try {
                Field field = getField(target.getClass(), fieldName);
                field.setAccessible(true);
                value = (V) field.get(target);
            } catch (NoSuchFieldException ex) {
                throw new ReflectException(ex);
            } catch (IllegalAccessException ex) {
                throw new ReflectException(ex);
            }
        }
        return value;
    }

    /**
     * Extrae un valor: intenta hacer un get del field indicado por nombre, y en
     * caso de obtener un null, intenta obtenerlo mediante el getter. Esta es
     * una implementación recursiva que admite profundidad, es decir, el
     * fieldName puede contener el carácter "." para indicar que es un atributo
     * de una subclase dentro de el.
     *
     * @param <T>
     * @param <V>
     * @param fieldName Nombre del field que se quiere obtener. Admite
     *                  concatenación de atributos para la extracción: p.e.:
     *                  atributoA.atributoB.atributoC para extraer el valor del atributoC
     *                  contenido en atributoB que está a su vez contenido en atributoA del
     *                  target indicado.
     * @param target    Objecto del cual se desea extraer el valor.
     * @return Valor obtenido al extraer.
     * @throws ReflectException Cuando ocurre algún error al acceder al target
     *                          mediante reflect.
     */
    @SuppressWarnings("unchecked")
    public static <T, V> V getValue(String fieldName, T target) throws ReflectException {
        if (fieldName == null) {
            return null;
        }
        if (fieldName.contains(".")) {
            String currentField = fieldName.substring(0, fieldName.indexOf('.'));
            String nextField = fieldName.substring(fieldName.indexOf('.') + 1);
            Object nextTarget = getValue(currentField, target);
            if (nextTarget == null) {
                return null;
            }
            return getValue(nextField, nextTarget);
        } else {
            return getValueSimple(fieldName, target);
        }
    }

    /**
     * Establece un valor de forma simple: intenta establecerlo mediante el
     * setter. En caso de no conseguir establecer el valor, intenta hacer un set
     * del field.
     *
     * @param <T>
     * @param <V>
     * @param fieldName Nombre del field.
     * @param target    Objetivo sobre el que se desea establecer el valor.
     * @param value     Valor a establecer.
     * @throws ReflectException cuando ocurre algún error al intentar establecer
     *                          el valor mediante reflect.
     */
    @SuppressWarnings("unchecked")
    public static <T, V> void setValueSimple(String fieldName, T target, V value) throws ReflectException {
        if (fieldName == null || target == null) {
            return;
        }
        boolean setFail = false;
        try {
            Method setter = getSetter(target.getClass(), fieldName);
            setter.invoke(target, value);
        } catch (NoSuchFieldException ex) {
            setFail = true;
        } catch (InvocationTargetException ex) {
            setFail = true;
        } catch (IllegalAccessException ex) {
            setFail = true;
        } catch (NoSuchMethodException ex) {
            setFail = true;
        }
        if (setFail) {
            try {
                Field field = getField(target.getClass(), fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (IllegalArgumentException ex) {
                throw new ReflectException(ex);
            } catch (IllegalAccessException ex) {
                throw new ReflectException(ex);
            } catch (NoSuchFieldException ex) {
                throw new ReflectException(ex);
            }
        }
    }

    /**
     * Establece un valor: intenta hacer un set del field indicado por nombre, y
     * en caso de no conseguir cambiar el valor, intenta establecerlo mediante
     * el setter. Esta es una implementación recursiva que admite profundidad,
     * es decir, el fieldName puede contener el carácter "." para indicar que es
     * un atributo de una subclase dentro de el.
     *
     * @param <T>
     * @param <V>
     * @param fieldName Nombre del atributo, o cadena de atributos.
     * @param target    Objetivo sobre el que se desea establecer el valor.
     * @param value     Valor a establecer.
     * @throws ReflectException
     */
    @SuppressWarnings("unchecked")
    public static <T, V> void setValue(String fieldName, T target, V value) throws ReflectException {
        if (fieldName == null || target == null) {
            return;
        }
        if (fieldName.contains(".")) {
            String currentField = fieldName.substring(0, fieldName.indexOf('.'));
            String nextField = fieldName.substring(fieldName.indexOf('.') + 1);
            Object nextTarget = getValue(currentField, target);
            if (nextTarget != null) {
                setValue(nextField, nextTarget, value);
            }
        } else {
            setValueSimple(fieldName, target, value);
        }
    }

    /**
     * Permite crear una instanacia de la clase que se indica
     *
     * @param <T>   Tipo de la clase que se quiere instanciar
     * @param clazz Tipo de la clase que se quiere instanciar
     * @param args  parametros del constructor
     * @return Objeto instanciado de la clase
     * @throws ReflectException si existe algun error obteniendo la instancia
     */
    public static <T> T createInstance(Class<T> clazz, Object[] args)
            throws ReflectException {
        return (T) createObject(clazz, getListClasses(args), args);
    }

    /**
     * Obtiene el listado de clases de un listado de objetos
     *
     * @param objects listado de objetos
     * @return listado de las clases de los objetos
     */
    private static Class<?>[] getListClasses(Object[] objects) {
        if (!Util.nullObject(objects)) {
            Class<?>[] result = new Class<?>[objects.length];
            int i = 0;
            for (Object o : objects) {
                if (Util.nullObject(o)) {
                    result[i] = Object.class;
                } else {
                    result[i] = o.getClass();
                }
                i++;
            }
            return result;
        }
        return null;
    }

    /**
     * Crea una instancia de la clase
     *
     * @param clazz       Clase que se quiere instanciar
     * @param argsClasses array que contiene los tipos de clase de los
     *                    argumentos del consctructor
     * @param args        argumentos del constrcutor
     * @return una instancia de la clase
     * @throws ReflectException en caso de no tener un constructor que acepte
     *                          los argumentos que se indican
     */
    private static Object createObject(Class<?> clazz, Class<?>[] argsClasses,
                                       Object[] args)
            throws ReflectException {
        try {
            //Obtenemos un constructor con los parametros que le indicamos
            Constructor c = clazz.getConstructor(argsClasses);
            if (Util.nullObject(c)) {
                //Si no existe nos devuelve una exception
                throw new ReflectException();
            } else {
                //Si exite instanciamos la clase con ese constructor
                return c.newInstance(args);
            }
        } catch (NoSuchMethodException ex) {
            throw new ReflectException(ex);
        } catch (SecurityException ex) {
            throw new ReflectException(ex);
        } catch (InstantiationException ex) {
            throw new ReflectException(ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectException(ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectException(ex);
        }
    }

//    /**
//     *
//     * @param <A>
//     * @param target
//     * @param annotationClass
//     * @param values
//     * @throws ReflectException
//     */
//    private static <A extends Annotation> void fillValues(Object target, Class<?> clazz, Class<A> annotationClass, Map<String, Object> values) throws ReflectException {
//        if (clazz == Object.class) {
//            return;
//        }
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(annotationClass)) {
//                values.put(field.getName(), getValue(field, target));
//            }
//        }
//        if (target.getClass().getSuperclass() != null) {
//            fillValues(target, target.getClass().getSuperclass(), annotationClass, values);
//        }
//    }

    /**
     * Metodo que obtiene la lista de valores de todos los atributos anotados.
     *
     * @param <A>
     * @param target          objeto del que extraer los valores.
     * @param annotationClass clase de la anotacion a buscar
     * @return Mapa de valores de los atributos anotados.
     * @throws ReflectException
     */
    public static <A extends Annotation> Map<String, Object> getValues(Object target, Class<A> annotationClass) throws ReflectException {
        Map<String, Object> result = new TreeMap<String, Object>();
        Class<?> actualClazz = target.getClass();
        boolean stop = false;
        while (!stop) {
            Field[] fields = actualClazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.put(field.getName(), getValue(field, target));
                }
            }
            actualClazz = actualClazz.getSuperclass();
            stop = actualClazz == null;
        }
        return result;
    }

    /**
     * Metodo que obtiene una anotacion buscado en la clase indicada y en sus
     * superclases
     *
     * @param <A>             Clase de la anotacion
     * @param clazz           Clase donde se busca la anotacion
     * @param annotationClass Clase de la anotacion
     * @return La anotacion si la encuentra o null si no la encuentra
     */
    public static <A extends Annotation> A getAnnotation(Class<?> clazz,
                                                         Class<A> annotationClass) {
        if (clazz == null || annotationClass == null) {
            return null;
        }
        if (clazz.isAnnotationPresent(annotationClass)) {
            return (A) clazz.getAnnotation(annotationClass);
        } else {
            return clazz.getSuperclass() != null
                    ? getAnnotation(clazz.getSuperclass(), annotationClass) : null;
        }
    }

    /**
     * Metodo que comprueba si una anotacion está presente en la clase indicada
     * o en sus superclases
     *
     * @param <A>             Clase de la anotacion
     * @param clazz           Clase donde se busca la anotacion
     * @param annotationClass Clase de la anotacion
     * @return True si la anotación indicada está presente. False en caso
     *         contrario.
     */
    public static <A extends Annotation> boolean isAnnotationPresent(Class<?> clazz, Class<A> annotationClass) {
        if (clazz == null || annotationClass == null) {
            return false;
        }
        return clazz.isAnnotationPresent(annotationClass)
                ? true
                : clazz.getSuperclass() != null
                ? isAnnotationPresent(clazz.getSuperclass(), annotationClass)
                : false;
    }
}
