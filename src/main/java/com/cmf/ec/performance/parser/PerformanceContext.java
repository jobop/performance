package com.cmf.ec.performance.parser;

public class PerformanceContext {
	private String bizName = "";
	private int abidanceTime;
	private Long threadCount;
	private String logPath = "";

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public int getAbidanceTime() {
		return abidanceTime;
	}

	public void setAbidanceTime(int abidanceTime) {
		this.abidanceTime = abidanceTime;
	}

	public Long getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(Long threadCount) {
		this.threadCount = threadCount;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

}
