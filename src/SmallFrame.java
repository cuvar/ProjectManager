import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class SmallFrame extends JFrame implements ActionListener, KeyListener, WindowListener, DocumentListener {


    JLabel label;
    JTextArea ta;
    JButton but;
    JPanel panel2;

    int x;
    int y;
    final int w;
    final int h;


    //Constructor
    //
    public SmallFrame(String title, String tatext, String labeltext) {

        this.w = 260;
        this.h = 150;

        setTitle(title);
        setSize(w, h);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setFocusable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon("image.png").getImage());

        addWindowListener(this);
        addKeyListener(this);

        panel2 = new JPanel();
        panel2.setSize(w, h);
        panel2.setLayout(null);
        panel2.setBackground(new Color(50, 50, 50));

        label = new JLabel(labeltext);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setBounds(20, 15, 150, 20);
        label.setBackground(new Color(50, 50, 50));
        label.setForeground(new Color(255, 255, 255));

        ta = new JTextArea(tatext);
        ta.setFont(new Font("Arial", Font.PLAIN, 12));
        ta.setBounds(20, 45, 150, 20);
        ta.setBackground(new Color(150, 150, 150));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.addKeyListener(this);

        but = new JButton("OK");
        but.setBounds(190, 45, 50, 22);
        but.setFont(new Font("Arial", Font.PLAIN, 11));
        but.setBackground(new Color(150, 150, 150));
        but.addActionListener(this);

        panel2.add(label);
        panel2.add(ta);
        panel2.add(but);

        add(panel2);
        Manager.editor.repaint();

        setVisible(true);
    }



    //ActionListener
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == but) {
            //open Project
            if (this.getTitle() == "Open Project") {
                if(ta.getText() != "" || ta.getText() != " ") {
                    dispose();
                    openProject(ta.getText());
                }
            }

            //New Project
            else if (this.getTitle() == "New Project") {
                if(ta.getText() != "" || ta.getText() != " ") {
                    dispose();
                    newProject(ta.getText());
                }

            }
            //Rename Project
            else if (this.getTitle() == "Rename Project") {
                dispose();
                Manager.project.rename(ta.getText());
            }
        }
    }

    //KeyListener
    //
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_ESCAPE) {
            dispose();
            Editor.updateHelpPanel('b');
        }
        if (key == KeyEvent.VK_ENTER) {
            //Open Project
            if (this.getTitle() == "Open Project") {
                if(ta.getText() != "" || ta.getText() != " ") {
                    dispose();
                    openProject(ta.getText());
                }
            }

            //New Project
            else if (this.getTitle() == "New Project") {
                if(ta.getText() != "" || ta.getText() != " ") {
                    dispose();
                    newProject(ta.getText());
                }

            }
            //Rename Project
            else if (this.getTitle() == "Rename Project") {
                dispose();
                Manager.project.rename(ta.getText());
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}



    //WindowListener
    //
    @Override
    public void windowClosing(WindowEvent e) {
        Editor.updateHelpPanel('b');
        dispose();
    }

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}

    //DocumentListener
    //
    @Override
    public void insertUpdate(DocumentEvent e) {
        if(getTitle().equals("New Project") || getTitle().equals("Open Project") || getTitle().equals("Rename Project")) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String t = ta.getText();
                    while (Manager.calcTextWidth(t, ta.getFont()) >= ta.getWidth()) {
                        t = t.substring(0, t.length() - 1);
                        ta.setText(t);
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if(getTitle().equals("New Project") || getTitle().equals("Open Project") || getTitle().equals("Rename Project")) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String t = ta.getText();
                    while (Manager.calcTextWidth(t, ta.getFont()) >= ta.getWidth()) {
                        t = t.substring(0, t.length() - 1);
                        ta.setText(t);
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {}




    //Stuff for opening Project
    //
    public void openProject(String s) {
        s = s.replaceAll(System.getProperty("line.separator"), "");    //setup editor

        if((new File(s + ".pm").exists())) {
            //open Project
            try {
                Manager.project.writeData();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            Manager.project = new Project(s + ".pm");
            Manager.editor.todo.clear();            //clear lists
            Manager.editor.problem.clear();
            Manager.editor.rework.clear();
            Manager.editor.current.clear();

            JPanel view = ((JPanel)Editor.scrollPane.getViewport().getView());
            view.removeAll();

            Editor.updateHelpPanel('b');
            Manager.editor.arrangeEditorElements(Editor.labelArr);

            view.add(Manager.editor.helppanel);
            for (JLabel l : Editor.labelArr) {
                view.add(l);
            }

            try {
                Manager.project.getData();        //fill lists
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Manager.editor.repaint();
        }

    }



    //Stuff for new Project
    //
    public void newProject(String s){
        try {					//save file
            Manager.project.writeData();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        s = s.replaceAll(System.getProperty("line.separator"), "");	//setup editor
        Manager.projectName = s;
        Manager.editor.todo.clear();			//clear lists
        Manager.editor.problem.clear();
        Manager.editor.rework.clear();
        Manager.editor.current.clear();

        JPanel view = ((JPanel)Editor.scrollPane.getViewport().getView());

        Manager.editor.titleLabel.setText(Manager.projectName);	//set Title
        view.removeAll();
        Manager.editor.updateHelpPanel('b');

        Manager.editor.arrangeEditorElements(Manager.editor.labelArr);

        view.add(Manager.editor.helppanel);

        for(JLabel l : Manager.editor.labelArr) {
            view.add(l);
        }
        Manager.editor.align();
        Manager.editor.repaint();
        Manager.project = new Project(s + ".pm");			//setup new file0
        try {
            Manager.project.clear();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
