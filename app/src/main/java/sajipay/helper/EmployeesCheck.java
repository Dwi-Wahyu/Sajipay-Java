package sajipay.helper;

import sajipay.enums.Role;
import sajipay.models.Employee;
import sajipay.models.User;

public class EmployeesCheck {
    public static void CheckEmployeesRole(User user, Role role, String accessControllError) {
        if (!(user instanceof Employee)) {
            throw new SecurityException("Products can only be added by employee");
        }

        Employee employee = (Employee) user;

        if (!employee.role.equals(role)) {
            throw new SecurityException(accessControllError);
        }
    }
}
