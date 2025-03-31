import javax.swing.*;
import java.awt.*;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BattleshipGame extends JFrame {

    private Color oceanColor = new Color(0, 105, 148);
    private Color hitColor = new Color(255, 70, 70);
    private Color missColor = new Color(70, 70, 255);
    private Color buttonColor = new Color(0, 122, 255);
    private Color buttonHoverColor = new Color(10, 132, 255);
    private Color textColor = new Color(242, 242, 247);
    private String backgroundImagePath = "C:\\Users\\stran\\Desktop\\Battleship\\assets\\images\\clouds-over-the-sea-8k-4096x2304.jpg";
    private String tileImagePath = "C:\\Users\\stran\\Desktop\\Battleship\\assets\\images\\images.jpg";
    private Image backgroundImage;
    private Image tileImage;
    private String backgroundMusicPath = "C:\\Users\\stran\\Desktop\\Battleship\\assets\\tunes\\gentle-ocean-waves-mix-2018-19693.wav";
    private Clip backgroundMusic;
    private boolean isMusicPlaying = false;
    private float volume = 0.5f;
    private JSlider volumeSlider;
    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 50;
    private JButton[][] player1Board;
    private JButton[][] player2Board;
    private boolean[][] player1Ships;
    private boolean[][] player2Ships;
    private boolean player1Turn = true;
    private boolean vsComputer = true;
    private boolean manualPlacement = false;
    private JLabel turnLabel;
    private int player1Hits = 0, player1Misses = 0, player2Hits = 0, player2Misses = 0;
    private int totalPlayer1Ships = 0, totalPlayer2Ships = 0;
    private JLabel player1Stats, player2Stats;
    private JPanel mainPanel;
    private int shipsPlaced = 0;
    private int currentShipSize = 5;
    private boolean horizontalPlacement = true;
    private int[] shipSizes = { 5, 4, 3, 3, 2 };
    private String currentMode = "Main Menu";
    private boolean player1PlacementComplete = false;
    private boolean player2PlacementComplete = false;
    private JLabel instructionLabel;

    public BattleshipGame() {

        try {
            backgroundImage = ImageIO.read(new File(backgroundImagePath))
                    .getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
            tileImage = ImageIO.read(new File(tileImagePath))
                    .getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading images", "Error", JOptionPane.ERROR_MESSAGE);
        }

        setResizable(false);
        setTitle("🚢 Battleship Game 🚢");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 700));

        setTitle("🚢 Battleship Game 🚢");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 700));

        loadBackgroundMusic(backgroundMusicPath);
        playBackgroundMusic();

        setupMainMenu();
    }

    private void loadBackgroundMusic(String filePath) {
        try {
            File musicFile = new File(filePath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInput);

            // تنظیم کنترل حجم صدا
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(volume));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading music file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void playBackgroundMusic() {
        if (backgroundMusic != null && !isMusicPlaying) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMusicPlaying = true;
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && isMusicPlaying) {
            backgroundMusic.stop();
            isMusicPlaying = false;
        }
    }

    private void setVolume(float newVolume) {
        volume = newVolume;
        if (backgroundMusic != null && backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(volume));
        }
    }

    private void setupMainMenu() {
        currentMode = "Main Menu";
        mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
    
        GridBagConstraints gbc = new GridBagConstraints();
    
        JLabel titleLabel = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(textColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 0, 20, 0);
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
    
        JButton singlePlayerBtn = createMenuButton("Single Player vs Computer");
        JButton twoPlayerBtn = createMenuButton("Two Players");
        JButton exitBtn = createMenuButton("Exit Game");
    
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 10, 20);
        mainPanel.add(singlePlayerBtn, gbc);
    
        gbc.gridx = 1;
        mainPanel.add(twoPlayerBtn, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(exitBtn, gbc);
    
        singlePlayerBtn.addActionListener(_ -> {
            vsComputer = true;
            currentMode = "Single Player";
            showPlacementOptions();
        });
    
        twoPlayerBtn.addActionListener(_ -> {
            vsComputer = false;
            currentMode = "Two Players";
            showPlacementOptions();
        });
    
        exitBtn.addActionListener(_ -> System.exit(0));
    
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        volumePanel.setOpaque(false);
    
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        volumeLabel.setForeground(textColor);
    
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setPreferredSize(new Dimension(150, 20));
        volumeSlider.setBackground(new Color(0, 0, 0, 0));
        volumeSlider.setForeground(textColor);
        volumeSlider.addChangeListener(_ -> {
            float newVolume = volumeSlider.getValue() / 100f;
            setVolume(newVolume);
        });
    
        JButton musicToggleBtn = new JButton("Music: ON");
        musicToggleBtn.setFont(new Font("Arial", Font.BOLD, 14));
        musicToggleBtn.setForeground(Color.BLACK);
        musicToggleBtn.setBackground(new Color(44, 44, 46));
        musicToggleBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(99, 99, 102), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        musicToggleBtn.setFocusPainted(false);
        musicToggleBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                musicToggleBtn.setBackground(new Color(72, 72, 74));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                musicToggleBtn.setBackground(new Color(44, 44, 46));
            }
        });
        musicToggleBtn.addActionListener(_ -> {
            if (isMusicPlaying) {
                stopBackgroundMusic();
                musicToggleBtn.setText("Music: OFF");
            } else {
                playBackgroundMusic();
                musicToggleBtn.setText("Music: ON");
            }
        });
    
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        volumePanel.add(musicToggleBtn);
    
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(volumePanel, gbc);
    
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // رسم پس‌زمینه
                if (getModel().isArmed()) {
                    g2.setColor(buttonColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(buttonHoverColor);
                } else {
                    g2.setColor(buttonColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // رسم متن با رنگ سفید و سایه
                g2.setColor(new Color(0, 0, 0, 100)); // سایه متن
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x+1, y+1);
                
                g2.setColor(Color.WHITE); // رنگ اصلی متن
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void showPlacementOptions() {
        currentMode = "Placement Options";
        getContentPane().removeAll();
        
        mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
    
        JLabel titleLabel = new JLabel("Ship Placement", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(textColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
    
        JLabel modeLabel = new JLabel("Current Mode: " + (vsComputer ? "Single Player" : "Two Players"),
                SwingConstants.CENTER);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        modeLabel.setForeground(textColor);
        gbc.gridy = 1;
        mainPanel.add(modeLabel, gbc);
    
        JButton randomBtn = createMenuButton("Random Placement");
        JButton manualBtn = createMenuButton("Manual Placement");
        JButton backBtn = createMenuButton("Back to Main Menu");
    
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        mainPanel.add(randomBtn, gbc);
    
        gbc.gridx = 1;
        mainPanel.add(manualBtn, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(backBtn, gbc);
    
        randomBtn.addActionListener(_ -> {
            manualPlacement = false;
            currentMode = "Random Placement";
            initializeGame();
        });
    
        manualBtn.addActionListener(_ -> {
            manualPlacement = true;
            currentMode = "Manual Placement";
            prepareManualPlacement();
        });
    
        backBtn.addActionListener(_ -> {
            getContentPane().removeAll();
            setupMainMenu();
        });
    
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void prepareManualPlacement() {
        getContentPane().removeAll();
    
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
    
        if (!player1PlacementComplete) {
            player1Ships = new boolean[GRID_SIZE][GRID_SIZE];
            player2Ships = new boolean[GRID_SIZE][GRID_SIZE];
            shipsPlaced = 0;
            currentShipSize = shipSizes[0];
    
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            controlPanel.setOpaque(false);
    
            JButton rotateBtn = new JButton("Rotate Ship: " + (horizontalPlacement ? "Horizontal" : "Vertical"));
            styleControlButton(rotateBtn);
            rotateBtn.setForeground(Color.BLACK);
            rotateBtn.addActionListener(_ -> {
                horizontalPlacement = !horizontalPlacement;
                rotateBtn.setText("Rotate Ship: " + (horizontalPlacement ? "Horizontal" : "Vertical"));
            });
    
            instructionLabel = new JLabel("Player 1: Place your " + currentShipSize + "-length ship");
            instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            instructionLabel.setForeground(textColor);
    
            JLabel modeLabel = new JLabel("Mode: " + (vsComputer ? "Single Player" : "Two Players") + " | Manual Placement");
            modeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            modeLabel.setForeground(textColor);
    
            controlPanel.add(rotateBtn);
            controlPanel.add(instructionLabel);
            controlPanel.add(modeLabel);
    
            player1Board = createBoard(true, true);
            player2Board = createBoard(false, false);
    
            JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
            boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            boardsPanel.setOpaque(false);
    
            boardsPanel.add(createBoardPanel(player1Board, "Player 1 Fleet ⚓"));
            boardsPanel.add(createBoardPanel(player2Board, "Player 2 Board"));
    
            mainPanel.add(controlPanel, BorderLayout.NORTH);
            mainPanel.add(boardsPanel, BorderLayout.CENTER);
        } else {
            shipsPlaced = 0;
            currentShipSize = shipSizes[0];
    
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            controlPanel.setOpaque(false);
    
            JButton rotateBtn = new JButton("Rotate Ship: " + (horizontalPlacement ? "Horizontal" : "Vertical"));
            styleControlButton(rotateBtn);
            rotateBtn.setForeground(Color.BLACK);
            rotateBtn.addActionListener(_ -> {
                horizontalPlacement = !horizontalPlacement;
                rotateBtn.setText("Rotate Ship: " + (horizontalPlacement ? "Horizontal" : "Vertical"));
            });
    
            instructionLabel = new JLabel("Player 2: Place your " + currentShipSize + "-length ship");
            instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            instructionLabel.setForeground(textColor);
    
            JLabel modeLabel = new JLabel("Mode: Two Players | Manual Placement");
            modeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            modeLabel.setForeground(textColor);
    
            controlPanel.add(rotateBtn);
            controlPanel.add(instructionLabel);
            controlPanel.add(modeLabel);
    
            player2Board = createBoard(true, true);
            player1Board = createBoard(false, false);
    
            JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
            boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            boardsPanel.setOpaque(false);
    
            boardsPanel.add(createBoardPanel(player2Board, "Player 2 Fleet ⚓"));
            boardsPanel.add(createBoardPanel(player1Board, "Player 1 Board"));
    
            mainPanel.add(controlPanel, BorderLayout.NORTH);
            mainPanel.add(boardsPanel, BorderLayout.CENTER);
        }
    
        add(mainPanel);
        revalidate();
        repaint();
    }

    private void styleControlButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(44, 44, 46));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(99, 99, 102), 1),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(72, 72, 74));
                button.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 44, 46));
                button.setForeground(Color.BLACK);
            }
        });
    }

    private JButton[][] createBoard(boolean isPlayer, boolean interactive) {
        JButton[][] board = new JButton[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        
                        if (this.getText().isEmpty() && this.getBackground() == oceanColor) {
                            g2.setColor(new Color(0, 105, 148, 200));
                            g2.fillRect(0, 0, getWidth(), getHeight());
                            if (tileImage != null) {
                                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                                g2.drawImage(tileImage, 0, 0, getWidth(), getHeight(), this);
                            }
                        } else {
                            g2.setColor(getBackground());
                            g2.fillRect(0, 0, getWidth(), getHeight());
                        }
                        
                        if (!getText().isEmpty()) {
                            FontMetrics fm = g2.getFontMetrics();
                            int x = (getWidth() - fm.stringWidth(getText())) / 2;
                            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                            
                            // رسم سایه متن
                            g2.setColor(new Color(0, 0, 0, 100));
                            g2.drawString(getText(), x+1, y+1);
                            
                            // رسم متن اصلی با رنگ سفید
                            g2.setColor(Color.WHITE);
                            g2.drawString(getText(), x, y);
                        }
                        
                        g2.dispose();
                    }
                    
                    @Override
                    protected void paintBorder(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(new Color(255, 255, 255, 80));
                        g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
                        g2.dispose();
                    }
                };
                
                board[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                board[i][j].setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                board[i][j].setBackground(oceanColor);
                board[i][j].setForeground(Color.WHITE); // تنظیم رنگ متن به سفید
                board[i][j].setOpaque(false);
                board[i][j].setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                if (isPlayer) {
                    if ((player1PlacementComplete && !player2PlacementComplete && player2Ships != null && player2Ships[i][j]) ||
                        (player2PlacementComplete && player1Ships != null && player1Ships[i][j])) {
                        board[i][j].setText("🚢");
                        board[i][j].setBackground(new Color(0, 122, 255));
                    } else if (!player1PlacementComplete && player1Ships != null && player1Ships[i][j]) {
                        board[i][j].setText("🚢");
                        board[i][j].setBackground(new Color(0, 122, 255));
                    }
                }
    
                if (interactive) {
                    int finalI = i;
                    int finalJ = j;
                    board[i][j].addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            if (currentMode.equals("Manual Placement")) {
                                highlightShipPlacement(finalI, finalJ, currentShipSize, horizontalPlacement);
                            }
                        }
                    });
                    
                    board[i][j].addActionListener(_ -> {
                        if (currentMode.equals("Manual Placement")) {
                            handleManualPlacement(finalI, finalJ);
                        } else if (currentMode.equals("Game")) {
                            handleShot(finalI, finalJ);
                        }
                    });
                }
            }
        }
        return board;
    }

    private void highlightShipPlacement(int x, int y, int size, boolean horizontal) {
        JButton[][] currentBoard = !player1PlacementComplete ? player1Board : player2Board;

        // ابتدا همه هایلایت‌ها را پاک کنید
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                currentBoard[i][j].setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        }

        // هایلایت کردن سلول‌های انتخاب شده برای کشتی
        for (int i = 0; i < size; i++) {
            int nx = horizontal ? x + i : x;
            int ny = horizontal ? y : y + i;

            if (nx < GRID_SIZE && ny < GRID_SIZE) {
                currentBoard[nx][ny].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }
        }
    }

    private JPanel createBoardPanel(JButton[][] board, String title) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
    
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gridPanel.setOpaque(false);
    
        for (JButton[] row : board) {
            for (JButton btn : row) {
                gridPanel.add(btn);
            }
        }
    
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(textColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
    
        return panel;
    }

    private void handleManualPlacement(int x, int y) {
        JButton[][] currentBoard = !player1PlacementComplete ? player1Board : player2Board;
        boolean[][] currentPlayerShips = !player1PlacementComplete ? player1Ships : player2Ships;
    
        highlightShipPlacement(x, y, currentShipSize, horizontalPlacement);
    
        if (canPlaceShip(currentPlayerShips, x, y, currentShipSize, horizontalPlacement)) {
            placeShip(currentPlayerShips, x, y, currentShipSize, horizontalPlacement);
            
            for (int i = 0; i < currentShipSize; i++) {
                int nx = horizontalPlacement ? x + i : x;
                int ny = horizontalPlacement ? y : y + i;
                
                if (nx < GRID_SIZE && ny < GRID_SIZE) {
                    currentBoard[nx][ny].setText("🚢");
                    currentBoard[nx][ny].setBackground(new Color(30, 144, 255));
                    currentBoard[nx][ny].setOpaque(true);
                }
            }
    
            shipsPlaced++;
            if (shipsPlaced < shipSizes.length) {
                currentShipSize = shipSizes[shipsPlaced];
                instructionLabel.setText((!player1PlacementComplete ? "Player 1" : "Player 2") +
                        ": Place your " + currentShipSize + "-length ship");
            } else {
                if (!player1PlacementComplete) {
                    player1PlacementComplete = true;
                    totalPlayer1Ships = countShips(player1Ships);
                    if (vsComputer) {
                        initializeGame();
                    } else {
                        prepareManualPlacement();
                    }
                } else {
                    player2PlacementComplete = true;
                    totalPlayer2Ships = countShips(player2Ships);
                    initializeGame();
                }
            }
        } else {
            showError("Cannot place ship here! Try another location.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void initializeGame() {
        if (!manualPlacement) {
            if (!player1PlacementComplete) {
                player1Ships = new boolean[GRID_SIZE][GRID_SIZE];
                totalPlayer1Ships = placeRandomShips(player1Ships);
                player1PlacementComplete = true;
            }

            if (!vsComputer && !player2PlacementComplete) {
                player2Ships = new boolean[GRID_SIZE][GRID_SIZE];
                totalPlayer2Ships = placeRandomShips(player2Ships);
                player2PlacementComplete = true;
            } else if (vsComputer) {
                player2Ships = new boolean[GRID_SIZE][GRID_SIZE];
                totalPlayer2Ships = placeRandomShips(player2Ships);
                player2PlacementComplete = true;
            }
        } else {
            if (vsComputer && !player2PlacementComplete) {
                player2Ships = new boolean[GRID_SIZE][GRID_SIZE];
                totalPlayer2Ships = placeRandomShips(player2Ships);
                player2PlacementComplete = true;
            }
        }

        currentMode = "Game";
        startGame();
    }

    private int countShips(boolean[][] board) {
        int count = 0;
        for (boolean[] row : board) {
            for (boolean cell : row) {
                if (cell)
                    count++;
            }
        }
        return count;
    }

    private int placeRandomShips(boolean[][] board) {
        Random rand = new Random();
        int totalShips = 0;

        for (int size : shipSizes) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 100) {
                int x = rand.nextInt(GRID_SIZE);
                int y = rand.nextInt(GRID_SIZE);
                boolean horizontal = rand.nextBoolean();

                if (canPlaceShip(board, x, y, size, horizontal)) {
                    placeShip(board, x, y, size, horizontal);
                    totalShips += size;
                    placed = true;
                }
                attempts++;
            }
        }
        return totalShips;
    }

    private boolean canPlaceShip(boolean[][] board, int x, int y, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int nx = horizontal ? x + i : x;
            int ny = horizontal ? y : y + i;

            if (nx >= GRID_SIZE || ny >= GRID_SIZE || board[nx][ny]) {
                return false;
            }
        }
        return true;
    }

    private void placeShip(boolean[][] board, int x, int y, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int nx = horizontal ? x + i : x;
            int ny = horizontal ? y : y + i;
            board[nx][ny] = true;
        }
    }

    private void startGame() {
        getContentPane().removeAll();
    
        JPanel mainGamePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setColor(new Color(0, 0, 0, 120));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainGamePanel.setOpaque(false);
        mainGamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // ایجاد تخته‌های جدید با وضعیت فعلی
        JButton[][] newPlayer1Board = createBoard(false, false);
        JButton[][] newPlayer2Board = createBoard(false, false);
    
        // کپی وضعیت تخته‌های قبلی به تخته‌های جدید
        if (player1Board != null) {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (player1Board[i][j].getBackground() == hitColor ||
                        player1Board[i][j].getBackground() == missColor) {
                        newPlayer1Board[i][j].setBackground(player1Board[i][j].getBackground());
                        newPlayer1Board[i][j].setText(player1Board[i][j].getText());
                    }
                }
            }
        }
    
        if (player2Board != null) {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (player2Board[i][j].getBackground() == hitColor ||
                        player2Board[i][j].getBackground() == missColor) {
                        newPlayer2Board[i][j].setBackground(player2Board[i][j].getBackground());
                        newPlayer2Board[i][j].setText(player2Board[i][j].getText());
                    }
                }
            }
        }
    
        player1Board = newPlayer1Board;
        player2Board = newPlayer2Board;
    
        // فعال کردن تخته مناسب برای حمله
        if (player1Turn) {
            player2Board = createBoard(false, true); // تخته حریف را برای حمله فعال کنید
            // کپی وضعیت قبلی
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (newPlayer2Board[i][j].getBackground() == hitColor ||
                        newPlayer2Board[i][j].getBackground() == missColor) {
                        player2Board[i][j].setBackground(newPlayer2Board[i][j].getBackground());
                        player2Board[i][j].setText(newPlayer2Board[i][j].getText());
                    }
                }
            }
        } else {
            player1Board = createBoard(false, true); // تخته حریف را برای حمله فعال کنید
            // کپی وضعیت قبلی
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (newPlayer1Board[i][j].getBackground() == hitColor ||
                        newPlayer1Board[i][j].getBackground() == missColor) {
                        player1Board[i][j].setBackground(newPlayer1Board[i][j].getBackground());
                        player1Board[i][j].setText(newPlayer1Board[i][j].getText());
                    }
                }
            }
        }
    
        updateGameBoards();
    
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardsPanel.setOpaque(false);
    
        boardsPanel.add(createBoardPanel(player1Board, player1Turn ? "Player 1 Board" : "Attack Player 1"));
        boardsPanel.add(createBoardPanel(player2Board, player1Turn ? "Attack Player 2" : "Player 2 Board"));
    
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    
        turnLabel = new JLabel((player1Turn ? "Player 1" : "Player 2") + "'s Turn ⚔", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 23));
        turnLabel.setForeground(textColor);
    
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        statsPanel.setOpaque(false);
    
        player1Stats = new JLabel("Player 1 Hits: " + player1Hits + " 💥 | Misses: " + player1Misses + " 🌊", 
                SwingConstants.CENTER);
        player2Stats = new JLabel((vsComputer ? "Enemy" : "Player 2") + " Hits: " + player2Hits + " 💥 | Misses: " 
                + player2Misses + " 🌊", SwingConstants.CENTER);
    
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 17);
    
        player1Stats.setFont(emojiFont);
        player2Stats.setFont(emojiFont);
        player1Stats.setForeground(textColor);
        player2Stats.setForeground(textColor);
    
        JLabel modeLabel = new JLabel("Mode: " + (vsComputer ? "Single Player" : "Two Players") + 
                " | Placement: " + (manualPlacement ? "Manual" : "Random"), 
                SwingConstants.CENTER);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        modeLabel.setForeground(textColor);
    
        statsPanel.add(player1Stats);
        statsPanel.add(player2Stats);
    
        controlPanel.add(turnLabel, BorderLayout.NORTH);
        controlPanel.add(modeLabel, BorderLayout.CENTER);
        controlPanel.add(statsPanel, BorderLayout.SOUTH);
    
        mainGamePanel.add(controlPanel, BorderLayout.NORTH);
        mainGamePanel.add(boardsPanel, BorderLayout.CENTER);
    
        add(mainGamePanel);
        revalidate();
        repaint();
    }

    private void updateGameBoards() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (player1Board[i][j].getBackground() == hitColor) {
                    player1Board[i][j].setText("💥");
                    player1Board[i][j].setBackground(hitColor);
                    // player1Board[i][j].setForeground(Color.WHITE);
                } else if (player1Board[i][j].getBackground() == missColor) {
                    player1Board[i][j].setText("🌊");
                    player1Board[i][j].setBackground(missColor);
                    // player1Board[i][j].setForeground(Color.WHITE);
                }
    
                if (player2Board[i][j].getBackground() == hitColor) {
                    player2Board[i][j].setText("💥");
                    player2Board[i][j].setBackground(hitColor);
                    // player2Board[i][j].setForeground(Color.WHITE);
                } else if (player2Board[i][j].getBackground() == missColor) {
                    player2Board[i][j].setText("🌊");
                    player2Board[i][j].setBackground(missColor);
                    // player2Board[i][j].setForeground(Color.WHITE);
                }
            }
        }
    }

private void handleShot(int x, int y) {
    if (player1Turn) {
        if (player2Board[x][y].getBackground() != oceanColor) {
            showError("You've already targeted this location!");
            return;
        }

        if (player2Ships[x][y]) {
            player2Board[x][y].setBackground(hitColor);
            player2Board[x][y].setText("💥");
            player2Ships[x][y] = false;
            player1Hits++;
            totalPlayer2Ships--;
        } else {
            player2Board[x][y].setBackground(missColor);
            player2Board[x][y].setText("🌊");
            player1Misses++;
        }

        player2Board[x][y].setEnabled(false);
        updateStats();

        if (checkWin()) return;

        if (vsComputer) {
            player1Turn = false;
            turnLabel.setText("Enemy's Turn ⏳");

            Timer timer = new Timer(1500, _ -> {
                enemyMove();
                player1Turn = true;
                turnLabel.setText("Player 1's Turn ⚔");
                updateStats();
                checkWin();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            player1Turn = false;
            turnLabel.setText("Player 2's Turn ⚔");
            startGame();
        }
    } else {
        if (player1Board[x][y].getBackground() != oceanColor) {
            showError("You've already targeted this location!");
            return;
        }

        if (player1Ships[x][y]) {
            player1Board[x][y].setBackground(hitColor);
            player1Board[x][y].setText("💥");
            player1Ships[x][y] = false;
            player2Hits++;
            totalPlayer1Ships--;
        } else {
            player1Board[x][y].setBackground(missColor);
            player1Board[x][y].setText("🌊");
            player2Misses++;
        }

        player1Board[x][y].setEnabled(false);
        updateStats();

        if (checkWin()) return;

        player1Turn = true;
        turnLabel.setText("Player 1's Turn ⚔");
        startGame();
    }
}

    private void enemyMove() {
        Random rand = new Random();
        boolean moved = false;

        while (!moved) {
            int x = rand.nextInt(GRID_SIZE);
            int y = rand.nextInt(GRID_SIZE);

            if (player1Board[x][y].getBackground() == oceanColor) {
                if (player1Ships[x][y]) {
                    player1Board[x][y].setBackground(hitColor);
                    player1Board[x][y].setText("💥");
                    player1Ships[x][y] = false;
                    player2Hits++;
                    totalPlayer1Ships--;
                } else {
                    player1Board[x][y].setBackground(missColor);
                    player1Board[x][y].setText("🌊");
                    player2Misses++;
                }

                player1Board[x][y].setEnabled(false);
                moved = true;
            }
        }
    }

    private void updateStats() {
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 16);

        player1Stats.setFont(emojiFont);
        player2Stats.setFont(emojiFont);

        player1Stats.setText("Player 1 Hits: " + player1Hits + " 💥 | Misses: " + player1Misses + " 🌊");
        player2Stats.setText((vsComputer ? "Enemy" : "Player 2") + " Hits: " + player2Hits + " 💥 | Misses: "
                + player2Misses + " 🌊");
    }

    private boolean checkWin() {
        if (totalPlayer2Ships <= 0) {
            showVictory("Player 1 Wins! 🎉", "Player 1 Score: " + player1Hits + " hits, " + player1Misses + " misses");
            return true;
        } else if (totalPlayer1Ships <= 0) {
            showDefeat("Game Over! " + (vsComputer ? "Enemy" : "Player 2") + " Wins! 💀",
                    (vsComputer ? "Enemy" : "Player 2") + " Score: " + player2Hits + " hits, " + player2Misses
                            + " misses");
            return true;
        }
        return false;
    }

    private void showVictory(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        askRestart();
    }

    private void showDefeat(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        askRestart();
    }

    private void askRestart() {
        int choice = JOptionPane.showConfirmDialog(this, "Would you like to play again?", "Game Over",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    private void resetGame() {
        player1Hits = player1Misses = player2Hits = player2Misses = 0;
        player1Turn = true;
        shipsPlaced = 0;
        currentShipSize = shipSizes[0];
        player1PlacementComplete = false;
        player2PlacementComplete = false;
        getContentPane().removeAll();

        stopBackgroundMusic();
        player1Hits = player1Misses = player2Hits = player2Misses = 0;
        player1Turn = true;
        shipsPlaced = 0;
        currentShipSize = shipSizes[0];
        player1PlacementComplete = false;
        player2PlacementComplete = false;
        getContentPane().removeAll();
        setupMainMenu();
        playBackgroundMusic();

    }

    public static void main(String[] args) {
        try {
            UIManager.put("Label.foreground", Color.BLACK);
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("TitledBorder.titleColor", Color.BLACK);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            BattleshipGame game = new BattleshipGame();
            game.setVisible(true);
        });
    }
}