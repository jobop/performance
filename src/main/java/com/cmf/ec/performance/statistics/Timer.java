package com.cmf.ec.performance.statistics;

/**
 * 计时器,每隔一个时间间隔，触发些东西
 * 
 * @author zhengw
 * 
 */
public class Timer extends Thread {
	private long[] intervals;
	private Caller caller;

	public Timer(Caller caller, long... intervals) {
		this.caller = caller;
		this.intervals = intervals;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		int totalCall = intervals.length;
		int i = 0;
		while (totalCall > i) {
			if ((System.currentTimeMillis() - startTime) >= intervals[i]) {
				final int j = i;
				Thread call = new Thread() {
					public void run() {
						caller.call(j + 1, intervals[j]);
					};
				};
				call.setDaemon(true);
				call.start();
				i++;
				startTime = System.currentTimeMillis();

			}
		}
	}

	public static interface Caller {
		public void call(int i, long interval);
	}

	public static void main(String[] args) {
		Timer timer = new Timer(new Caller() {

			@Override
			public void call(int i, long interval) {
				System.out.println("第" + i + "次触发时间" + System.currentTimeMillis());
			}
		}, new long[] { 10, 20, 30 });

		System.out.println("开始时间" + System.currentTimeMillis());

		timer.start();
	}
}
