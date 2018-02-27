package com.xyy.erp.platform.common.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 服务层辅助类
 *
 * @author caofei
 */
@SuppressWarnings("rawtypes")
public class ServiceUtil {

    private static Logger LOGGER = Logger.getLogger(ServiceUtil.class);

    /**
     * 比较更新前后对象的值，将需要更新的属性赋予数据库保存的对象
     *
     * @param inObject 需要更新的对象
     * @param outObject 数据库保存的对象
     * @author caofei
     */
    public static Object getUpdateObject(Object inObject, Object outObject) {

        if (!inObject.getClass().isAssignableFrom(outObject.getClass())
                && !outObject.getClass().isAssignableFrom(inObject.getClass())) // if (!inObject.getClass().equals(outObject.getClass()))
        {
            throw new IllegalArgumentException("对象类型不匹配");
        }

        for (Class<?> claz = inObject.getClass(); claz != Object.class; claz = claz
                .getSuperclass()) {
            claz.getDeclaredFields();

            Field[] fields = claz.getDeclaredFields();

            String fieldName = null;
            String methodName = null;

            try {
                for (int i = 0; i < fields.length; i++) {

                    fieldName = fields[i].getName();
                    if (fieldName.equals("serialVersionUID")) {
                        continue;
                    }

                    if (Modifier.isStatic(fields[i].getModifiers())) {
                        continue;
                    }

                    methodName = "get"
                            + fieldName.substring(0, 1).toUpperCase()
                            + fieldName.substring(1);
                    Method method = inObject.getClass().getMethod(methodName);
                    // Class<?> type = fields[i].getType();

                    Object inValue = method.invoke(inObject);
                    Object outValue = method.invoke(outObject);

                    if ((null != inValue && !inValue.equals(outValue)) || (fields[i].getType() == List.class)) {
                        methodName = "set" + methodName.substring(3);
                        Method setMethod = inObject.getClass().getMethod(methodName, fields[i].getType());
                        setMethod.invoke(outObject, inValue);
                    }
                    
                }
            } catch (Exception e) {

                LOGGER.error(e.getMessage(), e);
            }
        }
        return outObject;
    }

    /**
     * 对象深拷贝
     *
     * @param <T>
     * @param oldObject
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(Object oldObject) {

        Object object = null;

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(oldObject);
            out.flush();
            out.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos
                    .toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            object = in.readObject();
            in.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return (T) object;
    }

    /**
     * 两个对象间赋值
     *
     * @param <T>
     * @param TargetBean
     * @param SourceBean
     * @return
     * @throws Exception
     */
    public static <T> T copyBean(T TargetBean, T SourceBean) throws Exception {
        if (TargetBean == null) {
            return null;
        }
        Field[] tFields = TargetBean.getClass().getDeclaredFields();
        Field[] sFields = SourceBean.getClass().getDeclaredFields();
        try {
            for (Field field : tFields) {
                String fieldName = field.getName();
                if (fieldName.equals("serialVersionUID")) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.getType() == Map.class) {
                    continue;
                }
                if (field.getType() == Set.class) {
                    continue;
                }
                if (field.getType() == List.class) {
                    continue;
                }
                for (Field sField : sFields) {
                    if (!sField.getName().equals(fieldName)) {
                        continue;
                    }
					Class type = field.getType();
                    String setName = "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(1);
                    Method tMethod = TargetBean.getClass().getMethod(setName, new Class[]{type});
                    String getName = "get" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(1);
                    Method sMethod = SourceBean.getClass().getMethod(getName, (Class[]) null);
                    Object setterValue = sMethod.invoke(SourceBean, (Object[]) null);
                    tMethod.invoke(TargetBean, new Object[]{setterValue});
                }
            }
        } catch (Exception e) {
            throw new Exception("设置参数信息发生异常", e);
        }
        return TargetBean;
    }

}
