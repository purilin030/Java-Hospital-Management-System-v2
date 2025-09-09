package Ass1;

public class Patient extends Person{
	private String disease, sex, adminStatus;
	private int age;
	
	public Patient(String id, String name, String disease, String sex, String adminStatus, int age) {
        super(id, name);
        this.disease = disease;
        this.sex = sex;
        this.adminStatus = adminStatus;
        this.age = age;
    }
	
	public Patient() {
		
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAdminStatus() {
		return adminStatus;
	}

	public void setAdminStatus(String adminStatus) {
		this.adminStatus = adminStatus;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public void showPatientInfo() {
		System.out.println("--- Patient Information ---");
		System.out.println("Patient ID: " + id);
		System.out.println("Name: " + name);
		System.out.println("Disease: " + disease);
		System.out.println("Sex: " + sex);
		System.out.println("Admission Status: " + adminStatus);
		System.out.println("Age: " + age);
		System.out.println("---------------------------");
		System.out.println();
	}

}
