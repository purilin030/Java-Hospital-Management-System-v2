package Ass1;

public class Initialization {
    public Doctor[] doctors = new Doctor[25];
    public Patient[] patients = new Patient[100];
    public Staff[] staff = new Staff[100];
    public Medicine[] medicines = new Medicine[100];
    public Lab[] labs = new Lab[20];
    public facility[] facilities = new facility[20];

    public int docCount = 3, patCount = 3, staffCount = 3, medCount = 3, labCount = 3, facCount = 3;

    public Initialization() {
        // Initialize Doctors
        doctors[0] = new Doctor("D251", "Young Luo Siong", "Cardiologist", "9AM-5PM", "MBBS", 101);
        doctors[1] = new Doctor("D292", "Wong Jiun Hong", "Neurologist", "9AM-5PM", "MD", 102);
        doctors[2] = new Doctor("D011", "Eric Chong", "Dermatologist", "8AM-4PM", "MBBS", 103);

        // Initialize Patients
        patients[0] = new Patient("P001", "Ali", "Fever", "Male", "Admitted", 25);
        patients[1] = new Patient("P002", "Aminah", "Cold", "Female", "Discharged", 30);
        patients[2] = new Patient("P003", "John", "Injury", "Male", "Admitted", 35);

        // Initialize Staff
        staff[0] = new Staff("S001", "Nina", "Nurse", "Female", 3000);
        staff[1] = new Staff("S002", "Sam", "Security", "Male", 4000);
        staff[2] = new Staff("S003", "Linda", "Pharmacist", "Female", 5000);

        // Initialize Medicines
        medicines[0] = new Medicine("Paracetamol", "PharmaCorp", "2026-01-11", 2, 50);
        medicines[1] = new Medicine("Ibuprofen", "MediLab", "2025-12-01", 3, 60);
        medicines[2] = new Medicine("Aspirin", "HealthPlus", "2026-01-15", 1, 100);

        // Initialize Labs
        labs[0] = new Lab("Blood Test", 5000);
        labs[1] = new Lab("X-Ray", 500);
        labs[2] = new Lab("MRI", 3000);

        // Initialize Facilities
        facilities[0] = new facility("General Ward");
        facilities[1] = new facility("ICU");
        facilities[2] = new facility("Operation Theater");
    }
}

