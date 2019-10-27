import java.awt.*;
import java.awt.Frame;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ColorUIResource;

@SuppressWarnings("serial")
public class Editor extends JFrame implements KeyListener {

    //size of editor
    static int width = 850;
    static int height = 550;

    //boolean status
    static boolean activated 	= false;

    //boolean for different actions
    static boolean add			= false;
    static boolean move 		= false;

    //	//array for tasks
    ArrayList<Task> todo		= new ArrayList<>();
    ArrayList<Task> problem 	= new ArrayList<>();
    ArrayList<Task> rework 		= new ArrayList<>();
    ArrayList<Task> current 	= new ArrayList<>();

    //components
    static JPanel       helppanel		= new JPanel();
    static JScrollPane  scrollPane      = new JScrollPane(new JPanel());
    static JLabel 	    titleLabel	 	= new JLabel(Manager.projectName);

    //titlelabels
    static JLabel 	todoLabel		 	= new JLabel("ToDo");
    static JLabel 	problemLabel		= new JLabel("Problem");
    static JLabel 	reworkLabel		 	= new JLabel("Rework");
    static JLabel 	currentLabel		= new JLabel("Current");

    //array for titlelabels
    static JLabel 	labelArr[] 	= {titleLabel, todoLabel, problemLabel, reworkLabel, currentLabel};
    static JLabel hpLabels[] = {
            new JLabel("Add - 2x Click"),
            new JLabel("Remove - R"),
            new JLabel("Edit - E"),
            new JLabel("Move - Drag & Drop"),
            new JLabel("Prioritize - Arrow Up"),
            new JLabel("New - N"),
            new JLabel("Open - O"),
            new JLabel("Rename - 2x Click")};



    //Constructor for Editor Frame
    public Editor() {

        //init frame
        setSize(width, height);
        setTitle("ProjectManager");
        setResizable(true);
        setLayout(null);
        setFocusable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("image.png").getImage());


        //
        //Listener
        //

        //KeyListener
        //
        addKeyListener(this);

        //ComponentListener
        //check if windows is resized
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if(e.getSource() == Manager.editor) {
                    JPanel view = (JPanel) scrollPane.getViewport().getView();
                    arrangeEditorElements(labelArr);
                    align();
                }
            }
        });

        //WindowListener
        // check if window is closed
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                try {
                    Manager.project.writeData();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.getWindow().dispose();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                if(!activated){
                    activated = true;
                    try {
                        Thread.sleep(200);
                        try {
                            Manager.project.getData();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    arrangeEditorElements(labelArr);
                    align();
                }
            }
        });

        //WindowStateListener
        //check if windows is in fullscreen
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent event) {
                boolean isMaximized = false;
                boolean wasMaximized = false;
                if ((event.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH){
                    isMaximized = true;
                } else {
                    isMaximized = false;
                }
                if ((event.getOldState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH){
                    wasMaximized = true;
                } else {
                    wasMaximized = false;
                }

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateScrollPane(getWidth(), getHeight());
                        arrangeEditorElements(labelArr);
                        align();
                    }
                });
            }
        });

        //MouseListener
        //check if mouse is clicked twice
        scrollPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    if(e.getClickCount() == 2) {
                        //add Task
                        if((!move) && (e.getY() > Manager.editor.todoLabel.getY())){
                            updateHelpPanel('a');
                            add = true;

                            //Adding a new Task
                            EditFrame editframe = new EditFrame("Add Task", new Point(e.getX(), e.getY()), null);
                            Manager.editor.repaint();
                            add = false;
                        }
                    }
                }
            }
        });



        //init components
        titleLabel.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        titleLabel.setSize(Manager.calcTextWidth(titleLabel.getText(), new Font("Bahnschrift", Font.BOLD, 20)), 40);
        titleLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {

                    //renaming project
                    SmallFrame frm = new SmallFrame("Rename Project", "", "Projectname");
                }
            }
        });

        //init group labels
        todoLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
        problemLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
        reworkLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
        currentLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));

        //init help panel
        helppanel.setSize(320, 100);
        helppanel.setLocation(10, 10);;
        helppanel.setLayout(null);
        helppanel.setBackground(new Color(50, 50, 50));

        arrangeEditorElements(labelArr);    //setting all labels in right place

        JPanel view = ((JPanel)scrollPane.getViewport().getView());
        view.setLayout(null);


        //adding everything to panel
        for(JLabel l : labelArr) {
            view.add(l);
        }
        view.add(helppanel);

        UIManager.put("ScrollBar.thumbHighlight", new ColorUIResource(Color.ORANGE));
        UIManager.put("ScrollBar.thumbDarkShadow", new ColorUIResource(Color.ORANGE));
        UIManager.put("ScrollBar.highlight", new ColorUIResource(Color.ORANGE));
        UIManager.put("ScrollBar.trackHighlight", new ColorUIResource(Color.ORANGE));

        //init scrollbar
        scrollPane.getVerticalScrollBar().setLayout(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ((JPanel) scrollPane.getViewport().getView()).setBackground(new Color(50,50,50));

        CustomScrollBarUI scrollbarUI = new CustomScrollBarUI();
        scrollPane.getVerticalScrollBar().setUI(scrollbarUI);
        scrollPane.getVerticalScrollBar().setOpaque(true);
        scrollPane.getVerticalScrollBar().setBackground(new Color(50,50,50));

        updateScrollPane(getWidth(), getHeight());

        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }





    //updates Scroll Pane
    //
    void updateScrollPane(int w, int h){
        JPanel view = ((JPanel)scrollPane.getViewport().getView());
        view.setPreferredSize(new Dimension(w, h));
        view.validate();

        scrollPane.setSize(getWidth() - 8,getHeight() - 38);
    }



    //arranging labels when program width changes
    //
    void arrangeEditorElements(JLabel arr[]) {
        int x, w, dx;

        x = (this.getWidth() - arr[0].getWidth()) / 2;		//setting title label in the middle
        arr[0].setLocation(x, 40);
        arr[0].setForeground(new Color(255, 255, 255));

        w = 100;
        dx = (this.getWidth() - (4 * w)) / 5;

        for(int i = 1; i < 5; i++) {								//calculates distance between group labels
            arr[i].setBounds(i * dx + (i - 1) * w, w + 10, w, 20);	//set x, y, w, h
            arr[i].setForeground(new Color(255, 255, 255));
        }



        for(int i = 0; i < hpLabels.length; i++) {
            if(i < 4) {
                hpLabels[i].setBounds(10, 15*i, 130, 15);
                hpLabels[i].setFont(new Font("Bahnschrift", Font.PLAIN, 12));
            }
            else {
                hpLabels[i].setBounds(140, 15*(i - 4), 130, 15);
                hpLabels[i].setFont(new Font("Bahnschrift", Font.PLAIN, 12));
            }
            hpLabels[i].setForeground(new Color(255, 255, 255));
            helppanel.add(hpLabels[i]);
        }

    }



    //aligns tasks
    //
    void align(){
        JPanel view = ((JPanel)scrollPane.getViewport().getView());
        updateScrollPane(getWidth(), getHeight());

        todo = orderList(todo);
        problem = orderList(problem);
        rework = orderList(rework);
        current = orderList(current);

        int h = todoLabel.getY() + 80;
        for(Task t : todo) { //put elements in according list
            t.setLocation(todoLabel.getX() + (todoLabel.getWidth() / 4) - (t.getWidth() / 2) - 5, h - 40);
            h += t.getHeight() + 10;
            if(t.isOutOfPanel(view.getHeight())){
                updateScrollPane(view.getWidth(), view.getHeight() + (t.getHeight() + 10));
            }
        }

        h = problemLabel.getY() + 80;
        for(Task t : problem) { //put elements in according list
            t.setLocation(problemLabel.getX() + (problemLabel.getWidth() / 4) - (t.getWidth() / 2) + 9, h - 40);
            h += t.getHeight() + 10;
            if(t.isOutOfPanel(view.getHeight())){
                updateScrollPane(view.getWidth(), view.getHeight() + (t.getHeight() + 10));
            }
        }

        h = reworkLabel.getY() + 80;
        for(Task t : rework) { //put elements in according list
            t.setLocation(reworkLabel.getX() + (reworkLabel.getWidth() / 4) - (t.getWidth() / 2) + 8, h - 40);
            h += t.getHeight() + 10;
            if(t.isOutOfPanel(view.getHeight())){
                updateScrollPane(view.getWidth(), view.getHeight() + (t.getHeight() + 10));
            }
        }

        h = currentLabel.getY() + 80;
        for(Task t : current) { //put elements in according list
            t.setLocation(currentLabel.getX() + (currentLabel.getWidth() / 4) - (t.getWidth() / 2) + 10, h - 40);
            h += t.getHeight() + 10;
            if(t.isOutOfPanel(view.getHeight())){
                updateScrollPane(view.getWidth(), view.getHeight() + (t.getHeight() + 10));
            }
        }

        Manager.editor.repaint();
    }



    //show in helppanel when keys pressed
    //
    static void updateHelpPanel(char c) {
        helppanel.removeAll();

        for(int i = 0; i < hpLabels.length; i++) {
            if(i < 4) {
                hpLabels[i].setBounds(10, 15*i, 130, 15);
                hpLabels[i].setFont(new Font("Bahnschrift", Font.PLAIN, 12));
                hpLabels[i].setBackground(new Color(50, 50, 50));
                hpLabels[i].setForeground(new Color(255, 255, 255));
                hpLabels[i].setOpaque(true);
            }
            else {
                hpLabels[i].setBounds(140, 15*(i - 4), 130, 15);
                hpLabels[i].setFont(new Font("Bahnschrift", Font.PLAIN, 12));
                hpLabels[i].setBackground(new Color(50, 50, 50));
                hpLabels[i].setForeground(new Color(255, 255, 255));
                hpLabels[i].setOpaque(true);
            }
        }


        switch (c) {
            case 'a':
                hpLabels[0].setForeground(new Color(60, 100, 200));
                Manager.editor.repaint();
                break;
            case 'r':
                hpLabels[1].setForeground(new Color(60, 100, 200));
                Manager.editor.repaint();
                break;
            case 'e':
                hpLabels[2].setForeground(new Color(60, 100, 200));
                Manager.editor.repaint();
                break;
            case 'm':
                hpLabels[3].setForeground(new Color(60, 100, 200));
                Manager.editor.repaint();
                break;
            case 'b':
                for(JLabel l : hpLabels)	{
                    l.setForeground(new Color(255, 255, 255));
                }
                break;
            case 'n':
                hpLabels[5].setForeground(new Color(60, 100, 200));
                Manager.editor.repaint();
                break;
            case 'o':
                hpLabels[6].setForeground(new Color(60, 100, 200));
                Manager.editor.repaint();
                break;
            default:
                break;
        }
        Manager.editor.repaint();
        for(int j = 0; j < hpLabels.length; j++) {		//add to helppanel
            helppanel.add(hpLabels[j]);
        }

    }



    //orders a list according to priority
    //
    ArrayList<Task> orderList(ArrayList<Task> list){
        ArrayList<Task> endList = new ArrayList<>();
        for(int i = 2; i >= 0; i--){
            for(Task t : list){
                if(t.priority == i){
                    endList.add(t);
                }
            }
        }

        return endList;
    }



    //
    //
    static Task getClickedTask(){
        //NULL-Task for return
        Task tt = null;

        //get all task from panel
        Task tasks[] = Manager.getAllTask();

        for(Task t : tasks){
            if(t.clicked){
                tt = t;
            }
        }

        return tt;

    }



    //KeyListener
    //
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        //Closing frame
        if(key == KeyEvent.VK_ESCAPE) {		//close window
            try {
                Manager.project.writeData();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }


        if(key == KeyEvent.VK_E) {			//editing

            Task t = getClickedTask();
            if(t != null){
                updateHelpPanel('e');
                t.edit();
                t.clicked = false;
                t.setBorder(new LineBorder(new Color(0,0,0), 1));
            } else {
            }

        }
        if(key == KeyEvent.VK_R) {			//removing

            Task t = getClickedTask();
            if(t != null){
                updateHelpPanel('r');
                t.remove();
                t.clicked = false;
                t.setBorder(new LineBorder(new Color(0,0,0), 1));
            }
        }
        if(key == KeyEvent.VK_UP) {		//prioritize
            Task t = getClickedTask();
            if(t != null){
                t.changePriority();
                t.clicked = false;
                t.setBorder(new LineBorder(new Color(0,0,0), 1));
            }
        }
        if(key == KeyEvent.VK_N && !move) {			//new Project
            updateHelpPanel('n');
            SmallFrame frm = new SmallFrame("New Project", "", "Projectname");      //create Frame for input
        }

        if(key == KeyEvent.VK_O && !move) {		//open Project
            updateHelpPanel('o');
            SmallFrame frm = new SmallFrame("Open Project", "", "Projectname");     //create Frame for input
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

}