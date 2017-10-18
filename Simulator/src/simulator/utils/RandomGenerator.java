package simulator.utils;

import javax.swing.JOptionPane;

public class RandomGenerator {
	
	private int milliseconds;
	private int maxMilliseconds;
	
	public RandomGenerator(int milliseconds, int maxMilliseconds) {
		this.milliseconds = milliseconds;
		this.maxMilliseconds = maxMilliseconds;
	}

	public int state(int numberStates) {
		int state = (int) (Math.random() * numberStates);
//		JOptionPane.showMessageDialog(null, state+" state");
		return state;
	}
	
	public int time() {
		int time = ((int) (Math.random() * maxMilliseconds) + 1) * milliseconds;
//		JOptionPane.showMessageDialog(null, time+"ms");
		return time;
	}
	
	public String log(String msg) {
		JOptionPane.showMessageDialog(null, msg);
		return msg;
	}
}
