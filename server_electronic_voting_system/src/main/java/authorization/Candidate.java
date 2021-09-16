package authorization;

public class Candidate {

    private final String name;
    private int voices;

    public Candidate(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getVoices() {
        return voices;
    }

    public void addVoice(){
        voices++;
    }

    @Override
    public String toString() {
        return "authorization.Candidate{" +
                "name='" + name + '\'' +
                ", voices=" + voices +
                '}';
    }
}
