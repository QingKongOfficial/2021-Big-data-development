import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Superb {
    JMenuBar menuBar;
    JMenu menu;

    public Superb(String hostString, String portString, String databaseString, String userString, String passwordString) {
        final JFrame jf = new JFrame("SQL");
        jf.setBounds(300, 150, 800, 400);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setVisible(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        jf.setContentPane(contentPane);

        initialize con = new initialize(hostString,portString,databaseString,userString,passwordString);
        String[] tables = con.getTables();
        menuBar = new JMenuBar();
        menu = new JMenu("Databases");
        JMenu subMenu = new JMenu(databaseString);
        menu.add(subMenu);

        JLabel queryLabel = new JLabel("Command");
        queryLabel.setBounds(10,10,100,25);
        contentPane.add(queryLabel);

        JTextArea input = new JTextArea(10,20);
        input.setBounds(120, 10, 650, 250);
        contentPane.add(input);

        JButton ok = new JButton("Confirm");
        ok.setBounds(650, 280, 100, 40);
        contentPane.add(ok);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query=input.getText();
                String[][] res=con.Connector(query);
                new Recall(res);
            }
        });

        for (String t:tables){
            JMenu subsubMenu = new JMenu(t);
            String[] columns = con.Column(t);
            for (String c:columns){
                subsubMenu.add(new JMenuItem(c));
            }
            subMenu.add(subsubMenu);
        }
        menuBar.add(menu);
        jf.setJMenuBar(menuBar);
        contentPane.revalidate();
    }
}