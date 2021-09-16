package authorization;

import errors.LoginIsNotAvailableException;
import errors.UserNotFoundException;
import errors.WrongCredentialsException;
import java.util.ArrayList;
import java.util.List;

public class InnerAuthService implements AuthorizationService {

    private final List<User> users;

    public InnerAuthService() {
        this.users = new ArrayList<>();
    }

    @Override
    public User enterUserByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (login.equals(user.getLogin())) {
                if (password.equals(user.getPassword())) return user;
                else throw new WrongCredentialsException("");
            }
        }
       throw new UserNotFoundException("");
    }

    @Override
    public User enterAdminByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (login.equals(user.getLogin())) {
                if (password.equals(user.getPassword())) {
                    if (user.isAdmin()) return user;
                    else throw new WrongCredentialsException("");
                }
            }
        }
        throw new UserNotFoundException("");
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) {
        for (User user : users) {
            if (login.equals(user.getLogin())) {
                if (oldPassword.equals(user.getPassword())) {
                    user.setPassword(newPassword);
                    return;
                } else throw new WrongCredentialsException("");
            }
        }
        throw new UserNotFoundException("");
    }

    @Override
    public List<User> getUserList() {
        return users;
    }

    @Override
    public void createNewUser(String login, String password, String nickname) {
        for (User user : users) {
            if (user.getLogin().equals(login)) throw new LoginIsNotAvailableException("");
        }
        users.add(new User(login, password, nickname, false));
    }

    @Override
    public void createNewAdmin(String login, String password, String nickname) {
        User admin = new User(login, password, nickname,true);
        users.add(admin);
    }

    public boolean isAdminRegister() {
        for (User user : users) {
            if (user.isAdmin()) {
                return true;
            }
        }
        return false;
    }

}