package com.github.jobop.performance.statistics;

import java.io.PrintStream;

public class Recoder {

	public static PrintStream out = null;
	public static PrintStream err = null;
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				try {
					if (out != null) {
						out.close();
					}
				} catch (Exception e) {
				}
				try {
					if (err != null) {
						err.close();
					}
				} catch (Exception e) {
				}
			}
		});
	}

	public static void setOut(PrintStream out) {
		Recoder.out = out;
	}

	public static void setErr(PrintStream err) {
		Recoder.err = err;
	}

}
