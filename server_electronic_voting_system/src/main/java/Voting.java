import authorization.Candidate;
import authorization.User;
import java.util.ArrayList;
import java.util.List;

public class Voting {
    private final String title;
    private final List<Candidate> candidates;
    private List<Candidate> votingResult;
    private List<User> allElectors;
    private final List<User> votedElectors;

    public Voting(String title, List<Candidate> candidates) {
        this.title = title;
        this.candidates = candidates;
        this.allElectors = new ArrayList<>();
        this.votedElectors = new ArrayList<>();
        this.votingResult = new ArrayList<>();
    }

    public List<User> getAllElectors() {
        return allElectors;
    }

    public List<User> getVotedElectors() {
        return votedElectors;
    }


    public String getTitle() {
        return title;
    }

    public List<Candidate> getVotingResult() {
        candidates.sort((o1, o2) -> Integer.compare(o2.getVoices(), o1.getVoices()));
        votingResult = candidates;
        return votingResult;
    }

    public void setAllElectors(List<User> allElectors) {
        this.allElectors = allElectors;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }


    @Override
    public String toString() {
        return "Voting{" +
                "title='" + title + '\'' +
                ", candidates=" + candidates +
                '}';
    }
}
