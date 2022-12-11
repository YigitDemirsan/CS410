
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

class ContextFreeGrammar {
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

    public void print_word(){
        for (int i = 0; i < this.right_most.size(); i++){
            System.out.print(this.left_most.get(i) + ": ");
            for (String word: this.right_most.get(i)){
                System.out.print(word + " ");
            }
            System.out.println();
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

    public List<String> get_all_terminals() { return terminal_side; }
    public List<String> get_all_nonterminals() { return non_terminal_side; }
    public List<String> all_rules(String t) { return this.right_most.get(this.left_most.indexOf(t)); }
    public List<String> get_left_side() { return left_most; }
    public List<List<String>> get_right_side() { return right_most; }
}
public class ChomskyNormalForm {
    private ContextFreeGrammar grammar;

    ChomskyNormalForm(File grammar_in) throws FileNotFoundException {
        this.grammar = new ContextFreeGrammar(grammar_in);
    }

    public void to_chomsky(){
        this.grammar.print_word();
        this.rmv_Non_Prod();
        this.rmv_In_Access();
        this.rmv_epsilon();
        this.add_terminal_rule();
        this.is_start_symbol();
        this.rmv_More_Than_2();
        this.rmv_Less_Than_1();
        this.grammar.merge_words();
        System.out.println(" ");
        System.out.println("The Chomsky Normal Form is: ");
        this.grammar.print_word();
    }
    protected void rmv_Non_Prod(){
        ArrayList<String> non_productive = new ArrayList<>();
        List<String> nonterminals = this.grammar.get_all_nonterminals();
        for (int i = 0; i < nonterminals.size(); i++) {
            String non_terminal = nonterminals.get(i);
            if (!this.grammar.get_left_side().contains(non_terminal)) {
                non_productive.add(non_terminal);
            }
        }
        int i = 0;
        List<List<String>> right = this.grammar.get_right_side();
        for (int i2 = 0; i2 < right.size(); i2++) {
            List<String> words = right.get(i2);
            i = i + 1;
            List<String> remove = new ArrayList<>(); // word with non_productive non_terminal of some terminal
            for (int i1 = 0; i1 < words.size(); i1++) {
                String word = words.get(i1);
                char[] charArray = word.toCharArray();
                for (int k = 0; k < charArray.length; k++) {
                    char c = charArray[k];
                    for (int j = 0; j < non_productive.size(); j++) {
                        if (non_productive.get(j).equals(String.valueOf(c))) {
                            remove.add(word);
                            break;
                        }
                    }
                }
            }
            this.grammar.get_right_side().get(i - 1).removeAll(remove);
        }
        ArrayList<Integer> remove = new ArrayList<>();
        int k = 0;
        List<String> grammarLeft = this.grammar.get_left_side();
        for (int j = 0; j < grammarLeft.size(); j++) {
            String left = grammarLeft.get(j); // we might remove all word for some non_terminal
            if (this.grammar.all_rules(left).size() == 0) { //and now can remove this non_terminal too
                remove.add(k);
            }
            k = k + 1;
        }
        k = 0;
        for (int i1 = 0; i1 < remove.size(); i1++) {
            int j = remove.get(i1);
            this.grammar.get_all_nonterminals().remove(this.grammar.get_left_side().get(j - k));
            this.grammar.get_right_side().remove(j - k);
            this.grammar.get_left_side().remove(j - k);
            k = k + 1;
        }
        if (non_productive.size() != 0){
            this.grammar.get_all_nonterminals().removeAll(non_productive);
            System.out.print("\nNon-productive non-terminals are: ");
            for (int j = 0; j < non_productive.size(); j++) {
                String s = non_productive.get(j);
                System.out.print(s + " ");
            }
            System.out.println();
            this.grammar.print_word();
        }
        else{
            System.out.println("\nNo non-productive non-terminals.");
        }
    }

    private void rmv_epsilon(){
        if (!this.grammar.is_contain_epsilon){
            System.out.println("No epsilon non-terminals.");
            return;
        }
        ArrayList<String> epsilon = this.grammar.epsilon_non_terminals();
        System.out.print("Epsilon non_terminals: ");
        for (int i = 0; i < epsilon.size(); i++) {
            String s = epsilon.get(i);
            System.out.print(s + " ");
        }
        System.out.println();
        for (int i = 0; i < this.grammar.get_left_side().size(); i++){
            for (int j = 0; j < this.grammar.get_right_side().get(i).size(); j++){
                String word = this.grammar.get_right_side().get(i).get(j);
                char[] charArray = word.toCharArray();
                for (int k = 0; k < charArray.length; k++) {
                    char c = charArray[k];
                    if (epsilon.contains(String.valueOf(c))) {
                        if (word.length() == 1) {
                            this.grammar.get_right_side().get(i).add("0");
                        } else {
                            this.grammar.get_right_side().get(i).add(word.replaceFirst(String.valueOf(c), ""));
                        }
                        break;
                    }
                }
            }
        }
        ArrayList<String> remove = new ArrayList<>();
        List<String> left_side = this.grammar.get_left_side();
        for (int i = 0; i < left_side.size(); i++) {
            String left = left_side.get(i);
            if (left.equals("S")) continue;
            this.grammar.all_rules(left).remove("0");
            if (this.grammar.all_rules(left).size() == 0) {
                remove.add(left);
            }
        }
        for (int i = 0; i < remove.size(); i++) {
            String left = remove.get(i);
            this.grammar.get_all_nonterminals().remove(left);
            this.grammar.get_right_side().remove(this.grammar.get_left_side().indexOf(left));
            this.grammar.get_left_side().remove(left);
        }
        this.grammar.print_word();
    }
    private void rmv_In_Access(){
        ArrayList<String> inaccessible = new ArrayList<>();
        List<String> left_side = this.grammar.get_left_side();
        for (int j = 0; j < left_side.size(); j++) {
            String non_terminal = left_side.get(j);
            if (non_terminal.equals("S")) continue;
            boolean finded = false;
            List<List<String>> right = this.grammar.get_right_side();
            for (int i = 0; i < right.size(); i++) {
                List<String> words = right.get(i);
                for (String word : words) {
                    if (word.contains(non_terminal)) {
                        finded = true;
                        break;
                    }
                }
                if (finded) break;
            }
            if (!finded && !inaccessible.contains(non_terminal)) {
                inaccessible.add(non_terminal);
            }
        }
        if (inaccessible.size() != 0){
            this.grammar.get_all_nonterminals().removeAll(inaccessible);
            System.out.print("Inaccessible non-terminals: ");
            for (int i = 0; i < inaccessible.size(); i++) {
                String s = inaccessible.get(i);
                System.out.print(s + " ");
            }
            System.out.println();
            int i = 0;
            int removed = 0;
            List<String> grammarLeft = this.grammar.get_left_side();
            for (int j = 0; j < grammarLeft.size(); j++) {
                String left = grammarLeft.get(j);
                if (inaccessible.contains(left)) {
                    this.grammar.get_right_side().remove(i - removed);
                    removed += 1;
                }
                i += 1;
            }
            this.grammar.get_left_side().removeAll(inaccessible);
            this.grammar.print_word();
        }
        else {
            System.out.println();
            System.out.println("There is no inaccessible non-terminals.");
        }
    }
    private void is_start_symbol(){
        boolean need_to_add = false;
        for (List<String> rules: this.grammar.get_right_side()) {
            for (String rule : rules) {
                if (rule.contains("S")) {
                    need_to_add = true;
                    break;
                }
            }
            if (need_to_add){
                break;
            }
        }
        if (need_to_add){
            this.grammar.get_all_nonterminals().add("S0");
            System.out.println("\nNew start symbol - S0");
            this.grammar.change_left_side("S", "S0");
            this.grammar.add_new_rule("S", this.grammar.all_rules("S0"));
            this.grammar.print_word();
        }
    }
    private void add_terminal_rule(){
        List<String> left_side = this.grammar.get_left_side();
        for (int j = 0; j < left_side.size(); j++) {
            String left = left_side.get(j);
            List<String> l = new ArrayList<>();
            List<String> rules = this.grammar.all_rules(left);
            for (int i = 0; i < rules.size(); i++) {
                String rule = rules.get(i);
                StringBuilder new_word = new StringBuilder("");
                for (char c : rule.toCharArray()) {
                    if (!String.valueOf(c).toUpperCase().equals(String.valueOf(c))) {
                        new_word.append(String.valueOf(c).toUpperCase());
                    } else {
                        new_word.append(c);
                    }
                }
                l.add(String.valueOf(new_word));
            }
            this.grammar.rewrite_rule(left, l);
        }
        List<String> terminals = this.grammar.get_all_terminals();
        for (int i = 0; i < terminals.size(); i++) {
            String terminal = terminals.get(i);
            String non_terminal = terminal.toUpperCase();
            if (!this.grammar.get_all_nonterminals().contains(non_terminal)) {
                this.grammar.get_all_nonterminals().add(non_terminal);
            }
            List<String> l = new ArrayList<>();
            l.add(terminal);
            this.grammar.add_new_rule(non_terminal, l);
            this.grammar.get_all_nonterminals().add(non_terminal);
        }
        System.out.println("Added new rules has been added to the terminals:");
        this.grammar.print_word();
    }
    private void rmv_Less_Than_1(){
        List<String> grammarLeft = this.grammar.get_left_side();
        for (int i = 0; i < grammarLeft.size(); i++) {
            String left = grammarLeft.get(i);
            ArrayList<String> rule_list = new ArrayList<>();
            List<String> strings = this.grammar.all_rules(left);
            for (int j = 0; j < strings.size(); j++) {
                String rule = strings.get(j);
                if (rule.length() < 2 && rule.toUpperCase().equals(rule)) {
                    List<String> rules = this.grammar.all_rules(rule);
                    for (int k = 0; k < rules.size(); k++) {
                        String new_rules = rules.get(k);
                        if (new_rules.length() == 1 && new_rules.toLowerCase().equals(new_rules)) {
                            rule_list.add(new_rules);
                        }
                        if (new_rules.length() == 2) {
                            rule_list.add(new_rules);
                        }
                    }
                } else {
                    rule_list.add(rule);
                }
            }
            this.grammar.rewrite_rule(left, rule_list);
        }
        System.out.println("Productions with a single non-terminal on the right side are removed.");
        this.grammar.print_word();
    }
    private void rmv_More_Than_2(){
        ArrayList<String> r = new ArrayList<>(this.grammar.get_left_side());
        for (String left: r){
            List<String> new_r = new ArrayList<>();
            for (String rule: this.grammar.all_rules(left)){
                if (rule.length() > 2){
                    while (rule.length() > 2){
                        String w = rule.substring(0, 2);
                        rule = rule.substring(2);
                        List<String> l = new ArrayList<>();
                        l.add(w);
                        String w_ = this.grammar.free();
                        this.grammar.get_all_nonterminals().add(w_);
                        this.grammar.add_new_rule(w_, l);
                        rule = w_ + rule;
                    }
                    new_r.add(rule);
                }
                else {
                    new_r.add(rule);
                }
            }
            this.grammar.rewrite_rule(left, new_r);
        }
        System.out.println("Removing productions with length more than 2.");
        this.grammar.print_word();
    }
}
class Main {
    public static void main(String[] args) {
        try {
            ChomskyNormalForm chomsky = new ChomskyNormalForm(new File("\\..\\test.txt"));
            chomsky.to_chomsky();
        }
        catch (FileNotFoundException e){
            System.out.print(e.toString());
        }
    }
}
