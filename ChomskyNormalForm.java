
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
        List<List<String>> right = this.grammar.getRight();
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
            this.grammar.getRight().get(i - 1).removeAll(remove);
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
            this.grammar.getRight().remove(j - k);
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
            for (int j = 0; j < this.grammar.getRight().get(i).size(); j++){
                String word = this.grammar.getRight().get(i).get(j);
                char[] charArray = word.toCharArray();
                for (int k = 0; k < charArray.length; k++) {
                    char c = charArray[k];
                    if (epsilon.contains(String.valueOf(c))) {
                        if (word.length() == 1) {
                            this.grammar.getRight().get(i).add("0");
                        } else {
                            this.grammar.getRight().get(i).add(word.replaceFirst(String.valueOf(c), ""));
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
            this.grammar.getRight().remove(this.grammar.get_left_side().indexOf(left));
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
            List<List<String>> right = this.grammar.getRight();
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
                    this.grammar.getRight().remove(i - removed);
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
        for (List<String> rules: this.grammar.getRight()) {
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
