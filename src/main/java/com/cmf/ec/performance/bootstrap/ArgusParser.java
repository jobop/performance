package com.cmf.ec.performance.bootstrap;

import groovyjarjarcommonscli.PatternOptionBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

public class ArgusParser {
	private final static String BIZ_NAME = "n";
	private final static String ABIDANCE_TIME = "t";
	private final static String THREAD_COUNT = "c";
	private final static String LOG_PATH = "l";

	public static BootstrapArgus parse(String[] args) {

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

	private static BootstrapArgus assembleBootstrapArgus(CommandLine cmd) throws ParseException {
		BootstrapArgus argus = new BootstrapArgus();
		argus.setAbidanceTime((Long) cmd.getParsedOptionValue(ABIDANCE_TIME));
		argus.setThreadCount((Long) cmd.getParsedOptionValue(THREAD_COUNT));
		String bizName = cmd.getOptionValue(BIZ_NAME);
		if (StringUtils.isNotEmpty(bizName)) {
			argus.setBizName(bizName);
		}
		String logPath = cmd.getOptionValue(LOG_PATH);
		if (StringUtils.isNotEmpty(logPath)) {
			argus.setLogPath(logPath);
		}
		return argus;
	}

	private static Options createOptions() {
		Options options = new Options();
		options.addOption(bizNameOption()).addOption(abidanceTimeOption()).addOption(threadCountOption()).addOption(logPathOption());
		return options;
	}

	private static final Option bizNameOption() {
		Option opt = new Option(BIZ_NAME, true, "bizName");
		opt.setType(String.class);
		opt.setRequired(false);
		return opt;
	}

	private static final Option abidanceTimeOption() {
		Option opt = new Option(ABIDANCE_TIME, true, "abidanceTime");
		opt.setType(PatternOptionBuilder.NUMBER_VALUE);
		opt.setRequired(true);
		return opt;
	}

	private static final Option threadCountOption() {
		Option opt = new Option(THREAD_COUNT, true, "threadCount");
		opt.setType(PatternOptionBuilder.NUMBER_VALUE);
		opt.setRequired(true);
		return opt;
	}

	private static final Option logPathOption() {
		Option opt = new Option(LOG_PATH, true, "logPath");
		opt.setType(String.class);
		opt.setRequired(false);
		return opt;
	}

	private static void help() {
		System.out.println("usage: performance [-n bizName]  [-l logPath] -t abidanceMinuteTime -c threadCount");
	}
}
