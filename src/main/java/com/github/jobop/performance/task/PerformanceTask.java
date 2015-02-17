package com.github.jobop.performance.task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.jobop.performance.spi.BizContext;
import com.github.jobop.performance.spi.PerformanceBizSpi;
import com.github.jobop.performance.spi.TestPerformanceBizSpi;
import com.github.jobop.performance.spi.TestPerformanceBizSpi2;
import com.github.jobop.performance.statistics.Counter;
import com.github.jobop.performance.statistics.Monitor;
import com.github.jobop.performance.statistics.Recoder;
import com.github.jobop.performance.statistics.Timer;
import com.github.jobop.performance.statistics.Timer.Caller;
import com.github.jobop.performance.statistics.impl.DefaultMonitor;

public class PerformanceTask {
	private String bizName = "";
	private int abidanceTime = 0;
	private Long threadCount = 0L;
	private String logPath = "/usr/local/log/performance_recode.log";
	// 此处装载要测试的业务
	private List<PerformanceBizSpi> spiList = new ArrayList<PerformanceBizSpi>();
	private List<Monitor> monitors = new ArrayList<Monitor>();
	/**
	 * 执行上下文
	 */
	private BizContext bizContext = new BizContext();

	public PerformanceTask addTest(PerformanceBizSpi spi) {
		this.spiList.add(spi);
		return this;
	}

	public void addContext(String key, Object value) {
		bizContext.put(key, value);
	}

	public void start() {

		System.out.println("开始接口测试。。");
		initRecoder();
		// 找出要执行的spi
		List<PerformanceBizSpi> needExcuteSpi = findNeedExcuteSpi(this.getBizName());
		for (PerformanceBizSpi spi : needExcuteSpi) {
			doExcute(spi);
		}
		System.out.println("接口测试结束，详细结果请查看记录日志。。");
	}

	private void initRecoder() {
		try {
			Recoder.setOut(new PrintStream(new FileOutputStream(logPath)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void doExcute(final PerformanceBizSpi spi) {
		final Counter counter = new Counter();
		final CountDownLatch startCountDownLatch = new CountDownLatch(1);
		final CountDownLatch endCountDownLatch = new CountDownLatch(abidanceTime);
		ExecutorService exec = Executors.newFixedThreadPool(threadCount.intValue());
		for (int i = 0; i < threadCount; i++) {
			exec.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					startCountDownLatch.await();
					while (!Thread.interrupted()) {
						long startTime = System.currentTimeMillis();
						boolean success = false;
						try {
							success = spi.excute(bizContext);
						} catch (Throwable e) {
							e.printStackTrace();
							success = false;
						}
						counter.addTime(System.currentTimeMillis() - startTime);
						if (success) {
							counter.incSuccess();
						} else {
							counter.incFail();
						}
					}
					return true;
				}

			});

		}
		adjudge(spi, counter, startCountDownLatch, endCountDownLatch);
		waitEnd(endCountDownLatch);
		clearThread(spi, exec);

	}

	private void waitEnd(final CountDownLatch endCountDownLatch) {
		try {
			endCountDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void clearThread(final PerformanceBizSpi spi, ExecutorService exec) {

		// 一般的做法应该是注释的代码，但这里测试线程是永远不会空闲的，所以shutdown不会给他们发信号量，这里直接shutdownNow来发信号量
		if (null != exec) {
			exec.shutdownNow();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("接口 " + spi.getClass().getName() + "线程清洗完毕");
		}
		// if (null != exec) {
		// exec.shutdown();
		// try {
		// exec.awaitTermination(5000, TimeUnit.MILLISECONDS);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// exec.shutdownNow();
		// }
		// }
	}

	private void adjudge(final PerformanceBizSpi spi, final Counter counter, final CountDownLatch startCountDownLatch, final CountDownLatch endCountDownLatch) {
		// 每分钟记录一次
		final long[] intervals = new long[abidanceTime];
		for (int i = 0; i < intervals.length; i++) {
			intervals[i] = 60 * 1000;
		}

		Timer timer = new Timer(new Caller() {

			@Override
			public void call(int totalCall, int i, long interval) {
				try {
					for (Monitor m : monitors) {
						m.monitor(spi, counter, totalCall, i, interval);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				endCountDownLatch.countDown();
			}

		}, intervals);
		timer.setDaemon(true);
		timer.start();
		startCountDownLatch.countDown();
	}

	private List<PerformanceBizSpi> findNeedExcuteSpi(String bizName) {
		List<PerformanceBizSpi> needExcuteSpi = new ArrayList<PerformanceBizSpi>();
		if (bizName == null || bizName.equals("")) {
			needExcuteSpi = spiList;
		} else {
			for (PerformanceBizSpi spi : spiList) {
				if (spi.getClass().getName().equals(bizName)) {
					needExcuteSpi.add(spi);
				}
			}
		}
		return needExcuteSpi;
	}

	public String getBizName() {
		return bizName;
	}

	public int getAbidanceTime() {
		return abidanceTime;
	}

	public Long getThreadCount() {
		return threadCount;
	}

	public String getLogPath() {
		return logPath;
	}

	public List<PerformanceBizSpi> getSpiList() {
		return spiList;
	}

	// XXX:这种和命令行保持一致的api是否合适？
	public PerformanceTask n(String bizName) {
		this.bizName = bizName;
		return this;
	}

	public PerformanceTask t(int abidanceTime) {
		this.abidanceTime = abidanceTime;
		return this;
	}

	public PerformanceTask c(Long threadCount) {
		this.threadCount = threadCount;
		return this;
	}

	public PerformanceTask l(String logPath) {
		this.logPath = logPath;
		return this;
	}

	public PerformanceTask registMonitor(Monitor m) {
		this.monitors.add(m);
		return this;
	}

	public static void main(String[] args) {
		new PerformanceTask().t(2).c(50l).l("D:\\recode.log").addTest(new TestPerformanceBizSpi()).addTest(new TestPerformanceBizSpi2()).registMonitor(new DefaultMonitor()).start();

	}
}
