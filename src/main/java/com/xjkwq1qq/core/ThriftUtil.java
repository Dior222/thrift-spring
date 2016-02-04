package com.xjkwq1qq.core;

import java.lang.reflect.Constructor;

import org.apache.thrift.TProcessor;
import org.springframework.util.ClassUtils;

public class ThriftUtil {
	public static final String PROCESSOR_NAME = "$Processor";
	public static final String IFRC_NAME = "$Iface";

	/**
	 * 
	 * @param service
	 *            thrift��service����
	 * @return service���󹹽���TProcessor
	 * @throws Exception
	 */
	public static TProcessor buildProcessor(Object service) throws Exception {
		// iface�ӿ�
		Class<?> ifaceClass = getThriftServiceIfaceClass(service.getClass());
		if (ifaceClass == null) {
			throw new ThriftRuntimeException("the iface is null");
		}
		// Processor
		Class<TProcessor> processorClass = getThriftServiceProcessorClass(ifaceClass);
		if (processorClass == null) {
			throw new ThriftRuntimeException("the processor is null");
		}
		// constructor
		Constructor<TProcessor> constructor = ClassUtils.getConstructorIfAvailable(processorClass, ifaceClass);
		if (constructor == null) {
			throw new ThriftRuntimeException("the processor constructor is null");
		}
		return constructor.newInstance(service);
	}

	/**
	 * ��ȡ�����Thrift��TProcessor
	 * 
	 * @param value
	 *            ��Ϊ����Ķ���
	 * @return class TProcessor��class
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws ThriftRuntimeException
	 */
	@SuppressWarnings("unchecked")
	private static Class<TProcessor> getThriftServiceProcessorClass(Class<?> ifaceClass) throws SecurityException, ClassNotFoundException,
			ThriftRuntimeException {
		if (ifaceClass == null) {
			return null;
		}
		Class<?> parentClass = getThriftServiceParent(ifaceClass);
		if (parentClass == null) {
			return null;
		}
		return (Class<TProcessor>) getProcessorClass(parentClass);
	}

	/**
	 * ��ȡ���������Processor��Class
	 * 
	 * @param parentClass
	 * @return
	 */
	private static Class<?> getProcessorClass(Class<?> parentClass) {
		Class<?>[] declaredClasses = parentClass.getDeclaredClasses();
		for (Class<?> declaredClasse : declaredClasses) {
			if (declaredClasse.getName().endsWith(PROCESSOR_NAME)) {
				return declaredClasse;
			}
		}
		return null;
	}

	/**
	 * ��ȡ����ʵ�ֶ���ļ��ɽӿ�class
	 * 
	 * @param serviceClass
	 * @return
	 */
	private static Class<?> getThriftServiceIfaceClass(Class<?> serviceClass) {
		Class<?>[] interfaceClasses = serviceClass.getInterfaces();
		for (Class<?> interfaceClass : interfaceClasses) {
			if (interfaceClass.getName().endsWith(IFRC_NAME)) {
				return interfaceClass;
			}
		}
		return null;
	}

	/**
	 * ��ȡthrift���ɵĸ������class
	 * 
	 * @param ifaceClass
	 *            ��ȡ����ʵ�ֶ���ļ��ɽӿ�class
	 * @return ��ȡ����ʵ�����Ӧ��thrift���ɵĸ������class
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	private static Class<?> getThriftServiceParent(Class<?> ifaceClass) throws SecurityException, ClassNotFoundException {
		// ��ȡ����
		String parentClassName = ifaceClass.getName().substring(0, ifaceClass.getName().indexOf(IFRC_NAME));
		return Class.forName(parentClassName);
	}
}
