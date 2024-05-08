import java.util.ArrayList;

public class DNA {

    private Profile[] database;
    private String[] STRsOfInterest;

    public DNA(String databaseFile, String STRsFile) {
        createDatabaseOfProfiles(databaseFile);
        readAllSTRsOfInterest(STRsFile);
    }

    public void createDatabaseOfProfiles(String filename) {
        StdIn.setFile(filename);

        int numProfiles = StdIn.readInt();
        database = new Profile[numProfiles];

        for (int i = 0; i < numProfiles; i++) {
            String name = StdIn.readString();
            String sequence1 = StdIn.readString();
            String sequence2 = StdIn.readString();
            database[i] = new Profile(name, null, null, sequence1, sequence2);
        }
    }

    public void readAllSTRsOfInterest(String filename) {
        StdIn.setFile(filename);

        int numSTRs = StdIn.readInt();
        STRsOfInterest = new String[numSTRs];

        for (int i = 0; i < numSTRs; i++) {
            STRsOfInterest[i] = StdIn.readString();
        }
    }

    public Profile createUnknownProfile(String filename) {
        StdIn.setFile(filename);
        String sequence1 = StdIn.readString();
        String sequence2 = StdIn.readString();
        return new Profile("Unknown", null, null, sequence1, sequence2);
    }

    public STR findSTRInSequence(String sequence, String STR) {
        int maxRepeat = 0;
        int STRLength = STR.length();
    
        for (int i = 0; i <= sequence.length() - STRLength; i++) {
            int currentRepeat = 0;
            int j = i;
    
            // Check if the current substring starting at position j matches the STR
            while (j <= sequence.length() - STRLength && sequence.substring(j, j + STRLength).equals(STR)) {
                currentRepeat++;
                j += STRLength;
            }
    
            // Update maxRepeat if the current substring has more consecutive repeats
            maxRepeat = Math.max(maxRepeat, currentRepeat);
        }
    
        return new STR(STR, maxRepeat);
    }

    public void createProfileSTRs(Profile profile, String[] allSTRs) {
        STR[] S1_STRs = new STR[allSTRs.length];
        STR[] S2_STRs = new STR[allSTRs.length];

        for (int i = 0; i < allSTRs.length; i++) {
            S1_STRs[i] = findSTRInSequence(profile.getSequence1(), allSTRs[i]);
            S2_STRs[i] = findSTRInSequence(profile.getSequence2(), allSTRs[i]);
        }

        profile.setS1_STRs(S1_STRs);
        profile.setS2_STRs(S2_STRs);
    }

    public void createDatabaseSTRs() {
        for (Profile profile : database) {
            createProfileSTRs(profile, STRsOfInterest);
        }
    }

    public boolean identicalSTRs(STR[] s1, STR[] s2) {
        for (int i = 0; i < s1.length; i++) {
            if (!s1[i].equals(s2[i])) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Profile> findMatchingProfiles(STR[] unknownProfileS1_STRs) {
        ArrayList<Profile> matchingProfiles = new ArrayList<>();
        for (Profile profile : database) {
            if (identicalSTRs(profile.getS1_STRs(), unknownProfileS1_STRs)) {
                matchingProfiles.add(profile);
            }
        }
        return matchingProfiles;
    }

    public boolean punnetSquare(STR[] firstParent, STR[] inheritedFromFirstParent,
                                STR[] secondParent, STR[] inheritedFromSecondParent) {
        for (int i = 0; i < firstParent.length; i++) {
            if (!(firstParent[i].equals(inheritedFromFirstParent[i]) && secondParent[i].equals(inheritedFromSecondParent[i]))) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Profile> findPossibleParents(STR[] S1_STRs, STR[] S2_STRs) {
        ArrayList<Profile> parentList = new ArrayList<>();

    for (Profile parent1 : database) {
        for (Profile parent2 : database) {
            if (!parent1.equals(parent2)) { 
                if (punnetSquare(parent1.getS1_STRs(), S1_STRs, parent2.getS2_STRs(), S2_STRs)) {
                    parentList.add(parent2);
                    parentList.add(parent1);
                }
                if (punnetSquare(parent1.getS2_STRs(), S1_STRs, parent2.getS2_STRs(), S2_STRs)) {
                    parentList.add(parent2);
                    parentList.add(parent1);
                }
                if (punnetSquare(parent1.getS1_STRs(), S2_STRs, parent2.getS1_STRs(), S2_STRs)) {
                    parentList.add(parent2);
                    parentList.add(parent1);
                }
                if (punnetSquare(parent1.getS2_STRs(), S2_STRs, parent2.getS1_STRs(), S2_STRs)) {
                    parentList.add(parent2);
                    parentList.add(parent1);
                }
            }
        }
    }
    return parentList;
    }

    public Profile[] getDatabase() {
        return database;
    }

    public String[] getSTRsOfInterest() {
        return STRsOfInterest;
    }
}
