package tf.api.model;

public class ModelAndLight {

	private TrafficModel tfModel;
	private TrafficLight tfLight;
	
	public ModelAndLight(TrafficModel tfModel, TrafficLight tfLight) {
		super();
		this.tfModel = tfModel;
		this.tfLight = tfLight;
	}

	public TrafficModel getTfModel() {
		return tfModel;
	}

	public void setTfModel(TrafficModel tfModel) {
		this.tfModel = tfModel;
	}

	public TrafficLight getTfLight() {
		return tfLight;
	}

	public void setTfLight(TrafficLight tfLight) {
		this.tfLight = tfLight;
	}
}
