package Ass1;

public class Doctor extends Person{
	
	private String specialist, worktime, qualification;
	private int room;
	
	 public Doctor(String id, String name, String specialist, String worktime, String qualification, int room) {
	        super(id, name);
	        this.specialist = specialist;
	        this.worktime = worktime;
	        this.qualification = qualification;
	        this.room = room;
	    }
	
	public Doctor() {
		
	}

	public String getSpecialist() {
		return specialist;
	}

	public void setSpecialist(String specialist) {
		this.specialist = specialist;
	}

	public String getWorktime() {
		return worktime;
	}

	public void setWorktime(String worktime) {
		this.worktime = worktime;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}
	
	public void showDoctorInfo() {
		System.out.println("--- Doctor Information ---");
		System.out.println("Doctor's ID: " + id);
		System.out.println("Doctor's Name: " + name);
		System.out.println("Specialist: " + specialist);
		System.out.println("Work Time: " + worktime);
		System.out.println("Qualification: " + qualification);
		System.out.println("Room Number: " + room);
		System.out.println("--------------------------");
		System.out.println();
	}

}
