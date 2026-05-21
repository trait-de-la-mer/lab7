package tools;

public class UserManager {
    private BaseConnect baseConnect;
    public UserManager(BaseConnect baseConnect) {
        this.baseConnect = baseConnect;
    }

    public boolean check(User user){
        return baseConnect.checkUser(user);
    }

    public boolean addUser(User user){
        return baseConnect.addUser(user);
    }
}
