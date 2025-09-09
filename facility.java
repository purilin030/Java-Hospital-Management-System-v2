package Ass1;


public class facility {
	
	private String facility;
	
	public facility(String facility) {
		this.facility = facility;
	}
	
	public facility() {
		
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}
	
	public void showFacility() {
		System.out.println("--- Facility Information ---");
		System.out.println("Facility Name: " + facility);
		System.out.println("----------------------------");
		System.out.println();
	}

}
