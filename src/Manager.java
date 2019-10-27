import javax.swing.*;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Manager {

    //editor frame
    static Editor editor;

    //Project information
    static Project project = new Project("Project.pm");
    static String projectName = "Project";
    static String location;



    //Manager
    //
    public static void main(String[] args) {
        editor = new Editor();

    }



    //calcs distances between two components
    //
    static float getDistBetweenComponents(Component l1, Component l2) {
        int x1 = -1, x2 = -1;
        int y1 = -1, y2 = -1;
        int dx =  0, dy =  0;
        float dist = 0;

        x1 = l1.getX() + (l1.getWidth() / 2);	//get position
        y1 = l1.getY();
        x2 = l2.getX() + (l2.getWidth() / 2);
        y2 = l2.getY();

        //get cathetes
        dx = Math.abs(x1 - x2);
        dy = Math.abs(y1 - y2);

        //calc hypotenuse
        dist = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        return dist;
    }



    //calculate width of string
    //
    static int calcTextWidth(String text, Font font){
        int w = 0;

        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
        w = (int) (font.getStringBounds(text, frc).getWidth()) + 10;

        return w;
    }



    //formats text of task
    //
    public static String formatTextForList(String t) {
        String textt = t;

        //start & end with html
        if(!textt.contains("<html>")) {
            textt = "<html>" + textt;
        }
        if(!textt.contains("</html>")) {
            textt = textt + "</html>";
        }

        if(!textt.contains("<br>")) {
            textt = textt.replaceAll(System.getProperty("line.separator"), "<br>");
            textt = textt.replaceAll("\n", "<br>");

        }

        return textt;
    }



    //check if task already exists and return where
    //
    static char checkIfTaskExist(String s){
        char s1 = 'n';
        s = Manager.formatTextForList(s);

        //check in todo
        for(Task t : Manager.editor.todo){
            if(t.getText().equals(s)){
                s1 = 't';
                break;
            }
        }
        //check in problem
        for(Task t : Manager.editor.problem){
            if(t.getText().equals(s)){
                s1 = 'p';
                break;
            }
        }
        //check in rework
        for(Task t : Manager.editor.rework){
            if(t.getText().equals(s)){
                s1 = 'r';
                break;
            }
        }
        //check in current
        for(Task t : Manager.editor.current){
            if(t.getText().equals(s)){
                s1 = 'c';
                break;
            }
        }

        return s1;
    }


    //get all task on panel
    //
    static Task[] getAllTask() {

        ArrayList<Task> listtask = new ArrayList<>();
        JPanel view = ((JPanel)Editor.scrollPane.getViewport().getView());

        Component comp[] = view.getComponents();


        for(Component c : comp){
            if(c instanceof Task){
                listtask.add((Task) c);
            }
        }

        Task task[] = new Task[listtask.size()];
        int i = 0;
        for(Task t : listtask){
            task[i] = listtask.get(i);
            i++;
        }

        return task;
    }



    //if a string is longer than a textarea
    //we will split it by adding \n at
    //the textarea ending
    public static String wrapString(String string, int charWrap) {
        if (!string.contains("\n")) {
            int lastBreak = 0;
            int nextBreak = charWrap;

            if (string.length() > charWrap) {
                String setString = "";
                do {
                    while (string.charAt(nextBreak) != ' ' && nextBreak > lastBreak) {
                        nextBreak--;
                    }

                    if (nextBreak == lastBreak) {
                        nextBreak = lastBreak + charWrap;
                    }
                    setString += string.substring(lastBreak, nextBreak).trim() + System.getProperty("line.separator");
                    lastBreak = nextBreak;
                    nextBreak += charWrap;

                } while (nextBreak < string.length());
                setString += string.substring(lastBreak).trim();
                return setString;

            } else {
                return string;
            }
        } else {
            return string;
        }
    }


}
