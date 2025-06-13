package sajipay.models;

import sajipay.enums.Role;

public class Employee extends User {
    public Role role;
    public int yearsOfExperience;
    public double salary;

    public Employee(String username, String password) {
        super(username, password);
    }

    public Employee(String username, String password, Role role, int yearsOfExperience, double salary) {
        this(username, password);
        this.role = role;
        this.yearsOfExperience = yearsOfExperience;
        this.salary = salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

}
