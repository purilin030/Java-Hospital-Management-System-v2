package Ass1;


public class Lab {
	
	private String lab;
	private int cost;
	
	public Lab(String lab, int cost) {
		this.lab = lab;
		this.cost = cost;
	}
	
	public Lab() {
		
	}

	public String getLab() {
		return lab;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public void labList() {
		System.out.println("--- Lab Information ---");
		System.out.println("Lab Name: " + lab);
		System.out.println("Cost: RM" + cost); // Assuming cost is in Malaysian Ringgit
		System.out.println("-----------------------");
		System.out.println();
	}
	
}
