package com.cmf.ec.performance.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.cmf.ec.performance.spi.PerformanceBizSpi;

public class PrerformanceBizSpiLoaderUtil {
	public static List<PerformanceBizSpi> loadSpiFromDir(File dir) {
		List<PerformanceBizSpi> spis = new ArrayList<PerformanceBizSpi>();
		File[] jarFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".jar");
			}
		});
		File[] spiFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith("spi.lst");
			}
		});
		if (spiFiles == null || spiFiles.length == 0) {
			return spis;
		}

		JarFileClassLoader classLoader = new AppJarFileClassLoader(jarFiles);
		List<String> spiListLine = null;
		try {
			spiListLine = readFileForLines(spiFiles[0]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (spiListLine == null || spiListLine.size() == 0) {
			return spis;
		}
		for (String spiName : spiListLine) {
			PerformanceBizSpi spi = null;
			try {
				Class<?> clazz = classLoader.loadClass(spiName);
				Object obj = clazz.newInstance();
				spi = (PerformanceBizSpi) obj;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("can not load spi:" + spiName + " from dir" + dir.getAbsolutePath());
			}
			if (null != spi) {
				spis.add(spi);
			}
		}

		return spis;
	}

	private static List<String> readFileForLines(File file) throws Exception {
		List<String> lines = new ArrayList<String>();
		if (file == null || !file.exists()) {
			return lines;
		}
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return lines;
	}

}
