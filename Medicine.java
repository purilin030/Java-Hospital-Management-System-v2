package Ass1;


public class Medicine {
	
	private String name, manufacturer, expiryDate;
	private int cost, count;
	
	public Medicine(String name, String manufacturer, String expiryDate, int cost, int count) {
		this.name = name;
		this.manufacturer = manufacturer;
		this.expiryDate = expiryDate;
		this.cost = cost;
		this.count = count;
	}
	
	public Medicine() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void findMedicine() {
		System.out.println("--- Medicine Details ---");
		System.out.println("Medicine Name: " + name);
		System.out.println("Manufacturer: " + manufacturer);
		System.out.println("Expiry Date: " + expiryDate);
		System.out.println("Cost per unit: RM" + cost); // Assuming cost is in Malaysian Ringgit
		System.out.println("Available Count: " + count);
		System.out.println("------------------------");
		System.out.println();
	}
	
}
