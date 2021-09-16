import authorization.AuthorizationService;
import authorization.Candidate;
import errors.TitleIsNotAvailableException;
import java.util.ArrayList;
import java.util.List;

public class VotingSystem {
    private final List<Voting> createdVotingList;
    private final List<Voting> activeVotingList;
    private final List<Voting> completedVotingList;
    private final AuthorizationService authService;

    public VotingSystem(AuthorizationService authService) {
        this.authService = authService;
        this.activeVotingList = new ArrayList<>();
        this.completedVotingList = new ArrayList<>();
        this.createdVotingList = new ArrayList<>();
    }

    public List<Voting> getCompletedVotingList() {
        return completedVotingList;
    }

    public List<Voting> getActiveVotingList() {
        return activeVotingList;
    }

    public void startVoting(String title) {
        for (Voting voting : createdVotingList) {
            if (voting.getTitle().equals(title)) {

                createdVotingList.remove(voting);
                activeVotingList.add(voting);
                voting.setAllElectors(authService.getUserList());
                return;
            }
        }
    }

    public void newVoting(String[] parseMessageArray) {
        String title = parseMessageArray[1];
        for (Voting voting: createdVotingList) {
            if (voting.getTitle().equalsIgnoreCase(title)){
                throw new TitleIsNotAvailableException("");
            }
        }
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 2; i < parseMessageArray.length; i++) {
            candidates.add(new Candidate(parseMessageArray[i]));
        }
        createdVotingList.add(new Voting(title, candidates));
        System.out.println(createdVotingList);
        System.out.println(authService.getUserList());
    }

    public List<Candidate> endVoting(String title) {
        for (Voting voting : activeVotingList) {
            if (voting.getTitle().equals(title)) {
                activeVotingList.remove(voting);
                completedVotingList.add(voting);
                return voting.getVotingResult();
            }
        }
        return new ArrayList<>();
    }

    public List<Voting> getCreatedVotingList() {
        return createdVotingList;
    }

    @Override
    public String toString() {
        return "VotingSystem{" +
                "votingList=" + activeVotingList +
                '}';
    }

}
