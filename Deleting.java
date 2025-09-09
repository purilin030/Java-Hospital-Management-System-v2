package Ass1;

public class Deleting {

    public static boolean deleteDoctorById(Doctor[] doctors, int[] count, String id) {
        for (int i = 0; i < count[0]; i++) {
            if (doctors[i].getId().equalsIgnoreCase(id)) {
                shiftLeft(doctors, i, count);
                return true;
            }
        }
        return false;
    }

    public static boolean deletePatientById(Patient[] patients, int[] count, String id) {
        for (int i = 0; i < count[0]; i++) {
            if (patients[i].getId().equalsIgnoreCase(id)) {
                shiftLeft(patients, i, count);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteStaffById(Staff[] staff, int[] count, String id) {
        for (int i = 0; i < count[0]; i++) {
            if (staff[i].getId().equalsIgnoreCase(id)) {
                shiftLeft(staff, i, count);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteMedicineByName(Medicine[] medicines, int[] count, String name) {
        for (int i = 0; i < count[0]; i++) {
            if (medicines[i].getName().equalsIgnoreCase(name)) {
                shiftLeft(medicines, i, count);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteLabByName(Lab[] labs, int[] count, String name) {
        for (int i = 0; i < count[0]; i++) {
            if (labs[i].getLab().equalsIgnoreCase(name)) {
                shiftLeft(labs, i, count);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteFacilityByName(facility[] facilities, int[] count, String name) {
        for (int i = 0; i < count[0]; i++) {
            if (facilities[i].getFacility().equalsIgnoreCase(name)) {
                shiftLeft(facilities, i, count);
                return true;
            }
        }
        return false;
    }

    // Generic shift left for array
    private static <T> void shiftLeft(T[] array, int index, int[] count) {
        for (int i = index; i < count[0] - 1; i++) {
            array[i] = array[i + 1];
        }
        array[count[0] - 1] = null;
        count[0]--;
    }
}
