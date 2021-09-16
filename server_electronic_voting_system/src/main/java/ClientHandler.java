import authorization.AuthorizationService;
import authorization.Candidate;
import authorization.User;
import errors.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

public class ClientHandler {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private User currentUser;
    private final char symbol = 10000;
    private AuthorizationService authService;
    private VotingSystem votingSystem;
    private String title;
    private ChatServer server;

    public ClientHandler(Socket socket, ChatServer server) {
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.authService = server.getAuthService();
            this.votingSystem = server.getVotingSystem();
            this.server = server;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launch() {
        Thread handlerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
                try {
                    String message = in.readUTF();
                    String[] parseMessageArray = message.split("" + symbol);
                    String parseMessage = parseMessageArray[0];
                    System.out.println(Arrays.toString(parseMessageArray));
                    switch (parseMessage) {
                        case ("Con"): {
                            newConnection();
                            break;
                        }
                        case ("NewAdmin"): {
                            authService.createNewAdmin(parseMessageArray[1], parseMessageArray[2], parseMessageArray[3]);
                            break;
                        }
                        case ("NewUser"): {
                            newUser(parseMessageArray[1], parseMessageArray[2], parseMessageArray[3]);
                            break;
                        }
                        case ("EnterAdmin"): {
                            enterAdmin(parseMessageArray[1], parseMessageArray[2]);
                            break;
                        }
                        case ("NewVoting"): {
                            try {
                                votingSystem.newVoting(parseMessageArray);
                                sendMessage("NewVotingOk" + symbol);
                            } catch (TitleIsNotAvailableException e) {
                                sendMessage("Error" + symbol + "Выборы с таким название уже существуют");
                            }
                            break;
                        }
                        case ("StartVoting"): {
                            startVoting();
                            break;
                        }
                        case ("VotingForStart"): {
                            votingSystem.startVoting(parseMessageArray[1]);
                            break;
                        }
                        case ("EndVoting"): {
                            endVoting();
                            break;
                        }
                        case ("VotingForEnd"): {
                            sendResultVoting(votingSystem.endVoting(parseMessageArray[1]));
                            break;
                        }
                        case ("GetResultAllVoting"): {
                            getActiveAndCompletedVoting();
                            break;
                        }
                        case ("GetVotingResult"): {
                            sendMessage("ReturnVotingResultAdmin" + symbol + getVotingResult(parseMessageArray[1]));
                            break;
                        }
                        case ("EnterUser"): {
                            enterUser(parseMessageArray[1], parseMessageArray[2]);
                            break;
                        }
                        case ("UserVoting"): {
                            userVoting();
                            break;
                        }
                        case ("GetCandidate"): {
                            sendCandidate(parseMessageArray[1]);
                            break;
                        }
                        case ("UserChoice"): {
                            userVote(title, parseMessageArray[1]);
                            break;
                        }
                        case ("GetCompletedVoting"): {
                            sendMessage("AllVotingUser" + symbol + getCompletedVoting());
                            break;
                        }
                        case ("GetVotingResultUser"): {
                            sendMessage("ReturnVotingResultUser" + symbol + getVotingResult(parseMessageArray[1]));
                            break;
                        }
                        case ("ChangePassword"): {
                            changePassword(parseMessageArray);
                            break;
                        }
                        case ("UserExit"): {
                            closeConnection();
                            break;
                        }
                        case ("UserEnterMainMenu"):{
                            userInMainMenu();
                            break;
                        }
                    }
                } catch (SocketTimeoutException e) {
                    closeConnection();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    closeConnection();
                    break;
                }
            }
            System.out.println("launch");
        });
        handlerThread.start();
    }

    private void userInMainMenu() {
        server.removeAuthorizedClientFromList(this);
    }

    private void changePassword(String[] parseMessageArray) {
        try {
            authService.changePassword(parseMessageArray[1], parseMessageArray[2], parseMessageArray[3]);
            sendMessage("ChangePasswordOk" + symbol);
        } catch (WrongCredentialsException e) {
            sendMessage("Error" + symbol + "Неверные данные");
        } catch (UserNotFoundException e) {
            sendMessage("Error" + symbol + "Пользователь с такими данными не найден");
        }
    }

    private void userVote(String title, String choice) {
        System.out.println(Integer.parseInt(choice));
        for (Voting voting : votingSystem.getActiveVotingList()) {
            if (voting.getTitle().equalsIgnoreCase(title)) {
                currentUser.vote(voting.getCandidates().get((Integer.parseInt(choice)) - 1));
                voting.getVotedElectors().add(currentUser);
                currentUser.getParticipationInElections().put(title, voting.getCandidates().get((Integer.parseInt(choice)) - 1));
                sendMessage("UserVoteOk" + symbol);
                return;
            }
        }
    }

    private void sendCandidate(String title) {
        String message = "CandidateList" + symbol;
        this.title = title;
        for (Voting voting : votingSystem.getActiveVotingList()) {
            if (voting.getTitle().equalsIgnoreCase(title)) {
                message = message.concat("Голосование: " + title + symbol);
                for (Candidate candidate : voting.getCandidates()) {
                    message = message.concat(candidate.getName() + symbol);
                }
                sendMessage(message);
                return;
            }
        }
    }

    private void userVoting() {
        String message = "UserVotingMenu" + symbol;
        for (Voting voting : votingSystem.getActiveVotingList()) {
            System.out.println(voting.getAllElectors());
            System.out.println(currentUser);
            if (voting.getAllElectors().contains(currentUser) && !voting.getVotedElectors().contains(currentUser)) {
                message = message.concat(voting.getTitle() + symbol);
            }
        }
        sendMessage(message);
    }

    private String getVotingResult(String title) {
        String message = "";
        for (Voting completedVoting : votingSystem.getCompletedVotingList()) {
            if (completedVoting.getTitle().equalsIgnoreCase(title)) {
                message = message.concat("Голосование: " + title + " (завершенное)" + "  (зарегистрированные/проголосовавшие избиратели  " + completedVoting.getAllElectors().size() + "/" + completedVoting.getVotedElectors().size() + symbol);
                for (Candidate candidate : completedVoting.getVotingResult()) {
                    message = message.concat(candidate.getName() + "   Количество набранных голосов: " + candidate.getVoices() + symbol);
                }
                return message;
            }
        }
        for (Voting voting : votingSystem.getActiveVotingList()) {
            if (voting.getTitle().equalsIgnoreCase(title)) {
                message = message.concat("Голосование: " + title + " (активное)" + "  (зарегистрированные/проголосовавшие избиратели  " + voting.getAllElectors().size() + "/" + voting.getVotedElectors().size() + symbol);
                for (Candidate candidate : voting.getCandidates()) {
                    message = message.concat(candidate.getName() + "   Количество набранных голосов: " + candidate.getVoices() + symbol);
                }
                return message;
            }
        }
        return message;
    }

    private void getActiveAndCompletedVoting() {
        String message = "AllVotingAdmin" + symbol;
        System.out.println(votingSystem.getActiveVotingList());
        for (Voting voting : votingSystem.getActiveVotingList()) {
            message = message.concat(voting.getTitle() + symbol).concat("  Активное  ").concat("  (зарегистрированные/проголосовавшие избиратели  " + voting.getAllElectors().size() + "/" + voting.getVotedElectors().size() + symbol);
        }
        sendMessage(message.concat(getCompletedVoting()));
    }

    private String getCompletedVoting() {
        String message = "";
        for (Voting voting : votingSystem.getCompletedVotingList()) {
            message = message.concat(voting.getTitle() + symbol).concat("  Завершенное  ").concat("  (зарегистрированные/проголосовавшие избиратели  " + voting.getAllElectors().size() + "/" + voting.getVotedElectors().size() + symbol);
        }
        return message;
    }

    private void sendResultVoting(List<Candidate> resultVoting) {
        String message = "VotingResult" + symbol;
        for (Candidate candidate : resultVoting) {
            message = message.concat(candidate.getName() + "   Количество набранных голосов: " + candidate.getVoices() + symbol);
        }
        sendMessage(message);
    }

    private void endVoting() {
        String message = "EndVoting" + symbol;
        System.out.println(votingSystem.getActiveVotingList());
        for (Voting voting : votingSystem.getActiveVotingList()) {
            System.out.println(voting);
            message = message.concat(voting.getTitle() + symbol).concat("  (зарегистрированные/проголосовавшие избиратели  " + voting.getAllElectors().size() + "/" + voting.getVotedElectors().size() + symbol);

        }
        sendMessage(message);
    }

    private void startVoting() {
        String message = "StartVoting" + symbol;
        for (Voting voting : votingSystem.getCreatedVotingList()) {
            message = message.concat(voting.getTitle() + symbol);
        }
        sendMessage(message);
    }


    private void enterAdmin(String login, String password) {
        try {
            currentUser = authService.enterAdminByLoginAndPassword(login, password);
            checkingNotAlreadyAuthorize(login);
        } catch (WrongCredentialsException e) {
            sendMessage("Error" + symbol + "Неверные данные");
            return;
        } catch (UserNotFoundException e) {
            sendMessage("Error" + symbol + "Пользователь не найден");
            return;
        } catch (AlreadyAuthorizeException e) {
            sendMessage("Error" + symbol + "Администратор уже работает в системе");
            return;
        }
        sendMessage("AdminEnter" + symbol);
    }

    private void checkingNotAlreadyAuthorize(String login) {
        for (ClientHandler handler : server.getHandlers()) {
            if (handler.getCurrentUser().getLogin().equalsIgnoreCase(login)) {
                throw new AlreadyAuthorizeException("");
            }
        }
        server.addAuthorizedClientToList(this);
    }

    private void enterUser(String login, String password) {
        try {
            currentUser = authService.enterUserByLoginAndPassword(login, password);
            checkingNotAlreadyAuthorize(login);
        } catch (WrongCredentialsException e) {
            sendMessage("Error" + symbol + "Неверные данные");
            return;
        } catch (UserNotFoundException e) {
            sendMessage("Error" + symbol + "Пользователь не найден");
            return;
        } catch (AlreadyAuthorizeException e) {
            sendMessage("Error" + symbol + "Пользователь с таким логином уже в системе");
            return;
        }
        sendMessage("UserEnter" + symbol);
    }

    private void newUser(String login, String name, String password) {
        try {
            authService.createNewUser(login, name, password);
        } catch (LoginIsNotAvailableException e) {
            sendMessage("Error" + symbol + "Логин не доступен");
        }
        sendMessage("NewUserOk" + symbol);
    }

    private void newConnection() {
        if (authService.isAdminRegister()) {
            sendMessage("AdminTrue" + symbol);
        } else sendMessage("AdminFalse" + symbol);
    }

    private void closeConnection() {
        Thread.currentThread().interrupt();
        try {
            socket.close();
            server.removeAuthorizedClientFromList(this);
            System.out.println(currentUser + " вышел");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}