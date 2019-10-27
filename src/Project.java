import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Project extends File{

    String name;
    String path;



    //Constructor
    //
    public Project(String url) {
        super(url);
        this.name = "";
        path = url;

    }



    //getFrom
    //gets all task from file
    void getData() throws IOException {
        FileReader reader = new FileReader(this);	//init reader
        BufferedReader breader = new BufferedReader(reader);
        String lines[] = new String[4];			//arry for lines


        for (int i = 0; i < 4; i++) {			//read lines
            lines[i] = breader.readLine();		//& put in array
        }
        breader.close();						//close reader

        String t[] = lines[0].split("`");		//split lines-elements by `
        String p[] = lines[1].split("`");		//and put in new array
        String r[] = lines[2].split("`");
        String c[] = lines[3].split("`");


        JPanel view = ((JPanel)Editor.scrollPane.getViewport().getView());
        Component comp[] = view.getComponents();
        for(Component component : comp) {
            if(component instanceof Task) {
                view.remove(component);
            }
        }

        int hh = Editor.todoLabel.getY() + 80;	//height for each task
        for(String s : t) {
            Task t2;
            if(s.contains("^^")){   //priority ==  2
                s = s.substring(2,s.length());
                t2 = new Task(s, Editor.todoLabel.getX() + (Editor.todoLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(2);
            }
            else if(s.contains("^")){   //priority == 1
                s = s.substring(1,s.length());
                t2 = new Task(s, Editor.todoLabel.getX() + (Editor.todoLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(1);
            }
            else {  //priority == 0
                t2 = new Task(s, Editor.todoLabel.getX() + (Editor.todoLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(0);
            }
            Manager.editor.todo.add(t2);	//put in todoList
            hh += t2.getHeight() + 10;		//increase height var
        }

        hh = Editor.problemLabel.getY() + 80;	//reinit height var
        for(String s : p) {
            Task t2;
            if(s.contains("^^")){
                s = s.substring(2,s.length());
                t2 = new Task(s, Editor.problemLabel.getX() + (Editor.problemLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(2);
            }
            else if(s.contains("^")){
                s = s.substring(1,s.length());
                t2 = new Task(s, Editor.problemLabel.getX() + (Editor.problemLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(1);
            }
            else {
                t2 = new Task(s, Editor.problemLabel.getX() + (Editor.problemLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(0);
            }
            Manager.editor.problem.add(t2);	//put in todoList
            hh += t2.getHeight() + 10;		//increase height var
        }

        hh = Editor.reworkLabel.getY() + 80;	//reinit height var
        for(String s : r) {
            Task t2;
            if(s.contains("^^")){
                s = s.substring(2,s.length());
                t2 = new Task(s, Editor.reworkLabel.getX() + (Editor.reworkLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(2);
            }
            else if(s.contains("^")){
                s = s.substring(1,s.length());
                t2 = new Task(s, Editor.reworkLabel.getX() + (Editor.reworkLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(1);
            }
            else {
                t2 = new Task(s, Editor.reworkLabel.getX() + (Editor.reworkLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(0);
            }
            Manager.editor.rework.add(t2);	//put in todoList
            hh += t2.getHeight() + 10;		//increase height var
        }

        hh = Editor.currentLabel.getY() + 80;	//reinit height var
        for(String s : c) {
            Task t2;
            if(s.contains("^^")){
                s = s.substring(2,s.length());
                t2 = new Task(s, Editor.currentLabel.getX() + (Editor.currentLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(2);
            }
            else if(s.contains("^")){
                s = s.substring(1,s.length());
                t2 = new Task(s, Editor.currentLabel.getX() + (Editor.currentLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(1);
            }
            else {
                t2 = new Task(s, Editor.currentLabel.getX() + (Editor.currentLabel.getWidth() / 4), hh);	//add to taskList & editor
                t2.setPriority(0);
            }
            Manager.editor.current.add(t2);	//put in todoList
            hh += t2.getHeight() + 10;		//increase height var
        }

        String s = Manager.project.getName();
        Manager.projectName = s.replace(".pm", "");		//set projectLabel to file name
        Editor.titleLabel.setText(Manager.projectName);
        Editor.titleLabel.setSize(Manager.calcTextWidth(Editor.titleLabel.getText(), new Font("Bahnschrift", Font.BOLD, 20)), 40);

    }



    //writeTo
    //
    void writeData() throws IOException {
        FileWriter writer = new FileWriter(this);
        BufferedWriter bwriter = new BufferedWriter(writer);

        bwriter.write("");	//clear file

        if(Manager.editor.todo.isEmpty()) {									//Manager.editor.todoList
            bwriter.write("`");
            bwriter.write(System.getProperty("line.separator"));
        } else {
            for (Task s : Manager.editor.todo) {
                if(s.priority == 1){
                    bwriter.append("^");
                }
                else if(s.priority == 2){
                    bwriter.append("^^");
                }
                bwriter.append(s.getText());
                bwriter.append("`");
            }
            bwriter.write(System.getProperty("line.separator"));
        }

        if(Manager.editor.problem.isEmpty()) {									//Manager.editor.problemList
            bwriter.write("`");
            bwriter.write(System.getProperty("line.separator"));
        } else {
            for (Task s : Manager.editor.problem) {
                if(s.priority == 1){
                    bwriter.append("^");
                }
                else if(s.priority == 2){
                    bwriter.append("^^");
                }
                bwriter.append(s.getText());
                bwriter.append("`");
            }
            bwriter.write(System.getProperty("line.separator"));
        }

        if(Manager.editor.rework.isEmpty()) {									//Manager.editor.reworkList
            bwriter.write("`");
            bwriter.write(System.getProperty("line.separator"));
        } else {
            for (Task s : Manager.editor.rework) {
                if(s.priority == 1){
                    bwriter.append("^");
                }
                else if(s.priority == 2){
                    bwriter.append("^^");
                }
                bwriter.append(s.getText());
                bwriter.append("`");
            }
            bwriter.write(System.getProperty("line.separator"));
        }

        if(Manager.editor.current.isEmpty()) {									//Manager.editor.currentList
            bwriter.write("`");
        } else {
            for (Task s : Manager.editor.current) {
                if(s.priority == 1){
                    bwriter.append("^");
                }
                else if(s.priority == 2){
                    bwriter.append("^^");
                }
                bwriter.append(s.getText());
                bwriter.append("`");
            }
        }

        bwriter.close(); //close file
    }



    //clear
    //clear all the contents of a file
    void clear() throws IOException {
        FileWriter writer = new FileWriter(this);
        BufferedWriter bwriter = new BufferedWriter(writer);

        bwriter.write("");	//set contents of file to nothing

        for (int i = 0; i < 5; i++) {	//append ` at every line (4 lines)
            bwriter.append("`");
            bwriter.write(System.getProperty("line.separator"));
        }
        bwriter.close();

    }



    //rename
    //renames a project
    void rename(String text) {
        if(!this.name.equals("Project")){
            if(text != "" || text != " ") {
                Manager.projectName = text.replaceAll(System.getProperty("line.separator"), "");	//update global projectName
                Editor.titleLabel.setText(Manager.projectName);			//update the titlelabel in the editor
                Manager.editor.arrangeEditorElements(Editor.labelArr);		//realign all titlelabels
                Manager.editor.repaint();

                Project newProject = new Project(Manager.projectName + ".pm");	//rename the file
                if (!newProject.exists()) {
                    Manager.project.renameTo(newProject);
                }
            }
        }
    }



    //remove
    //removes a project
    void remove() {
        this.remove();
    }

}