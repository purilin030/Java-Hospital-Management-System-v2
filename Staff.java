package Ass1;

public class Staff extends Person{
	
	private String designation;
	private String sex;
	private int salary;
	
	public Staff(String id, String name, String designation, String sex, int salary) {
        super(id, name);
        this.designation = designation;
        this.sex = sex;
        this.salary = salary;
    }

	public Staff() {
	
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}
	
	public void showStaffInfo() {
		System.out.println("--- Staff Information ---");
		System.out.println("Staff ID: " + id);
		System.out.println("Name: " + name);
		System.out.println("Designation: " + designation);
		System.out.println("Sex: " + sex);
		System.out.println("Salary: RM" + salary); 
		System.out.println("-------------------------");
		System.out.println();
	}
	
}
