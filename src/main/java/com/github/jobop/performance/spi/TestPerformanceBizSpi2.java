package com.github.jobop.performance.spi;

import java.util.Random;

public class TestPerformanceBizSpi2 implements PerformanceBizSpi {

	@Override
	public boolean excute(BizContext context) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return ((new Random().nextInt(10) % 2) == 0) ? false : true;
	}

}
