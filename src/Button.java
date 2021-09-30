import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import java.net.URL;


public class Button extends JButton
{
	private static final long serialVersionUID = -7196363953902143443L;

	protected boolean flagged = false;
	protected boolean open = false;
	private int row, column;
	private int[][] grid;
	private Button[][] gridButtons;
	
	public Button(int r, int c) //initial, temporary form of the Button
	{
		row = r;
		column = c;
		
		grid = GameUI.grid;
		gridButtons = GameUI.gridButtons;
		
		setPreferredSize(new Dimension(30,30));
		setBackground(new Color(0, 0, 0));
		setIcon(null);
	}
	
	public void completeButtonInit(int r, int c) //coordinates of the cell the user chose to start with
	{
		addMouseListener(new ButtonMouseListener(r, c));
	}
	
	private class ButtonMouseListener extends MouseAdapter
	{
		private int rowOfButtonToOpenFirst;
		private int colOfButtonToOpenFirst;
		
		public ButtonMouseListener(int r, int c)
		{
			rowOfButtonToOpenFirst = r;
			colOfButtonToOpenFirst = c;
			
			if(row == grid.length - 1 && column == grid[0].length - 1) //if we've just added a listener to the last button in the grid
			{
				open(rowOfButtonToOpenFirst, colOfButtonToOpenFirst);
				checkIfWin();
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent evt)
		{		
			if(evt.getButton() == MouseEvent.BUTTON1)
			{
				//if button is closed and unflagged, open button
				if(!flagged)
				{
					if(!open)
					{
						switch(grid[row][column])
						{
							case -1:
									open = true;
									setBackground(new Color(255, 255, 255));
									 setIcon(new ImageIcon(getClass().getResource("images/mine.png")));
									 exit(false);
									 	break;
									
							case 1:
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							case 8:
									open = true;
									setIcon(new ImageIcon(getClass().getResource("images/" + String.valueOf(grid[row][column]) + ".png")));
									setBackground(new Color(255, 255, 255));
									checkIfWin();
											break;
							
							case 0:
									open(row, column);
									checkIfWin();
											break;
						}	
					}
					else
					{
						switch(grid[row][column])
						{
							case 1:
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							case 8:	int countOfFlags = 0;
									try { if(gridButtons[row-1][column-1].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									try { if(gridButtons[row-1][column].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									try { if(gridButtons[row-1][column+1].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									try { if(gridButtons[row][column-1].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									try { if(gridButtons[row][column+1].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									try { if(gridButtons[row+1][column-1].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									try { if(gridButtons[row+1][column].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									try { if(gridButtons[row+1][column+1].flagged) countOfFlags++; }
									catch(ArrayIndexOutOfBoundsException e) {}
									
									if(countOfFlags == grid[row][column])
									{
										gridButtons[row][column].open = false;
										open(row, column);
										checkIfWin();
									}
										break;
						}
					}
				}
			}
			else if(evt.getButton() == MouseEvent.BUTTON3)
			{
				//check if button is closed
						//if button is flagged, unflag; if it is unflagged, flag
				if(!open)
				{
					if(flagged)
					{
						setIcon(null);
						GameUI.minesUnflagged++;
					}
					else
					{
						setIcon(new ImageIcon(getClass().getResource("images/flag.png")));
						GameUI.minesUnflagged--;
					}
					
					flagged = !flagged;
					GameUI.minesUnflaggedLabel.setText(GameUI.padWithZeroes(GameUI.minesUnflagged));
					checkIfWin();
					
				}
			}
		}
				
		private void open(int r, int c)
		{
			if(!gridButtons[r][c].open && !gridButtons[r][c].flagged)
			{
				gridButtons[r][c].open = true;
				gridButtons[r][c].setIcon(new ImageIcon(getClass().getResource("images/" + String.valueOf(grid[r][c]) + ".png")));
				gridButtons[r][c].setBackground(new Color(255, 255, 255));
				
				if(grid[r][c] >= 0)
				{
					openButton(r-1, c-1);
					openButton(r-1, c);
					openButton(r-1, c+1);
					openButton(r, c-1);
					openButton(r, c+1);
					openButton(r+1, c-1);
					openButton(r+1, c);
					openButton(r+1, c+1);
				}
			}
		}
		
		private void openButton(int r, int c)
		{
			try
			{
				if(!gridButtons[r][c].flagged)
				{
					if(grid[r][c] == 0)
						open(r, c);
					else if(grid[r][c] > 0)
					{
						gridButtons[r][c].open = true;
						gridButtons[r][c].setIcon(new ImageIcon(getClass().getResource("images/" + String.valueOf(grid[r][c]) + ".png")));
						gridButtons[r][c].setBackground(new Color(255, 255, 255));
					}
					else //it's a mine
					{
						gridButtons[r][c].open = true;
						gridButtons[r][c].setIcon(new ImageIcon(getClass().getResource("images/mine.png")));
						gridButtons[r][c].setBackground(new Color(255, 255, 255));
					}
				}
			}
			catch(ArrayIndexOutOfBoundsException e) { }
		}
		
		private String checkGridComplete()
		{	
			boolean foundAClosedBlockThatIsNotAMine = false;
			
			for(int r = 0; r < gridButtons.length; r++)
				for(int c = 0; c < gridButtons[r].length; c++)
				{
					if(gridButtons[r][c].open && grid[r][c] == -1)
						return "L"; //found an uncovered mine -- loss
					
					if(!foundAClosedBlockThatIsNotAMine)
					{
						if(!gridButtons[r][c].open)
						{
							if(grid[r][c] != -1) //is not a mine
								foundAClosedBlockThatIsNotAMine = true;
						}
					}
					
				}
			
			if(foundAClosedBlockThatIsNotAMine)
				return "I"; //there are no mines uncovered, and some number blocks are still covered... game is still in progress
			else
				return "W"; //there are no mines uncovered, and all the number blocks are uncovered: game is won
		}
		
		private void checkIfWin()
		{
			switch(checkGridComplete())
			{
				case "W": exit(true);
							break;
							
				case "L": exit(false);
							break;
			}
		}
		
		private void exit(boolean gameWon)
		{
			GameUI.timer.stop();
			
			//CHANGE THIS!
			if(gameWon)
			{
				GameUI.winOrLoseSmileyLabel.setIcon(new ImageIcon(getClass().getResource("images/cool.png")));
			}
			else
			{
				GameUI.winOrLoseSmileyLabel.setIcon(new ImageIcon(getClass().getResource("images/sad.png")));
			}
		}
	}
	
	public class InitialButtonMouseListener extends MouseAdapter
	{
		private int row;
		private int column;
		
		private int gridLength = grid[0].length;
		private int gridWidth = grid.length;
		
		public InitialButtonMouseListener(int r, int c)
		{
			row = r;
			column = c;
		}
		
		@Override
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getButton() == MouseEvent.BUTTON1)
			{
				assignMines(row, column);
				
				for(int r = 0; r < gridWidth; r++)
					for(int c = 0; c < gridLength; c++)
					{
						gridButtons[r][c].removeMouseListener(gridButtons[r][c].getMouseListeners()[0]);
						gridButtons[r][c].completeButtonInit(row, column);
						
						if(grid[r][c] != -1)
							grid[r][c] = countMinesInSurroundingBlocks(r, c);
					}
			}
		}
		
		private void assignMines(int r, int c) //but not around the cell with coordinates (r,c)
		{
			ArrayList<Integer> dontTouchTheseCells = new ArrayList<Integer>();
			int coordinatesAsAnInt = r*gridLength + c;
			dontTouchTheseCells.add(coordinatesAsAnInt); //don't put a mine in the chosen cell
			
			ArrayList<Coordinates> possibleNeighbourCoordinates = new ArrayList<Coordinates>();
			possibleNeighbourCoordinates.add(new Coordinates(r-1, c-1));
			possibleNeighbourCoordinates.add(new Coordinates(r-1, c));
			possibleNeighbourCoordinates.add(new Coordinates(r-1, c+1));
			possibleNeighbourCoordinates.add(new Coordinates(r,c-1));
			possibleNeighbourCoordinates.add(new Coordinates(r,c+1));
			possibleNeighbourCoordinates.add(new Coordinates(r+1,c-1));
			possibleNeighbourCoordinates.add(new Coordinates(r+1,c));
			possibleNeighbourCoordinates.add(new Coordinates(r+1,c+1));
			
			for(Coordinates possNeighbourCoordinates : possibleNeighbourCoordinates)
				if(possNeighbourCoordinates.validCoordinates())
					dontTouchTheseCells.add(possNeighbourCoordinates.toInt());
			
			Random random = new Random();
			int minePos, row, col;
			boolean validCell = false;
			
			for(int i = 0; i < GameUI.numMines; i++)
			{
				do
				{
					validCell = false;
					
					minePos = random.nextInt(gridWidth * gridLength - 1);
					
					if(!dontTouchTheseCells.contains(minePos))
					{
						row = minePos/gridLength;
						col = minePos%gridLength;
						
						if(grid[row][col] != -1)
						{
							validCell = true;
							grid[row][col] = -1;
						}
					}
				}
				while(!validCell);
			}
		}
		
		private int countMinesInSurroundingBlocks(int r, int c)
		{
			int count = 0;
			
			try {if(grid[r-1][c-1] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			try {if(grid[r-1][c] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			try {if(grid[r-1][c+1] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			try {if(grid[r][c-1] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			try {if(grid[r][c+1] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			try {if(grid[r+1][c-1] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			try {if(grid[r+1][c] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			try {if(grid[r+1][c+1] == -1) count++;}
			catch(ArrayIndexOutOfBoundsException e) { }
			
				return count;
		}
		
		private class Coordinates
		{
			private int rw;
			private int col;
			
			public Coordinates(int r, int c)
			{
				rw = r;
				col = c;
			}
			
			private boolean validRow()
			{
				return 0 <= rw && rw < grid.length;
			}
			
			private boolean validColumn()
			{
				return 0 <= col && col < grid[0].length;
			}
			
			public boolean validCoordinates()
			{
				return validRow() && validColumn();
			}
			
			public int toInt()
			{
				return rw*grid[0].length + col;
			}
		}
	}
}
