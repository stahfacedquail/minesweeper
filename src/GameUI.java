import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;

public class GameUI extends JFrame
{
	private static final long serialVersionUID = -5285564050945629510L;
	
	private JPanel menuPanel;
	private JPanel toolsPanel; //contains the timer, countdown of mines...
	private JPanel gridPanel;

	protected static int[][] grid;
	protected static Button[][] gridButtons;
	
	private static Size[] gridSizes;
	
	private final static int BEGINNER_MINES = 10;
	private final static int INTERMEDIATE_MINES = 40;
	private final static int EXPERT_MINES = 99;
	private final static int[] numberOfMines = {BEGINNER_MINES, INTERMEDIATE_MINES, EXPERT_MINES};
	
	protected static int minesUnflagged;
	protected static JLabel minesUnflaggedLabel;
	protected static JLabel winOrLoseSmileyLabel;
	
	private static int gridWidth, gridLength;
	protected static int numMines;
	
	private int time;
	private JLabel timeLabel;
	protected static Timer timer;
	
	private JComboBox<String> levelComboBox = new JComboBox<String>(new String[] {"Beginner", "Intermediate", "Expert"});
	
	protected static Button.InitialButtonMouseListener[][] initialButtonListeners;
	
	public GameUI(int level)
	{
		super("Minesweeper");
		
		gridSizes = new Size[3];
		gridSizes[0] = new Size(9, 9);
		gridSizes[1] = new Size(16, 16);
		gridSizes[2] = new Size(16, 30);
		
		levelComboBox.setSelectedIndex(level);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/mine.png")));
	}
	
	public void initComponents()
	{	
		menuPanel = new JPanel();
		toolsPanel = new JPanel(); //contains the timer, countdown of mines...
		gridPanel = new JPanel();
		
		//GRID
		Size size = gridSizes[levelComboBox.getSelectedIndex()];
		gridWidth = size.getWidth();
		gridLength = size.getHeight();
		
		initialButtonListeners = new Button.InitialButtonMouseListener[gridWidth][gridLength];
		
		numMines = numberOfMines[levelComboBox.getSelectedIndex()];
		minesUnflagged = numMines;
		
		grid = new int[gridWidth][gridLength];
		gridButtons = new Button[gridWidth][gridLength];
		
		GridLayout gridLayout = new GridLayout(gridWidth, gridLength);
		gridPanel.setLayout(gridLayout);
		for(int r = 0; r < gridWidth; r++)
			for(int c = 0; c < gridLength; c++)
			{
				gridButtons[r][c] = new Button(r, c);
				gridButtons[r][c].removeMouseListener(gridButtons[r][c].getMouseListeners()[0]);
				initialButtonListeners[r][c] = gridButtons[r][c].new InitialButtonMouseListener(r, c);
				gridButtons[r][c].addMouseListener(initialButtonListeners[r][c]);
				gridPanel.add(gridButtons[r][c]);
			}
				
		//TOOLS
		GridLayout toolsPanelLayout = new GridLayout(1,3);
		toolsPanel.setLayout(toolsPanelLayout);
		
		toolsPanel.setBackground(new Color(255, 255, 255));
		
		minesUnflaggedLabel = new JLabel();
		minesUnflaggedLabel.setFont(new Font("Tahoma", Font.PLAIN, 45));
		minesUnflaggedLabel.setText(minesUnflagged + "");
		toolsPanel.add(minesUnflaggedLabel);
		
		winOrLoseSmileyLabel = new JLabel();
		winOrLoseSmileyLabel.setIcon(new ImageIcon(getClass().getResource("images/happy.png")));
		toolsPanel.add(winOrLoseSmileyLabel);
		
		time = 0;
		timeLabel = new JLabel();
		timeLabel.setFont(new Font("Tahoma", Font.PLAIN, 45));
		int delay = 1000; //milliseconds
		ActionListener taskPerformer = new ActionListener()
											{
										      public void actionPerformed(ActionEvent evt)
										      {
										          time++;
										    	  timeLabel.setText(time + "");
										      }
											};
		timer = new Timer(delay, taskPerformer);
		timer.start();
		toolsPanel.add(timeLabel);
		
		//MENU
		menuPanel.setLayout(new FlowLayout());
		levelComboBox.addActionListener(new ActionListener()
										{
											public void actionPerformed(ActionEvent evt)
											{
												displayNewGame();
											}
										});
		menuPanel.add(levelComboBox);
		
		setLayout(new BorderLayout());
		add(menuPanel, BorderLayout.NORTH);
		add(toolsPanel, BorderLayout.CENTER);
		add(gridPanel, BorderLayout.SOUTH);
		
		pack();
		setVisible(true);
	}
	
	private void displayNewGame()
	{
		if(timer != null)
			timer.stop();
		dispose();
		GameUI newGameUI = new GameUI(levelComboBox.getSelectedIndex());
		newGameUI.initComponents();
	}
	
	protected static String padWithZeroes(int i)
	{
		int lengthOfMaxMines = (new String(numMines + "")).length();
		int lengthOfThisNum = (new String(i + "")).length();
		
		int numOfZeroesToPadWith = lengthOfMaxMines - lengthOfThisNum;
		
		String result = "";
		for(int z = 0; z < numOfZeroesToPadWith; z++)
			result += "0";
		
		return (result + i);
	}
	
	private class Size
	{
		private int width;
		private int height;
		
		public Size(int w, int h)
		{
			width = w;
			height = h;
		}
		
		public int getHeight()
		{
			return height;
		}
		
		public int getWidth()
		{
			return width;
		}
	}
}
