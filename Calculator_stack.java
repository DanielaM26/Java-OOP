import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class Calculator extends JFrame {
    JButton digits[] = {
            new JButton(" 0 "),
            new JButton(" 1 "),
            new JButton(" 2 "),
            new JButton(" 3 "),
            new JButton(" 4 "),
            new JButton(" 5 "),
            new JButton(" 6 "),
            new JButton(" 7 "),
            new JButton(" 8 "),
            new JButton(" 9 ")
    };

    JButton operators[] = {
            new JButton(" ( "),
            new JButton(" ) "),
            new JButton(" + "),
            new JButton(" - "),
            new JButton(" * "),
            new JButton(" / "),
            new JButton(" = "),
            new JButton(" C ")
    };

    String oper_values[] = {"+", "-", "*", "/", "=", ""};

    String value;
    char operator;

    JTextArea area = new JTextArea(3, 5);

    static class Node {
        Object data;
        Node next;
        /* Constructor */
        public Node() {
            this.data = '\0';
            this.next = null;
        }

        public Node(Object data) {
            this.data = data;
            this.next = null;
        }
    };

    public static class Stack {
        private Node head;
        /* Constructor */
        public Stack() {
            this.head = null;
        }
        /* Metoda Push */
        public void push(Object data) {
            Node nod = new Node(data);
            nod.next = head;
            head = nod;
        }
        /* Metoda Pop */
        public void pop() {
            if(head != null) {
                head = head.next;
            }
        }
        /* Metoda top */
        public Object top() {
            try {
                if(head != null) {
                    return head.data;
                }
                else throw new Exception("Eroare");
            } catch(Exception e) {
                System.out.println("EROARE Top: Stiva este goala!");
            }
            return 0;
        }
        /* Metoda empty */
        public boolean empty() {
            if(head != null) return false;
            return true;
        }
    };

    public static boolean isoperator(char c) {
        if(c == '+' || c == '-' || c == '*' || c == '/')
            return true;
        return false;
    }

    public static int priority(char c) {
        if(c == '*' || c == '/')
            return 2;
        if(c == '+' || c == '-')
            return 1;
        return -1;
    }

    public static double evaluate(double a, double b, char op) {
        double ans = 0;
        switch(op) {
            case '+':  ans = a + b;
                break;
            case '-':  ans = a - b;
                break;
            case '*':  ans = a * b;
                break;
            case '/':  ans = a / b;
                break;
        }
        return ans;
    }

    public static void checkEval(String expression) {
        if(expression.length() == 0) {
            throw new ArithmeticException("Expresie vida!");
        }
        int counter_open = 0;
        int counter_close = 0;
        /* Parcurg expresia caracter cu caracter si verific posibilele erori */
        for(int i = 0; i < expression.length(); i++) {
            /* Daca caracterul e orice altceva in afara de numere sau operatori, arunc exceptia */
            char c = expression.charAt(i);
            if(!((c >= '0' && c <= '9') || isoperator(c) || c == '(' || c == ')')) {
                throw new ArithmeticException("Caractere ilegale");
            }
            else if(i > 0 && c == ')' && expression.charAt(i - 1) == '(') {
                throw new ArithmeticException("Paranteze incorecte");
            }
            else if(i > 0 && isoperator(c) && isoperator(expression.charAt(i - 1))) {
                throw new ArithmeticException("Operatori succesivi");
            }
            else if(c == '(') {
                counter_open++;
                if(counter_open < counter_close) {
                    throw new ArithmeticException("Parantezele nu sunt inchise corect");
                }
            }
            else if(c == ')') {
                counter_close++;
                if(counter_open < counter_close) {
                    throw new ArithmeticException("Parantezele nu sunt inchise corect");
                }
            }
        }
        if(counter_open != counter_close) {
            throw new ArithmeticException("Parantezele nu sunt inchise corect");
        }
    }

    public static String postfixConvertor(String expression) {
        String ans = "";
        Stack stiva = new Stack();

        for(int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            /* Daca caracterul este un operand, il adaug la ans */
            if(c >= '0' && c <= '9') {
                ans += ' ';
                /* Parcurg caracterele cat timp sunt numere si creez numarul */
                while(i < expression.length() && expression.charAt(i) >= '0' && expression.charAt(i) <= '9') {
                    ans += expression.charAt(i);
                    i++;
                }
                i--;
                ans += ' ';
            } else if (c == '(') { /* Daca caracterul e paranteza deschisa, o adaug in stiva */
                stiva.push('(');
            }
            else if(c == ')') { /* Daca caracterul e paranteza inchisa, pun in sir continutul stivei pana cand dau de paranteza deschisa */
                while((char)stiva.top() != '(') {
                    ans += ' ';
                    ans += (char)stiva.top();
                    ans += ' ';
                    stiva.pop();
                }
                stiva.pop();
            }
            else {
                /* Cat timp mai sunt elemente in stiva iar operatorul curent are prioritate mai mica decat cel din stiva, adaug in sir varful stivei */
                while(!stiva.empty() && priority(c) <= priority((char)stiva.top())) {
                    ans += ' ';
                    ans += (char)stiva.top();
                    ans += ' ';
                    stiva.pop();
                }
                /* La final, adaug si operatorul curent */
                stiva.push(c);
            }
        }
        /* Adaug la final in sir resturile ramase in stiva */
        while(!stiva.empty()) {
            ans += ' ';
            ans += (char)stiva.top();
            ans += ' ';
            stiva.pop();
        }

        return ans;
    }

    public static double postfixEval(String expression) {
        Stack stiva = new Stack();
        double ans = 0, number = -1;
        for(int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if(c == ' ' && number != -1) {
                stiva.push(number);
                number = -1;
            } else if(c >= '0' && c <= '9') {
                if(number == -1)
                    number = c - '0';
                else
                    number = number * 10 + c - '0';
            }
            else if(c != ' '){
                double n1 = (double)stiva.top();
                stiva.pop();
                double n2 = (double)stiva.top();
                stiva.pop();
                ans = evaluate(n2, n1, c);
                stiva.push(ans);
            }
        }
        ans = (double)stiva.top();
        stiva.pop();
        return ans;
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.setSize(230, 240);
        calculator.setTitle(" Java-Calc, PP Lab1 ");
        calculator.setResizable(false);
        calculator.setVisible(true);
        calculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Calculator() {
        add(new JScrollPane(area), BorderLayout.NORTH);
        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new FlowLayout());

        for (int i=0;i<10;i++)
            buttonpanel.add(digits[i]);

        for (int i=0;i<8;i++)
            buttonpanel.add(operators[i]);

        add(buttonpanel, BorderLayout.CENTER);
        area.setForeground(Color.BLACK);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);

        for (int i=0;i<10;i++) {
            int finalI = i;
            digits[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    area.append(Integer.toString(finalI));
                }
            });
        }

        for (int i=0;i<8;i++){
            int finalI = i;
            operators[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (finalI == 7)
                        area.setText("");
                    else if (finalI == 6) {
                        try {
                            checkEval(area.getText());
                            String ans = postfixConvertor(area.getText());
                            double val = postfixEval(ans);
                            area.setText(" = " + Double.toString(val));
                        } catch (Exception e) {
                            area.setText(" !!!Probleme!!! ");
                        }
                    }
                    else if (finalI == 0) {
                        area.append("(");
                    }
                    else if(finalI == 1) {
                        area.append(")");
                    }
                    else {
                        area.append(oper_values[finalI - 2]);
                        operator = oper_values[finalI - 2].charAt(0);
                    }
                }
            });
        }
    }
}