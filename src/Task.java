import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class Task extends JLabel implements MouseListener, MouseMotionListener{

    private int x;
    private int y;
    private int w;
    private int h;
    private String text;

    private int screenX = 0;
    private int screenY = 0;
    private int myX = 0;
    private int myY = 0;

    int priority;
    boolean clicked;

    //Constructor for one line
    public Task(String t, int xcoord, int ycoord) {
        x = xcoord;
        y = ycoord;
        w = 0;
        priority = 0;
        clicked = false;

        //from with html
        text = Manager.formatTextForList(t);

        //getting height of task field
        int l = 0;
        for(int k = 0; k < text.length(); k++) {
            if(text.charAt(k) == '<' && text.charAt(k + 1) == 'b') {
                k += 3;
                l++;
            }
        }

        String a[] = new String[4];
        String t1 = text;

        //un-form from html
        t1 = t1.replace("<html>", "``");
        t1 = t1.replace("</html>", "``");
        a  = t1.split("<br>");

        //getting width of whole text in one line
        int j = 0;
        for(String s : a) {
            j = Manager.calcTextWidth(s, new Font("Arial", Font.PLAIN, 12));
            if(j >= w) {
                w = j;
            }
        }

        //set height
        h = (l + 1) * 20 + 4;

        this.setText(text);
        this.setLocation(x - w/2, y - 40);
        this.setSize(w, h);

        //set font stuff
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setFont(new Font("Arial", Font.PLAIN, 12));

        //set color stuff in back- & foreground
        this.setOpaque(true);
        this.setForeground(new Color(0, 0, 0));
        updateBackground();

        //set border
        this.setBorder(new LineBorder(new Color(0,0,0), 1));

        //add listeners
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        JPanel view = ((JPanel)Editor.scrollPane.getViewport().getView());
        view.add(this);
        view.validate();

        Manager.editor.repaint();

    }






    //removes selected task
    //
    public void remove() {

        //remove from group
        switch(getGroup()) {
            case 't':
                Manager.editor.todo.remove(this);
                break;
            case 'p':
                Manager.editor.problem.remove(this);
                break;
            case 'r':
                Manager.editor.rework.remove(this);
                break;
            case 'c':
                Manager.editor.current.remove(this);
                break;
            default:
                break;
        }

        //write to file
        try {
            Manager.project.writeData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //remove from panel
        JPanel view = ((JPanel)Editor.scrollPane.getViewport().getView());
        view.remove(this);
        Manager.editor.repaint();

        //realign tasks
        Manager.editor.align();

        //reset bools & helppanel
        Editor.updateHelpPanel('b');

    }



    //lets you edit the task
    //
    public void edit() {
        this.setOpaque(true);
        this.setForeground(new Color(230, 230, 230));
        Manager.editor.repaint();

        @SuppressWarnings("unused")
        EditFrame editFrame = new EditFrame("Edit Task", new Point(this.x, this.y), this);

        this.setForeground(new Color(0, 0, 0));
        Manager.editor.repaint();
    }



    //replace task
    //
    public void replaceWith(Task task){
        int index = 0;

        //get group
        char c = this.getGroup();

        //replace in group
        switch (c){
            case 't':
                index = findIndexInGroup(this, Manager.editor.todo);
                Manager.editor.todo.set(index, task);
                break;
            case 'p':
                index = findIndexInGroup(this, Manager.editor.problem);
                Manager.editor.problem.set(index, task);
                break;
            case 'r':
                index = findIndexInGroup(this, Manager.editor.rework);
                Manager.editor.rework.set(index, task);
                break;
            case 'c':
                index = findIndexInGroup(this, Manager.editor.current);
                Manager.editor.current.set(index, task);
                break;
            default:
                break;
        }

    }



    //gets index of task in group
    //
    public int findIndexInGroup(Task s, ArrayList<Task> list) {
        int index = 0;

        for(Task t : list) {
            if(t.getText().equals(s.getText())){
                break;
            }
            index++;
        }

        return index;
    }



    //get current group of JLabel
    //
    char getGroup(){
        char list = ' ';
        //todo
        if(Manager.editor.todo.contains(this)) {
            list = 't';
        }
        //problem
        else if(Manager.editor.problem.contains(this)) {
            list = 'p';
        }
        //rework
        else if(Manager.editor.rework.contains(this)) {
            list = 'r';
        }
        //current
        else if(Manager.editor.current.contains(this)) {
            list = 'c';
        }

        return list;
    }



    //assigns the added/moved task to a group
    //
    void setGroup() {
        float dtodo = 0, dproblem = 0, drework = 0, dcurrent = 0;

        //calc dist between tasks & labels
        dtodo 		= Manager.getDistBetweenComponents(this, Editor.todoLabel);		//calc all distances
        dproblem 	= Manager.getDistBetweenComponents(this, Editor.problemLabel);
        drework 	= Manager.getDistBetweenComponents(this, Editor.reworkLabel);
        dcurrent 	= Manager.getDistBetweenComponents(this, Editor.currentLabel);

        float arr[] = 	{dtodo, dproblem, drework, dcurrent};
        float min = arr[0];

        //get minimal distance to a grouplabel
        for (float i : arr) {
            if(i < min) {
                min = i;
            }
        }

        //assign task to according list
        //todo
        if(min == dtodo) {
            Manager.editor.todo.add(this);
        }
        //problem
        else if (min == dproblem) {
            Manager.editor.problem.add(this);
        }
        //rework
        else if (min == drework) {
            Manager.editor.rework.add(this);
        }
        //current
        else if (min == dcurrent) {
            Manager.editor.current.add(this);
        }
    }



    //changes priority
    //
    void changePriority() {
        //inc priority var
        this.priority++;
        if(this.priority > 2)
            this.priority = 0;

        //update coloring
        updateBackground();
        Manager.editor.align();
    }



    //sets priority
    //
    void setPriority(int i) {
        this.priority = i;
        updateBackground();
    }


    //gets priority
    //
    int getPriority() {
        return this.priority;
    }



    //updates background
    //
    void updateBackground() {
        //set color to grey when priority == 0
        if(this.priority == 0) {
            this.setBackground(new Color(150, 150, 150));
            Manager.editor.repaint();
        }
        //set color to blue when priority == 1
        else if(this.priority == 1) {
            this.setBackground(new Color(120, 120, 210));
            Manager.editor.repaint();
        }
        //set color to red when priority == 2
        else if(this.priority == 2) {
            this.setBackground(new Color(223, 120, 110));
            Manager.editor.repaint();
        }
    }



    boolean isOutOfPanel(int panelHeight){
        int endh = this.y + this.h;
        boolean outOfPanel = false;

        if(endh > panelHeight){
            outOfPanel = true;
        }
        return outOfPanel;

    }





    /*
     *
     * Listener
     *
     */

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    //MousePressed
    //
    @Override
    public void mousePressed(MouseEvent e) {
        //if task is clicked
        if( (e.getX() >= 0 && e.getX() <= this.getWidth()) &&
                (e.getY() >= 0 && e.getY() <= this.getHeight())) {

            //get clicked task
            Task tt = Editor.getClickedTask();

            if(tt == null){     //if no task is clicked
                if(this.clicked){   // & this is click
                    //set click = false
                    this.clicked = false;
                    this.setBorder(new LineBorder(new Color(0,0,0), 1));
                } else {    // else this isn't clicked
                    //set click = true
                    this.clicked = true;
                    this.setBorder(new LineBorder(new Color(20,40,255), 1));
                }
            } else {       // if there is any clicked task
                if(tt == this){
                    this.clicked = false; //& set this.click = true
                    this.setBorder(new LineBorder(new Color(0,0,0), 1));
                } else {
                    tt.clicked = false; //set task to clicked = false;
                    tt.setBorder(new LineBorder(new Color(0,0,0), 1));

                    this.clicked = true; //& set this.click = true
                    this.setBorder(new LineBorder(new Color(20,40,255), 1));
                 }

            }

            //move
            if(!Editor.move && !Editor.add) {
                Editor.move = true;
                screenX = e.getXOnScreen();
                screenY = e.getYOnScreen();
                myX = getX();
                myY = getY();

            }

        }

    }



    //MouseReleased
    //
    @Override
    public void mouseReleased(MouseEvent e) {
        //while moving when task is dropped
        // align task
        if(Editor.move) {
            Editor.move = false;
            Editor.updateHelpPanel('b');
            Manager.editor.align();


        }

    }



    //MouseDragged
    //
    @Override
    public void mouseDragged(MouseEvent e) {
        if(Editor.move) {
            Editor.updateHelpPanel('m');
            int deltaX = e.getXOnScreen() - screenX;
            int deltaY = e.getYOnScreen() - screenY;

            //move on gui
            //set location to cursor
            setLocation(myX + deltaX, myY + deltaY);
            Manager.editor.repaint();

            //refresh/ change group
            switch(this.getGroup()) {
                case 't':
                    Manager.editor.todo.remove(this);
                    break;
                case 'p':
                    Manager.editor.problem.remove(this);
                    break;
                case 'r':
                    Manager.editor.rework.remove(this);
                    break;
                case 'c':
                    Manager.editor.current.remove(this);
                    break;
                default:
                    break;
            }

            this.setGroup();

            //save in file
            try {
                Manager.project.writeData();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {}

}