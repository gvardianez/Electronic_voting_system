package authorization;

import java.util.List;

public interface AuthorizationService {
    List<User> getUserList();
    User enterAdminByLoginAndPassword(String login, String password);
    User enterUserByLoginAndPassword(String login, String password);
    void changePassword(String login, String oldPassword, String newPassword);
    void createNewUser(String login, String password, String nickname);
    void createNewAdmin(String login, String password, String nickname);
    boolean isAdminRegister();
}
