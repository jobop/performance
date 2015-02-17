package com.github.jobop.performance.spi;

public interface PerformanceBizSpi {
	// 返回业务失败还是成功,由提供方决定

	public boolean excute(BizContext context);

}
