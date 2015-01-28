package com.cmf.ec.performance.task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cmf.ec.performance.spi.PerformanceBizSpi;
import com.cmf.ec.performance.spi.TestPerformanceBizSpi;
import com.cmf.ec.performance.spi.TestPerformanceBizSpi2;
import com.cmf.ec.performance.statistics.Counter;
import com.cmf.ec.performance.statistics.Recoder;
import com.cmf.ec.performance.statistics.Timer;
import com.cmf.ec.performance.statistics.Timer.Caller;

public class PerformanceTask {
	private String bizName = "";
	private int abidanceTime = 0;
	private Long threadCount = 0L;
	private String logPath = "/usr/local/log/performance_recode.log";
	// 此处装载要测试的业务
	private List<PerformanceBizSpi> spiList = new ArrayList<PerformanceBizSpi>();

	public PerformanceTask addTest(PerformanceBizSpi spi) {
		this.spiList.add(spi);
		return this;
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
							success = spi.execute();
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
			public void call(int i, long interval) {
				try {
					DecimalFormat df = new DecimalFormat("######0.00");
					double activeCount = counter.dumpChange();
					double activeSuccessCount = counter.dumpSuccessChange();
					double totalCount = counter.dumpTotalCount();
					double totalSuccessCount = counter.dumpTotalSuccessCount();
					Recoder.out.println("");
					Recoder.out.println("接口 " + spi.getClass().getName() + " 第" + i + "次输出,距离上次时间间隔为:" + interval + "ms");
					Recoder.out.println("现在时间为:" + System.currentTimeMillis());
					Recoder.out.println("本间隔执行次数为:" + df.format(activeCount));
					Recoder.out.println("总执行次数为:" + df.format(totalCount));
					Recoder.out.println("最近一分钟平均耗时为:" + df.format(counter.dumpTimeForCurrent()) + "ms");
					Recoder.out.println("最近一分钟平均tps为:" + df.format(activeCount / (interval / 1000)));
					if (activeCount == 0) {
						Recoder.out.println("最近一分钟成功率为:0.00%");
					} else {
						Recoder.out.println("最近一分钟成功率为:" + df.format((activeSuccessCount / activeCount) * 100) + "%");
					}
					Recoder.out.println("总平均耗时为:" + df.format(counter.dumpTimeForTotal()) + "ms");
					Recoder.out.println("总平均tps为:" + df.format(totalCount / (((i * interval)) / 1000)));
					if (totalCount == 0) {
						Recoder.out.println("总成功率为:0.00%");
					} else {
						Recoder.out.println("总成功率为:" + df.format((totalSuccessCount / totalCount) * 100) + "%");
					}
					if (i == intervals.length) {
						Recoder.out.println("****************************************************************************");
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

	public static void main(String[] args) {
		new PerformanceTask().t(2).c(50l).l("D:\\recode.log").addTest(new TestPerformanceBizSpi()).addTest(new TestPerformanceBizSpi2()).start();

	}
}
