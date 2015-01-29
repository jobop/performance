package com.github.jobop.performance.statistics;

import java.math.BigDecimal;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ¼ÆÊýÆ÷
 * 
 * @author zhengw
 * 
 */
public class Counter {
	private AtomicLong currentCount = new AtomicLong(0);
	private AtomicLong totalCount = new AtomicLong(0);

	private AtomicLong currentSuccessCount = new AtomicLong(0);
	private AtomicLong totalSuccessCount = new AtomicLong(0);

	private AtomicLong currentFailCount = new AtomicLong(0);
	private AtomicLong totalFailCount = new AtomicLong(0);

	private CopyOnWriteArrayList<Long> totleTimeList = new CopyOnWriteArrayList<Long>();
	private CopyOnWriteArrayList<Long> currentTimeList = new CopyOnWriteArrayList<Long>();

	public static void main(String[] args) {
		Counter counter = new Counter();
		counter.addTime(100);
		counter.addTime(200);
		System.out.println(counter.dumpTimeForCurrent());
	}

	public synchronized void addTime(long time) {
		totleTimeList.add(time);
		currentTimeList.add(time);
	}

	public synchronized long dumpTimeForTotal() {
		if (totleTimeList.size() == 0) {
			return 0;
		}
		BigDecimal bd = new BigDecimal(0);
		for (Long time : totleTimeList) {
			bd = bd.add(BigDecimal.valueOf(time));
		}
		bd = bd.divide(BigDecimal.valueOf(totleTimeList.size()), 2, BigDecimal.ROUND_HALF_EVEN);
		return bd.longValue();
	}

	public synchronized long dumpTimeForCurrent() {
		if (currentTimeList.size() == 0) {
			return 0;
		}
		BigDecimal bd = new BigDecimal(0);
		for (Long time : currentTimeList) {
			bd = bd.add(BigDecimal.valueOf(time));
		}
		bd = bd.divide(BigDecimal.valueOf(currentTimeList.size()), 2, BigDecimal.ROUND_HALF_EVEN);
		long avgTime = bd.longValue();
		currentTimeList.clear();
		return avgTime;
	}

	public synchronized void incSuccess() {
		currentSuccessCount.incrementAndGet();
		totalSuccessCount.incrementAndGet();
		currentCount.incrementAndGet();
		totalCount.incrementAndGet();
	}

	public synchronized void incFail() {
		currentFailCount.incrementAndGet();
		totalFailCount.incrementAndGet();
		currentCount.incrementAndGet();
		totalCount.incrementAndGet();
	}

	public synchronized long dumpTotalSuccessCount() {
		return totalSuccessCount.get();
	}

	public synchronized long dumpSuccessChange() {
		return currentSuccessCount.getAndSet(0);
	}

	public synchronized long dumpTotalFailCount() {
		return totalFailCount.get();
	}

	public synchronized long dumpFailChange() {
		return currentFailCount.getAndSet(0);
	}

	public synchronized long dumpTotalCount() {
		return totalCount.get();
	}

	public synchronized long dumpChange() {
		return currentCount.getAndSet(0);
	}
}
