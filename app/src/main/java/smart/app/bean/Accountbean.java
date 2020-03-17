package smart.app.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Accountbean implements Serializable {

    public int status;
    public String time;
    public data data;

    public class data implements Serializable{
        public String UserName;
        public String Email;
        public String FirstName;
        public String LastName;
        public String NickName;
        public String RegisterDate;
        public String LastLoginDate;
        public String Company;
        public String Department;
        public ArrayList<info> Authorizes;

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

        public String getFirstName() {
            return FirstName;
        }

        public void setFirstName(String firstName) {
            FirstName = firstName;
        }

        public String getLastName() {
            return LastName;
        }

        public void setLastName(String lastName) {
            LastName = lastName;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getRegisterDate() {
            return RegisterDate;
        }

        public void setRegisterDate(String registerDate) {
            RegisterDate = registerDate;
        }

        public String getLastLoginDate() {
            return LastLoginDate;
        }

        public void setLastLoginDate(String lastLoginDate) {
            LastLoginDate = lastLoginDate;
        }

        public String getCompany() {
            return Company;
        }

        public void setCompany(String company) {
            Company = company;
        }

        public String getDepartment() {
            return Department;
        }

        public void setDepartment(String department) {
            Department = department;
        }

        public class info implements Serializable{
            public String Id;
            public String Name;
        }
    }

}
