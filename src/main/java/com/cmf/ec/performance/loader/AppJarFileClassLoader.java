package com.cmf.ec.performance.loader;

import java.io.File;

public class AppJarFileClassLoader extends JarFileClassLoader {
	private String[] ignorePackagePreffix = new String[] { "com.cmf.ec.performance", "java.", "com.sun", "javax.", "sun.", "sunw." };

	public AppJarFileClassLoader(File[] files) {
		super(files);
	}

	private boolean isIgnoreClass(String name) {
		for (String preffix : ignorePackagePreffix) {
			if (name.startsWith(preffix)) {
				return true;
			}
		}
		return false;
	}

	// XXX:这里打破双亲委托，优先使用用户的jar，但怎么保证系统jar不被覆盖？现在只是简单根据包前缀让框架Spi接口交给父加载器去load而不是自己去load，以避免转型失败的问题
	// 总之spi接口和系统级别的class都不能由AppJarFileClassLoader自己load，否则外围上下文转型时会失败
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> c = findLoadedClass(name);
		ClassNotFoundException ex = null;
		if (c == null) {
			if (isIgnoreClass(name)) {
				try {
					c = getParent().loadClass(name);
				} catch (ClassNotFoundException e) {
					ex = e;
				}
			}
		}

		if (c == null) {
			try {
				c = this.findClass(name);
			} catch (ClassNotFoundException e) {
				ex = e;
			}
		}
		if (c == null) {
			try {
				c = getParent().loadClass(name);
			} catch (ClassNotFoundException e) {
				ex = e;
			}
		}
		if (c == null) {
			throw ex;
		}
		return c;
	}

}
