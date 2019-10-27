import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.WrappedPlainView;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Wrapper;

@SuppressWarnings("serial")
public class EditFrame extends SmallFrame implements KeyListener, WindowListener {



    //Constructor
    //
    public EditFrame(String title, Point p, Task ot) {
        super(title, "", "Task");

        if(ot == null){ //add
            this.x = Manager.editor.getX() + p.x - w / 2;
            this.y = Manager.editor.getY() + p.y - h / 4;
            setLocation(this.x, this.y);
            this.x = p.x;
            this.y = p.y;
        } else {    //edit
            this.x = Manager.editor.getX() + ot.getX() + ot.getWidth() / 2 - w / 2;
            this.y = Manager.editor.getY() + ot.getY() - h / 4;
            setLocation(this.x, this.y);
        }

        addWindowListener(this);
        addKeyListener(this);

        if(ot != null) {
            String s1 = ot.getText();
            s1 = s1.replaceAll("<html>", "");
            s1 = s1.replaceAll("<br>", System.getProperty("line.separator"));
            s1 = s1.replaceAll("</html>", "");
            ta.setText(s1);
        }

        ta.setSize(150,60);
        ta.setFont(new Font("Arial", Font.PLAIN, 12));

        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Adding Task
                if(getTitle() == "Add Task"){
                    Editor.updateHelpPanel('b');
                    String s = Manager.wrapString(ta.getText(), 27);


                    if(s.equals("") || s.equals(" ") || Manager.checkIfTaskExist(s) != 'n') {
                        EditFrame.this.dispose();
                    } else {
                        Task t;

                        //create new Task
                        if(s.contains("\n") || s.contains(System.getProperty("line.separator"))) {
                            t = new Task(s, x, y);

                        } else {
                            t = new Task(s, x, y);
                        }

                        t.setGroup();

                        try {
                            Manager.project.writeData();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        EditFrame.this.dispose();
                        Manager.editor.align();
                    }
                }

                //Editing Task
                else if(getTitle() == "Edit Task"){
                    ot.setForeground(new Color(0, 0, 0));
                    ot.setBackground(new Color(150, 150, 150));
                    Editor.updateHelpPanel('b');

                    String s = Manager.wrapString(ta.getText(), 27);
                    dispose();

                    if(!s.equals("") || !s.equals(" ") || Manager.checkIfTaskExist(s) == 'n') {

                        dispose();

                        JPanel view = ((JPanel)Editor.scrollPane.getViewport().getView());
                        view.remove(ot);

                        Task nt = new Task(s, x, y);		//create new Task

                        nt.setPriority(ot.getPriority());
                        ot.replaceWith(nt);

                        try {
                            Manager.project.writeData();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }


                        Manager.editor.align();
                        Manager.editor.repaint();

                    }
                }
            }
        });

        panel2.add(ta);
        panel2.add(but);

        add(panel2);
        Manager.editor.repaint();

        setVisible(true);
    }






    //KeyListener
    //
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_ESCAPE) {
            EditFrame.this.dispose();
            Editor.updateHelpPanel('b');
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
        EditFrame.this.dispose();
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
}
