package com.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JFrame;


public class XGame extends JApplet implements KeyListener, Runnable{
    private Dimension dimension;
    int slpSec = 1000;
    int stepNum = 20;
    int sleepStep[] = {2000, 2000, 2000, 5000, 800, 800, 800, 800, 2000, 4000,
            500, 300, 300, 300, 300, 700, 3000, 3000, 700, 1000};
    int xNum = 4;
    int xPos[] = new int[stepNum];
    int missed = 0;
    int correctPressed = 0;
    int w = 0;
    int h = 0;
    boolean displayed =false;
    boolean isX = false;
    int letterNumber = 0;
    String s[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "Y", "Z" };
    Thread thread;
    Font font;
    FontMetrics fm;
    int counter;
    boolean endGame = false;
    long dispPressInterval = 0;
    long dispSec = 0;
    boolean pressedAfterDisp = false;
    int faultNum = 0;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public XGame () {
        this.dimension = new Dimension(800, 600);
    }
    public Dimension getPreferredSize() {
        return this.dimension;
    }

    public static void main(String[] args)
    {
        JApplet applet = new XGame();
        applet.init();

        JFrame frame = new JFrame("XGame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(applet);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible( true);

        applet.start();
    }

    public void init(){
        this.setFocusable(true);
        addKeyListener(this);
        Random rnd = new Random();

        for(int i = 0; i < xNum; i++){
            int ran = rnd.nextInt(stepNum);
            while(xPos[ran] == 1)  ran = rnd.nextInt(stepNum);
            xPos[ran] = 1;
        }

        thread = new Thread(this);
        thread.start();


    }

    public void keyPressed(KeyEvent e) {
        // TODO 自動生成されたメソッド・スタブ
        if(!pressedAfterDisp){
            if(isX){
                java.awt.Toolkit.getDefaultToolkit().beep();
                System.out.println("X");
                faultNum++;
            }else{
                long l = System.currentTimeMillis() - dispSec;
                dispPressInterval += l;
                pressedAfterDisp = true;
                System.out.println("OK:" + l + "ms");
            }
        }

    }

    public void keyReleased(KeyEvent e) {
        // TODO 自動生成されたメソッド・スタブ

    }

    public void keyTyped(KeyEvent e) {
        // TODO 自動生成されたメソッド・スタブ

    }

    public void run() {
        // TODO 自動生成されたメソッド・スタブ
        for (int i = 0; i < s.length*2; i++){
            if(displayed){
                try{
                    Thread.sleep(500);
                }catch(InterruptedException e){
                }
                displayed = false;
                if(counter >= stepNum){
                    endGame = true;
                    repaint();
                    break;
                }
            }else{
                try{
                    Thread.sleep(slpSec);
                }catch(InterruptedException e){
                }

                Random rnd = new Random();

                if ( i > 0 && !isX ) {
                    if (!pressedAfterDisp) {
                        missed++;
                        System.out.println("missed:" + missed);
                    } else {
                        correctPressed++;
                    }
                }

                if(xPos[counter] == 1){
                    isX = true;
                }else{
                    isX = false;
                    letterNumber = 	 rnd.nextInt(24);
                }
                slpSec = sleepStep[counter++] + rnd.nextInt(300);
                dispSec = System.currentTimeMillis();
                pressedAfterDisp = false;
                displayed = true;
            }
            repaint();
        }
        repaint();
        System.out.println("fault:"+faultNum);
        System.out.println("avg interval:" + (float)dispPressInterval/(float)correctPressed );
    }

    public void paint(Graphics g){
//		g.setColor(Color.black);
//		g.fillRect(0, 0, w, h);
//		g.setColor(Color.white);
        if(h == 0 || w == 0){
            Dimension d = getSize();
            w = d.width;
            h = d.height;
            font =new Font("Dialog", Font.BOLD, (int)((float)w/5));
            g.setFont(font);
            fm = g.getFontMetrics();
            this.setBackground(Color.black);
            this.setForeground(Color.white);
            g.setColor(Color.black);
            g.fillRect(0, 0, w, h);
        }
        g.setFont(font);
        if (endGame){
            g.setColor(Color.black);
            g.fillRect(0, 0, w, h);
            g.setColor(Color.white);
            g.drawString("END", (w - fm.stringWidth("END")) / 4, h / 5);
            g.setFont(new Font("Dialog", Font.BOLD, (int) ((float) w / 12)));
            g.drawString("X FAULT:" + faultNum, (w - fm.stringWidth("END")) / 4, h * 2 / 6);
            g.drawString("MISSED:" + missed, (w- fm.stringWidth("END"))/4, h*3/6);
            double avgspeed = 0;
            if ( correctPressed == 0 ){
                avgspeed = -1.0;
                g.drawString("AVG SPEED: -", (w- fm.stringWidth("END"))/4, h*4/6);
            } else {
                avgspeed = Math.ceil((float) dispPressInterval / (float) (correctPressed));
                g.drawString("AVG SPEED:"+ avgspeed + "ms", (w- fm.stringWidth("END"))/4, h*4/6);
            }
            String praise = "";
            if ( faultNum == 0 && missed == 0 ) {
                if ( avgspeed < 300 ){
                    praise = "Alien!";
                } else if( avgspeed < 450 ){
                    praise = "Mr.Spock!";
                } else if( avgspeed < 700 ){
                    praise = "Jedi Master!";
                } else {
                    praise = "Excellent!";
                }
            } else if ( faultNum < 2 && missed < 1 ){
                if ( avgspeed < 300 ){
                    praise = "Superb.";
                } else if( avgspeed < 450 ){
                    praise = "Excellent.";
                } else if( avgspeed < 700 ){
                    praise = "Great.";
                } else {
                    praise = "Good.";
                }
            } else if (faultNum < 3 && missed < 1 ) {
                if (avgspeed < 300) {
                    praise = "Speedy.";
                } else {
                    praise = "Good.";
                }
            } else if ( avgspeed < 0 ) {
                praise = "Hello?";
            } else {
                praise = "Oh...";
            }

            g.drawString("[ " + praise + " ]", (w- fm.stringWidth("END"))/4, h*5/6);

        }else{
            if(displayed){
                if(isX){
                    g.drawString("X", (w- fm.stringWidth("X"))/2, h/2);
                }else{
                    g.drawString(s[letterNumber], (w- fm.stringWidth(s[letterNumber]))/2, h/2);
                }
            }else {
                g.setColor(Color.black);
                g.fillRect(0, 0, w, h);
            }
        }

    }


}
