import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Linker<onButtonOk> {
    private JFrame jFrame = new JFrame("START");
    private Container c = jFrame.getContentPane();
    private JLabel a1 = new JLabel("HOST");
    private JTextField host = new JTextField();
    private JLabel a2 = new JLabel("PORT");
    private JTextField port = new JTextField();
    private JLabel a3 = new JLabel("DATABASE");
    private JTextField database = new JTextField();
    private JLabel a4 = new JLabel("USER");
    private JTextField user = new JTextField();
    private JLabel a5 = new JLabel("PASSWORD");
    private JPasswordField password = new JPasswordField();
    private JButton okbtn = new JButton("CONNECT");
    private JButton cancelbtn = new JButton("EXIT");

    public Linker() {
        jFrame.setBounds(600, 200, 400, 340);
        c.setLayout(new BorderLayout());
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Begin();
        jFrame.setVisible(true);
    }
    public void Begin() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(new JLabel("Connection"));
        c.add(titlePanel, "North");
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(null);
        a1.setBounds(50, 20, 60, 20);
        a2.setBounds(50, 60, 60, 20);
        a3.setBounds(50, 100, 60, 20);
        a4.setBounds(50, 140, 60, 20);
        a5.setBounds(50, 180, 60, 20);
        fieldPanel.add(a1);
        fieldPanel.add(a2);
        fieldPanel.add(a3);
        fieldPanel.add(a4);
        fieldPanel.add(a5);
        host.setBounds(110, 20, 200, 20);
        port.setBounds(110, 60, 80, 20);
        database.setBounds(110, 100, 200, 20);
        user.setBounds(110, 140, 120, 20);
        password.setBounds(110, 180, 120, 20);
        fieldPanel.add(host);
        fieldPanel.add(port);
        fieldPanel.add(database);
        fieldPanel.add(user);
        fieldPanel.add(password);
        c.add(fieldPanel, "Center");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okbtn);
        buttonPanel.add(cancelbtn);
        c.add(buttonPanel, "South");

        okbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hostText = host.getText();
                String portText = port.getText();
                String databaseText = database.getText();
                String usernameText = user.getText();
                String passwordText = password.getText();
                jFrame.dispose();
                new Superb(hostText,portText,databaseText,usernameText,passwordText);
            }
        });

        cancelbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}