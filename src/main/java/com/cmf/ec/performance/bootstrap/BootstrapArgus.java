package com.cmf.ec.performance.bootstrap;

public class BootstrapArgus {
	private String bizName = "";
	private Long abidanceTime;
	private Long threadCount;
	private String logPath = "";

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}


	public Long getAbidanceTime() {
		return abidanceTime;
	}

	public void setAbidanceTime(Long abidanceTime) {
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
