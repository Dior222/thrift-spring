package com.xjkwq1qq.core;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.xjkwq1qq.annotation.ThriftService;

public class ThriftServiceApplication {
	public static final Logger LOG = LoggerFactory.getLogger(ThriftServiceApplication.class);

	private ApplicationContext applicationContext;
	public static final String DEFAULT_CONFIGSOURCE = "/thrift-spring.properties";
	private String configSource;

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void init() throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ɨ��thrift���");
		}
		Map<String, Object> thriftServices = applicationContext.getBeansWithAnnotation(ThriftService.class);
		if (thriftServices == null) {
			return;
		}

		int port = getPort();
		TServerTransport serverTransport = new TServerSocket(port);
		TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
		TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(multiplexedProcessor));
		for (Map.Entry<String, Object> entry : thriftServices.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();

			// ��ȡ����
			ThriftService thriftService = value.getClass().getAnnotation(ThriftService.class);
			if (StringUtils.isNoneBlank(thriftService.value())) {
				name = thriftService.value();
			}
			// ��ȡprocessor
			TProcessor processor = ThriftUtil.buildProcessor(value);
			// ע��
			if (LOG.isDebugEnabled()) {
				LOG.debug("ע����������" + name + "," + processor.getClass());
			}
			multiplexedProcessor.registerProcessor(name, processor);
		}

		// ����
		if (LOG.isDebugEnabled()) {
			LOG.debug("�������񣬼����˿ڣ�" + port);
		}
		server.serve();
	}

	/**
	 * ��ȡthrift�����˿�
	 * 
	 * @return thrift�����˿�
	 * @throws IOException
	 */
	private int getPort() throws IOException {
		Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(getConfigSource()));
		int port = 9090;
		try {
			String thriftPort = properties.getProperty("thrift.port");
			if (StringUtils.isNotBlank(thriftPort)) {
				port = Integer.valueOf(thriftPort);
			}
		} catch (Exception e) {

		}
		return port;
	}

	private String getConfigSource() {
		if (StringUtils.isBlank(configSource)) {
			configSource = DEFAULT_CONFIGSOURCE;
		}
		return configSource;
	}

	public void setConfigSource(String configSource) {
		this.configSource = configSource;
	}

}
