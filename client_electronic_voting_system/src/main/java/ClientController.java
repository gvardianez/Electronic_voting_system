import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientController {
    private final NetworkService networkService;
    private final Scanner scanner;
    private final char symbol = 10000;

    public ClientController() throws IOException {
        this.networkService = new NetworkService(this);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        networkService.readMessages();
        networkService.sendMessage("Con" + symbol);
    }

    public void parseMessage(String message) {
        String[] parseMessageArray = message.split("" + symbol);
        String parseMessage = parseMessageArray[0];
        System.out.println(Arrays.toString(parseMessageArray));
        switch (parseMessage) {
            case "AdminTrue": {
                printMainMenu();
                break;
            }
            case "AdminFalse": {
                createNewAdmin();
                break;
            }
            case "Error": {
                showError(parseMessageArray[1]);
                networkService.sendMessage("UserEnterMainMenu" + symbol);
                printMainMenu();
                break;
            }
            case "NewUserOk": {
                System.out.println("Новый пользователь зарегистрирован");
                printMainMenu();
                break;
            }
            case "AdminEnter": {
                printAdminMenu();
                break;
            }
            case "StartVoting": {
                startVotingMenu(parseMessageArray);
                printAdminMenu();
                break;
            }
            case "EndVoting": {
                endVotingMenu(parseMessageArray);
                break;
            }
            case "VotingResult": {
                votingResultAdminMenu(parseMessageArray);
                break;
            }
            case "AllVotingAdmin": {
                printAllVotingAdminMenu(parseMessageArray);
                break;
            }
            case "ReturnVotingResultAdmin": {
                printVotingResult(parseMessageArray);
                printAdminMenu();
                break;
            }
            case "UserEnter": {
                printUserMenu();
                break;
            }
            case "UserVotingMenu": {
                userVotingMenu(parseMessageArray);
                break;
            }
            case "CandidateList": {
                candidateListMenu(parseMessageArray);
                break;
            }
            case "NewVotingOk": {
                System.out.println("Новое голосование создано успешно");
                printAdminMenu();
                break;
            }
            case "AllVotingUser": {
                printAllVotingUserMenu(parseMessageArray);
                break;
            }
            case "ReturnVotingResultUser": {
                printVotingResult(parseMessageArray);
                printUserMenu();
                break;
            }
            case "UserVoteOk": {
                System.out.println("Вы успешно проголосовали");
                printUserMenu();
                break;
            }
            case "ChangePasswordOk": {
                System.out.println("Пароль успешно изменен");
                printUserMenu();
                break;
            }
        }
    }

    private void printAllVotingUserMenu(String[] parseMessageArray) {
        System.out.println("Список завершенных голосований:");
        String title = "";
        int count = 0;
        for (int i = 1; i < parseMessageArray.length; i += 2) {
            count++;
            System.out.println(count + ". " + parseMessageArray[i] + parseMessageArray[i + 1]);
        }
        System.out.println("Введите название голосования для просмотра текущих/окончательных результатов:");
        System.out.println("Для перехода в меню пользователя введите back");
        while (!title.equalsIgnoreCase("back")) {
            title = scanner.nextLine();
            for (int i = 1; i < parseMessageArray.length; i += 2) {
                if (title.equalsIgnoreCase(parseMessageArray[i])) {
                    System.out.println(title);
                    networkService.sendMessage("GetVotingResultUser" + symbol + title);
                    return;
                }
            }
        }
        printUserMenu();
    }

    private void candidateListMenu(String[] parseMessageArray) {
        System.out.println(parseMessageArray[1]);
        System.out.println("Для перехода в меню пользователя введите back");
        System.out.println("Чтобы проголосовать за кандидата, введите его номер");
        List<String> stringList = new ArrayList<>();
        for (int i = 2; i < parseMessageArray.length; i++) {
            System.out.println(i - 1 + ". " + parseMessageArray[i]);
            stringList.add(Integer.toString(i - 1));
        }
        String choice = "";
        while (!choice.equalsIgnoreCase("back")) {
            System.out.println(stringList);
            choice = scanner.nextLine();
            if (stringList.contains(choice)) {
                networkService.sendMessage("UserChoice" + symbol + choice);
                return;
            }
        }
        printUserMenu();
    }

    private void userVotingMenu(String[] parseMessageArray) {
        System.out.println("Список активных голосований:");
        for (int i = 1; i < parseMessageArray.length; i++) {
            System.out.println(i + ". " + parseMessageArray[i]);
        }
        System.out.println("Введите название голосования для его просмотра кандитов:");
        System.out.println("Для перехода в меню пользователя введите back");
        String title = "";
        while (!title.equalsIgnoreCase("back")) {
            title = scanner.nextLine();
            for (String string : parseMessageArray) {
                if (title.equals(string)) {
                    networkService.sendMessage("GetCandidate" + symbol + title);
                    return;
                }
            }
        }
        printUserMenu();
    }

    private void printVotingResult(String[] parseMessageArray) {
        for (int i = 2; i < parseMessageArray.length; i++) {
            System.out.println(i - 1 + ". " + parseMessageArray[i]);
        }
        System.out.println("Для перехода в предыдущее меню введите back");
        String choice = "";
        while (!choice.equalsIgnoreCase("back")) {
            choice = scanner.nextLine();
        }
    }

    private void printAllVotingAdminMenu(String[] parseMessageArray) {
        System.out.println("Список активных и завершенных голосований:");
        String title = "";
        int count = 0;
        for (int i = 1; i < parseMessageArray.length; i += 2) {
            count++;
            System.out.println(count + ". " + parseMessageArray[i] + parseMessageArray[i + 1]);
        }
        System.out.println("Введите название голосования для просмотра текущих/окончательных результатов:");
        System.out.println("Для перехода в меню аминистратора введите back");
        while (!title.equalsIgnoreCase("back")) {
            title = scanner.nextLine();
            for (int i = 1; i < parseMessageArray.length; i += 2) {
                if (title.equalsIgnoreCase(parseMessageArray[i])) {
                    System.out.println(title);
                    networkService.sendMessage("GetVotingResult" + symbol + title);
                    return;
                }
            }
        }
        printAdminMenu();
    }

    private void votingResultAdminMenu(String[] parseMessageArray) {
        System.out.println("Голосование завершено, список кандитатов и набранных голосов:");
        for (int i = 1; i < parseMessageArray.length; i++) {
            System.out.println(i + ". " + parseMessageArray[i]);
        }
        System.out.println("Для перехода в меню аминистратора введите back");
        String choice = "";
        while (!choice.equalsIgnoreCase("back")) {
            choice = scanner.nextLine();
        }
        printAdminMenu();
    }

    private void endVotingMenu(String[] parseMessageArray) {
        System.out.println("Список активных голосований:");
        String title = "";
        int count = 0;
        for (int i = 1; i < parseMessageArray.length; i += 2) {
            count++;
            System.out.println(count + ". " + parseMessageArray[i] + parseMessageArray[i + 1]);
        }
        System.out.println("Введите название голосования для его завершения:");
        System.out.println("Для перехода в меню аминистратора введите back");
        while (!title.equalsIgnoreCase("back")) {
            title = scanner.nextLine();
            for (int i = 1; i < parseMessageArray.length; i += 2) {
                if (title.equalsIgnoreCase(parseMessageArray[i])) {
                    networkService.sendMessage("VotingForEnd" + symbol + title);
                    return;
                }
            }
        }
        if (title.equals("back")) {
            printAdminMenu();
        }
    }

    private void startVotingMenu(String[] messageArray) {
        System.out.println("Список неактивных и незавершенных голосований:");
        for (int i = 1; i < messageArray.length; i++) {
            System.out.println(i + ". " + messageArray[i]);
        }
        System.out.println("Введите название голосования для его запуска:");
        System.out.println("Для перехода в меню аминистратора введите back");
        String title = "";
        while (!title.equalsIgnoreCase("back")) {
            title = scanner.nextLine();
            for (String string : messageArray) {
                if (title.equalsIgnoreCase(string)) {
                    networkService.sendMessage("VotingForStart" + symbol + title);
                    return;
                }
            }
        }
    }

    private void showError(String error) {
        System.out.println(error);
    }

    private void createNewAdmin() {
        System.out.println("Зарегистрируйте администратора");
        networkService.sendMessage("NewAdmin" + symbol + printForNewUser());
        printMainMenu();
    }

    private void printMainMenu() {
        String choice = "";
        while (!choice.equals("end")) {
            System.out.println("<<< Главное меню >>>");
            System.out.println("Для выхода из системы введите end");
            System.out.println("1. Войти как администратор");
            System.out.println("2. Войти как пользователь");
            System.out.println("3. Регистрация нового пользователя");
            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    networkService.sendMessage("EnterAdmin" + symbol + printForEnter());
                    return;
                case "2":
                    networkService.sendMessage("EnterUser" + symbol + printForEnter());
                    return;
                case "3":
                    System.out.println("Зарегистрируйте нового пользователя");
                    networkService.sendMessage("NewUser" + symbol + printForNewUser());
                    return;
            }
        }
        networkService.sendMessage("UserExit" + symbol);
        System.exit(0);
    }

    private void printAdminMenu() {
        String choice = "";
        while (!choice.equals("back")) {
            System.out.println("<<< Меню администратора >>>");
            System.out.println("Для перехода в главное меню введите back");
            System.out.println("1. Создать новое голосование и зарегистрировать кандидатов");
            System.out.println("2. Запустить процедуру голосования");
            System.out.println("3. Завершить голосование и получить результаты");
            System.out.println("4. Получить промежуточные/окончательные результаты активного/завершенного голосования");
            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    networkService.sendMessage("NewVoting" + symbol + printForNewVoting());
                    return;
                case "2":
                    networkService.sendMessage("StartVoting" + symbol);
                    return;
                case "3":
                    networkService.sendMessage("EndVoting" + symbol);
                    return;
                case "4":
                    networkService.sendMessage("GetResultAllVoting" + symbol);
                    return;
            }
        }
        networkService.sendMessage("UserEnterMainMenu" + symbol);
        printMainMenu();
    }

    private void printUserMenu() {
        String choice = "";
        while (!choice.equals("back")) {
            System.out.println("<<< Меню пользователя >>>");
            System.out.println("Для перехода в главное меню введите back");
            System.out.println("1. Проголосовать");
            System.out.println("2. Посмотреть результаты завершенных голосований");
            System.out.println("3. Сменить пароль");
            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    networkService.sendMessage("UserVoting" + symbol);
                    return;
                case "2":
                    networkService.sendMessage("GetCompletedVoting" + symbol);
                    return;
                case "3":
                    networkService.sendMessage("ChangePassword" + symbol + printForChangePassword());
                    return;
            }
        }
        networkService.sendMessage("UserEnterMainMenu" + symbol);
        printMainMenu();
    }


    private String printForNewVoting() {
        System.out.println("Ведите название голосования:");
        String newData = scanner.nextLine() + symbol;
        String end = "";
        while (!end.equals("end")) {
            System.out.println("Введите имя кандидата:");
            newData = newData.concat(scanner.nextLine() + symbol);
            System.out.println("Для завершения регистрации кандидатов введите: end");
            end = scanner.nextLine();
        }
        return newData;
    }

    private String printForNewUser() {
        System.out.println("Введите логин:");
        String newData = scanner.nextLine() + symbol;
        System.out.println("Введите имя:");
        newData = newData.concat(scanner.nextLine() + symbol);
        System.out.println("Введите пароль:");
        newData = newData.concat(scanner.nextLine());
        return newData;
    }

    private String printForEnter() {
        System.out.println("Введите логин:");
        String newData = scanner.nextLine() + symbol;
        System.out.println("Введите пароль:");
        newData = newData.concat(scanner.nextLine());
        return newData;
    }

    private String printForChangePassword() {
        System.out.println("Введите логин:");
        String newData = scanner.nextLine() + symbol;
        System.out.println("Введите старый пароль:");
        newData = newData.concat(scanner.nextLine()) + symbol;
        System.out.println("Введите новый пароль:");
        newData = newData.concat(scanner.nextLine());
        return newData;
    }
}
