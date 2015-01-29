package com.github.jobop.performance.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class JarFileClassLoader extends URLClassLoader {

	public JarFileClassLoader(File[] files) {
		super(files2Urls(files), (Thread.currentThread().getContextClassLoader() != null ? Thread.currentThread().getContextClassLoader()
				: (JarFileClassLoader.class.getClassLoader() != null ? JarFileClassLoader.class.getClassLoader() : ClassLoader.getSystemClassLoader())));

	}

	private static URL[] files2Urls(File[] files) {
		List<URL> urlList = new ArrayList<URL>();
		for (int i = 0; i < files.length; i++) {
			try {
				if (!files[i].getName().endsWith(".jar")) {
					continue;
				}
				urlList.add(files[i].toURI().toURL());
				System.out.println("加载JAR包：" + files[i].getAbsolutePath());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		URL[] urls = new URL[urlList.size()];
		return urlList.toArray(urls);

	}

	public final void addJarFile(File file) {
		try {
			this.addURL(file.toURI().toURL());
			System.out.println("加载JAR包：" + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
