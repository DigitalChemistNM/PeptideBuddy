import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Paths;


public class MainProgram {
    public static void main(String[] args) {  

        Scanner reader = new Scanner(System.in);
        
        //instantiate list of amino acids, currently only includes natural amino acids but can be expanded
        CollectInformation naturalAminoAcids = new CollectInformation();
        ArrayList<AminoAcid> list = naturalAminoAcids.collectAAInfo();
        
        //naturalAminoAcids.printList(list);

        CalculateSequence calculate = new CalculateSequence();
        
        //UI for input a given sequence, sequences can also be hard coded
        System.out.println("Enter your Sequence:");
        String peptide = reader.nextLine();

        //perform calculations
        double mw = calculate.molecularWeightofPeptide(list , peptide);
        double mono = calculate.monoIsotopicMassofPeptide(list, peptide);
        String smiles = calculate.smilesOfPeptide(list, peptide);
        
        //output
        System.out.println("Molecular weight: " + mw);
        System.out.println("Monoisotopic mass: " + mono);
        System.out.println("Smiles: " + smiles);
        
} 

} 

// This class encompasses info about individual amino acids

public class AminoAcid {
    private int number;
    private String name;
    private String threeLetterCode;
    private char oneLetterCode;
    private double monoIsotopicMass;
    private double molecularWeight;
    private String composition;
    private String smiles;

    public AminoAcid(int number, String name, String threeLetterCode, char oneLetterCode, double monoIsotopicMass, double molecularWeight, String composition, String smiles){
        this.number = number;
        this.name = name;
        this.threeLetterCode = threeLetterCode;
        this.oneLetterCode = oneLetterCode;
        this.monoIsotopicMass = monoIsotopicMass;
        this.molecularWeight = molecularWeight;
        this.composition = composition;
        this.smiles = smiles;
    
    }

    public double getMolecularWeight(){
        return this.molecularWeight;
    }

    public double getChar(){
        return this.oneLetterCode;
    }

    public double getMonoIsotopicMass(){
        return this.monoIsotopicMass;
    }

    public String getSmiles(){
        return this.smiles;
    }


    public String toString(){
        return this.number + ", " + this.name + ", " + this.threeLetterCode + ", " + this.oneLetterCode + ", " + this.monoIsotopicMass + ", " + this.molecularWeight + ", " + this.composition;
    }
}

// This class if for parsing the CSV file and populating the info about amino acids

public class CollectInformation{


    public ArrayList<AminoAcid> collectAAInfo(){

        ArrayList<AminoAcid> AminoAcidList = new ArrayList<>();
    
        try (Scanner scanner = new Scanner(Paths.get("aa.csv"))) {

            // we read the file until all lines have been read, split the variables and give them the appropriate assignments
            while (scanner.hasNextLine()) {

                String row = scanner.nextLine();
                String[] parts = row.split(","); 

                int number = Integer.valueOf(parts[0]);
                String name = parts [1];
                String threeLetterCode = parts [2];
                char oneLetterCode = parts[3].charAt(0);
                double monoIsotopicMass = Double.valueOf(parts[4]);
                double molecularWeight = Double.valueOf(parts[5]);
                String composition = parts[6];
                String smiles = parts[7];
                AminoAcid aa = new AminoAcid (number, name, threeLetterCode, oneLetterCode, monoIsotopicMass, molecularWeight, composition, smiles);
                AminoAcidList.add(aa);
                
                
                
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return AminoAcidList;
    }

    public void printList (ArrayList<AminoAcid> list){
        for (int i =0; i < list.size(); i++){
            System.out.println(list.get(i));
        }
    }

    

}

//This class performs neccesary calculations (molecular weight, smiles generation etc) 

public class CalculateSequence{

    //neccesary for fetching the info about a particular amino acid in a sequence
    public int getIndexFromOneLetterCode (ArrayList<AminoAcid> list, char amino){
        for (int i = 0; i < list.size(); i++){
            AminoAcid aa = list.get(i);
            if (aa.getChar() == amino){
                return i;
                
            }


        } return -1;

    }

    public double molecularWeightofPeptide (ArrayList<AminoAcid> list, String sequence){
        double peptideMW = 0.0;
        for (int i = 0; i < sequence.length(); i++ ){
            char aa = sequence.charAt(i);
            int index = getIndexFromOneLetterCode(list, aa);
            
            if (index != -1){
                peptideMW += list.get(index).getMolecularWeight();
            }
        } 
        //there is an additional water molecule, hence the +18 addition
        peptideMW += 18.015;
        return peptideMW;
    } 

    public double monoIsotopicMassofPeptide (ArrayList<AminoAcid> list, String sequence){

        double peptideMW = 0.0;
        for (int i = 0; i < sequence.length(); i++ ){
            char aa = sequence.charAt(i);
            int index = getIndexFromOneLetterCode(list, aa);
            
            if (index != -1){
                peptideMW += list.get(index).getMonoIsotopicMass();
            }
        } 
        peptideMW += 18.011;
        return peptideMW;

    }

    public String smilesOfPeptide (ArrayList<AminoAcid> list, String sequence){

        String peptideSmiles = "";

        for (int i =0; i < sequence.length() -1; i++){

            char aa = sequence.charAt(i);
            int index = getIndexFromOneLetterCode(list, aa);

            if (index != -1){
                peptideSmiles = peptideSmiles + removeLastCharacter(list.get(index).getSmiles());
            }

        }
        peptideSmiles += (list.get((sequence.length())).getSmiles());
        return peptideSmiles;
    }
    //remove last charchter as the oxygen molecule at the end is lost through the condensation reaction, hydrogens are ommitted in this SMILES representation
    public String removeLastCharacter (String aaSmiles){
        return aaSmiles.substring(0, aaSmiles.length() - 1);
    }

}

