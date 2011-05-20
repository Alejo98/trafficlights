package tf.api;

/**
 * Represents a traffic light
 */
public class TrafficLight {
	
	/**
	 * the current signal of the traffic light in West-East direction
	 */
	private Signal signalWE;
	/**
	 * the current signal of the traffic light in North-South direction
	 */
	private Signal signalNS;
	
	/**
	 * A new traffic light. 
	 * The initial signal of West-East is GREEN, while that of North-South is RED.
	 */
	public TrafficLight() {
		super();
		this.signalWE = Signal.GREEN;
		this.signalNS = Signal.RED;
	}
	
	/**
	 * A new traffic light
	 * 
	 * @param sgnWE	the initial signal in West-East direction
	 * @param sgnNS	the initial signal in North-South direction
	 */
	public TrafficLight(Signal sgnWE, Signal sgnNS) {

		super();
		
		if(isValidSignal(sgnWE, sgnNS)==false) {
			throw new IllegalArgumentException();
		}
		this.signalWE = sgnWE;
		this.signalNS = sgnNS;
	}
	
	
	public TrafficLight(TrafficLight light) {

		super();
		this.signalWE = light.signalWE;
		this.signalNS = light.signalNS;
	}
	
	
	
	private boolean isValidSignal(Signal sgn1, Signal sgn2) {
		
		if(sgn1.equals(Signal.GREEN) && 
				(sgn2.equals(Signal.AMBER) || sgn2.equals(Signal.GREEN)) ) {
			return false;
		}
		if(sgn2.equals(Signal.GREEN) && 
				(sgn1.equals(Signal.AMBER) || sgn1.equals(Signal.GREEN)) ) {
			return false;
		}
		return true;
	}

	
	/**
	 * Switch the traffic light to the next signal.
	 * There are only four states, described as follows.
	 * (signalWE, signalNS):
	 * (GREEN, RED)		-->
	 * (AMBER, RED)		-->
	 * (RED, GREEN)		-->
	 * (RED, AMBER)		-->
	 * (GREEN, RED)		loop
	 */
	public void switches() {
		
		if(signalWE.equals(Signal.GREEN)) {
			signalWE = Signal.AMBER;
			signalNS = Signal.RED;
		}
		else if(signalWE.equals(Signal.AMBER)) {
			signalWE = Signal.RED;
			signalNS = Signal.GREEN;
		}
		else if(signalWE.equals(Signal.RED)) {
			if(signalNS.equals(Signal.AMBER)) {
				signalWE = Signal.GREEN;
				signalNS = Signal.RED;
			}
			else {
				signalWE = Signal.RED;
				signalNS = Signal.AMBER;
			}
		}
	}

	/**
	 * Obtains the traffic light signal in the given direction
	 * 
	 * @param dir	the direction of the road which the signal affects
	 * @return	the current signal of the traffic light
	 */
	public Signal getSignal(Direction dir) {
		if(dir.equals(Direction.EAST_WEST) || 
				dir.equals(Direction.WEST_EAST) ) {
			return signalWE;
		}
		else {
			return signalNS;
		}
	}


}
