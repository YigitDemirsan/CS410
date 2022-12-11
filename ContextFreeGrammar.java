
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;
import java.io.File;

public class ContextFreeGrammar {
    private Scanner scanner;
    private ArrayList<String> left_most;
    private ArrayList<List<String>> right_most;
    private ArrayList<String> terminal_side;
    private ArrayList<String> non_terminal_side;
    boolean is_contain_epsilon = false;

    ContextFreeGrammar(File input) throws FileNotFoundException {
        this.scanner = new Scanner(input);
        this.left_most = new ArrayList<>();
        this.right_most = new ArrayList<>();
        this.terminal_side = new ArrayList<>();
        this.non_terminal_side = new ArrayList<>();
        this.parse_words();
        this.merge_words();
    }

    public ArrayList<String> epsilon_non_terminals(){
        ArrayList<String> empty_String = new ArrayList<String>();
        for (int i = 0; i < this.right_most.size(); i++){
            if (this.right_most.get(i).contains("0"))
                empty_String.add(this.left_most.get(i));
        }
        boolean is_new = true;
        do{
            is_new = false;
            for (int i = 0; i < this.right_most.size(); i++){
                List<String> get = this.right_most.get(i);
                for (int k = 0; k < get.size(); k++) {
                    String word = get.get(k);
                    boolean all_epsilon = true;
                    char[] charArray = word.toCharArray();
                    for (int j = 0; j < charArray.length; j++) {
                        char c = charArray[j];
                        if (!empty_String.contains(String.valueOf(c))) {
                            all_epsilon = false;
                            break;
                        }
                    }
                    if (all_epsilon && !empty_String.contains(this.left_most.get(i))) {
                        is_new = true;
                        empty_String.add(this.left_most.get(i));
                        break;
                    }
                }
            }
        }while(is_new);
        return empty_String;
    }

    public void add_new_rule(String left_side, List<String> right_most){
        this.left_most.add(left_side);
        this.right_most.add(right_most);
    }

    public void rewrite_rule(String left, List<String> right_most){
        this.right_most.get(this.left_most.indexOf(left)).clear();
        this.right_most.get(this.left_most.indexOf(left)).addAll(right_most);
    }

    public void change_left_side(String old_element, String new_element){
        this.left_most.set(this.left_most.indexOf(old_element), new_element);
    }

    public String free(){
        char c = 'A';
        do {
            if (!this.non_terminal_side.contains(String.valueOf(c)))
                return String.valueOf(c);
            c++;
        }while (c < 'Z');
            return "A";
    }

    private void parse_words(){
        while (this.scanner.hasNext()){
            List<String> line = Arrays.asList(scanner.nextLine().split(" "));
            this.left_most.add(line.get(0));
            this.right_most.add(new ArrayList<>());
            this.right_most.get(this.right_most.size() - 1).addAll(line.subList(1, line.size()));
            for (int i = 0; i < line.size(); i++) {
                String s = line.get(i);
                char[] charArray = s.toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    char c = charArray[j];
                    if (c == '0') {
                        this.is_contain_epsilon = true;
                    } else if (String.valueOf(c).toUpperCase().equals(String.valueOf(c))) {
                        String t = String.valueOf(c);
                        if (!this.non_terminal_side.contains(t))
                            this.non_terminal_side.add(t);
                    } else {
                        String t = String.valueOf(c);
                        if (!this.terminal_side.contains(t))
                            this.terminal_side.add(String.valueOf(c));
                    }
                }
            }
        }
    }

    public void print_word(){
        for (int i = 0; i < this.right_most.size(); i++){
            System.out.print(this.left_most.get(i) + ": ");
            for (String word: this.right_most.get(i)){
                System.out.print(word + " ");
            }
            System.out.println();
        }
    }

    public void merge_words(){
        ArrayList<Integer> removable_elements = new ArrayList<>();
        for (int i = 0; i < this.left_most.size(); i++){
            for (int j = i + 1; j < this.left_most.size(); j++){
                if (this.left_most.get(i).equals(this.left_most.get(j))){
                    this.right_most.get(i).addAll(this.right_most.get(j));
                    removable_elements.add(j);
                }
            }
        }
        int k = 0;
        for (int i = 0 ; i<removable_elements.size() ; i++){
            this.left_most.remove(i - k);
            this.right_most.remove(i - k);
            k = k+ 1;
        }
    }

    public void setLeft_most(ArrayList<String> left_most) {
        this.left_most = left_most;
    }

    public void setRight_most(ArrayList<List<String>> right_most) {
        this.right_most = right_most;
    }

    public void setTerminal_side(ArrayList<String> terminal_side) {
        this.terminal_side = terminal_side;
    }

    public void setNon_terminal_side(ArrayList<String> non_terminal_side) {
        this.non_terminal_side = non_terminal_side;
    }

    public void setIs_contain_epsilon(boolean is_contain_epsilon) {
        this.is_contain_epsilon = is_contain_epsilon;
    }

    public List<String> all_rules(String t) { return this.right_most.get(this.left_most.indexOf(t)); }
    public List<String> get_all_terminals() { return terminal_side; }
    public List<String> get_all_nonterminals() { return non_terminal_side; }
    public List<String> get_left_side() { return left_most; }
    public List<List<String>> getRight() { return right_most; }
}