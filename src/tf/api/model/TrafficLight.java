package tf.api.model;

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
	
	/**
     * Copy constructor.
     * @param light Make a copy of this.
     */
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
	 * 
	 * @deprecated Please use nextSignal() instead.
	 */
	@Deprecated
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
	 * Obtains the traffic light of the next signal.
	 * There are only four states, described as follows.
	 * (signalWE, signalNS):
	 * (GREEN, RED)		-->
	 * (AMBER, RED)		-->
	 * (RED, GREEN)		-->
	 * (RED, AMBER)		-->
	 * (GREEN, RED)		loop
	 * 
	 * @return the next signal of the traffic light
	 */
	public TrafficLight nextSignal() {

		if(signalWE.equals(Signal.GREEN)) {
			return new TrafficLight(Signal.AMBER, Signal.RED);
		}
		else if(signalWE.equals(Signal.AMBER)) {
			return new TrafficLight(Signal.RED, Signal.GREEN);
		}
		else if(signalWE.equals(Signal.RED)) {
			if(signalNS.equals(Signal.AMBER)) {
				return new TrafficLight(Signal.GREEN, Signal.RED);
			}
			else {
				return new TrafficLight(Signal.RED, Signal.AMBER);
			}
		}
		return null;
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

	
	/**
	 * Obtains an integer representation of the traffic light.
	 * (signalWE, signalNS): Value
	 * (GREEN, RED): 0
	 * (AMBER, RED): 1
	 * (RED, GREEN): 2
	 * (RED, AMBER): 3
	 * 
	 * @return the integer value
	 */
	public int intValue() {
		int value;
		if( signalWE.equals(Signal.GREEN) ) {
			value = 0;
		}
		else if( signalWE.equals(Signal.AMBER) ) {
			value = 1;
		}
		else {
			if( signalNS.equals(Signal.GREEN) ) {
				value = 2;
			}
			else {
				value = 3;
			}
		}
		return value;
	}

}
