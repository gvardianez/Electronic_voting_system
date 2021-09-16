package authorization;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final String login;
    private String password;
    private final String name;
    private final boolean isAdmin;
    private final Map<String, Candidate> participationInElections;

    public User(String login, String password, String name, boolean isAdmin) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
        participationInElections = new HashMap<>();
    }

    public Map<String, Candidate> getParticipationInElections() {
        return participationInElections;
    }

    public void vote(Candidate candidate) {
        candidate.addVoice();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getLogin() {
        return login;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
