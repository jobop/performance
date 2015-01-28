package com.cmf.ec.performance.parser;

import com.cmf.ec.performance.cli.CommandLine;
import com.cmf.ec.performance.cli.CommandLineParser;
import com.cmf.ec.performance.cli.Option;
import com.cmf.ec.performance.cli.Options;
import com.cmf.ec.performance.cli.ParseException;
import com.cmf.ec.performance.cli.PatternOptionBuilder;
import com.cmf.ec.performance.cli.PosixParser;

public class ArgusParser {

	private final static String BIZ_NAME = "n";
	private final static String ABIDANCE_TIME = "t";
	private final static String THREAD_COUNT = "c";
	private final static String LOG_PATH = "l";

	public PerformanceContext parse(String[] args) {

		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(createOptions(), args);
			return assembleBootstrapArgus(cmd);
		} catch (ParseException e) {
			help();
			System.exit(0);
			return null;
		}

	}

	private PerformanceContext assembleBootstrapArgus(CommandLine cmd) throws ParseException {
		PerformanceContext argus = new PerformanceContext();
		argus.setAbidanceTime(((Long) cmd.getParsedOptionValue(ABIDANCE_TIME)).intValue());
		argus.setThreadCount((Long) cmd.getParsedOptionValue(THREAD_COUNT));
		String bizName = cmd.getOptionValue(BIZ_NAME);
		if (null != bizName && !bizName.equals("")) {
			argus.setBizName(bizName);
		}
		String logPath = cmd.getOptionValue(LOG_PATH);
		if (logPath != null && !logPath.equals("")) {
			argus.setLogPath(logPath);
		}
		return argus;
	}

	private Options createOptions() {
		Options options = new Options();
		options.addOption(bizNameOption()).addOption(abidanceTimeOption()).addOption(threadCountOption()).addOption(logPathOption());
		return options;
	}

	private final Option bizNameOption() {
		Option opt = new Option(BIZ_NAME, true, "bizName");
		opt.setType(String.class);
		opt.setRequired(false);
		return opt;
	}

	private final Option abidanceTimeOption() {
		Option opt = new Option(ABIDANCE_TIME, true, "abidanceTime");
		opt.setType(PatternOptionBuilder.NUMBER_VALUE);
		opt.setRequired(true);
		return opt;
	}

	private final Option threadCountOption() {
		Option opt = new Option(THREAD_COUNT, true, "threadCount");
		opt.setType(PatternOptionBuilder.NUMBER_VALUE);
		opt.setRequired(true);
		return opt;
	}

	private final Option logPathOption() {
		Option opt = new Option(LOG_PATH, true, "logPath");
		opt.setType(String.class);
		opt.setRequired(false);
		return opt;
	}

	private void help() {
		System.out.println("usage: performance [-n bizName]  [-l logPath] -t abidanceMinuteTime -c threadCount");
	}
}
