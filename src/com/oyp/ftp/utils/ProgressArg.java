package com.oyp.ftp.utils;

/**进度参数*/
public class ProgressArg {
	int max, min, value;

	public ProgressArg(int m, int i, int v) {
		max = m;
		min = i;
		value = v;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
