package com.github.jobop.performance.spi;

import java.util.Random;

public class TestPerformanceBizSpi implements PerformanceBizSpi {

	@Override
	public boolean execute() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return ((new Random().nextInt(10) % 3) == 0) ? false : true;
	}

}
