package com.github.jobop.performance.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.github.jobop.performance.loader.PrerformanceBizSpiLoaderUtil;
import com.github.jobop.performance.parser.ArgusParser;
import com.github.jobop.performance.parser.PerformanceContext;
import com.github.jobop.performance.spi.PerformanceBizSpi;
import com.github.jobop.performance.task.PerformanceTask;

public class Bootstrap {
	private String baseSpiPath = "/usr/local/webapp/performance";

	//
	public void run(String[] args) {

		// 解析命令行 压测接口(不指定则按顺序全压)n 压测时长t 线程数c 报告地址l
		PerformanceContext context = new ArgusParser().parse(args);
		System.out.println("接口测试参数设置为 \n\r测试持续时间：" + context.getAbidanceTime() + "\n\r" + "指定测试业务(空为全部)：" + context.getBizName() + "\n\r" + "日志输出路径：" + context.getLogPath() + "\n\r" + "worker线程数："
				+ context.getThreadCount() + "\n\r");

		PerformanceTask task = new PerformanceTask();
		task.t(context.getAbidanceTime()).n(context.getBizName()).c(context.getThreadCount()).l(context.getLogPath());
		defindLibPath();
		List<PerformanceBizSpi> spiList = loadSpiList();
		// 把可用于测试的spi都添加到任务中
		for (PerformanceBizSpi spi : spiList) {
			task.addTest(spi);
		}
		task.start();
	}

	private void defindLibPath() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(System.getProperty("appHome") + "/" + "config.properties")));
		} catch (IOException e) {
			System.out.println("配置文件不存在：" + System.getProperty("appHome") + "/" + "config.properties");
		}
		baseSpiPath = prop.getProperty("LIB_BASE_PATH");
	}

	private List<PerformanceBizSpi> loadSpiList() {
		List<PerformanceBizSpi> spiList = new ArrayList<PerformanceBizSpi>();
		File baseDir = new File(baseSpiPath);
		if (null == baseDir || !baseDir.exists()) {
			System.out.println("baseDir 不存在！baseSpiPath=" + baseSpiPath);
			return spiList;
		}
		File[] subDirs = baseDir.listFiles();
		spiList = PrerformanceBizSpiLoaderUtil.loadSpiFromDir(baseDir);
		for (File subDir : subDirs) {
			if (!subDir.isDirectory()) {
				continue;
			}
			spiList.addAll(PrerformanceBizSpiLoaderUtil.loadSpiFromDir(subDir));
		}
		return spiList;
	}

}
