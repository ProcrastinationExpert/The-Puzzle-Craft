package main;
import controller.AuthResponse;

import javax.swing.JFrame;

import utils.*;
import controller.AuthService;
import controller.DatabaseService;
import controller.FirebaseAuthService;
import controller.FirestoreDatabaseService;
import javax.swing.*;
import java.awt.Color;
import model.*;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.RenderingHints;
import java.awt.GradientPaint;

import gameLogic.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Andharu Utomo, Muhammad Azzami Yahya, Audi Devina Dewi
 */
public class Launcher extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Launcher.class.getName());

    private final AuthService authService;
    private final DatabaseService databaseService;
    
    private String usernameText, passwordText;
    
    public Launcher(AuthService authService, DatabaseService databaseService) {
        this.authService = authService;
        this.databaseService = databaseService;
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        switchPanelToLogin(); // awalnya panel login dlu
    }
    
    // GANTI-GANTI CARD LAYOUT
    
    private void switchPanelToLogin() {
        AccountConditionCardPanel.removeAll();
        AccountConditionCardPanel.add(UserLoginPanel);
        AccountConditionCardPanel.repaint();
        AccountConditionCardPanel.revalidate();
        playBtn.setEnabled(false);
        levelEditorBtn.setEnabled(false);
    }
    
    private void switchPanelToLoggedIn() {
        AccountConditionCardPanel.removeAll();
        AccountConditionCardPanel.add(UserLoggedInPanel);
        AccountConditionCardPanel.repaint();
        AccountConditionCardPanel.revalidate();
        setLabelFromUserData();
        playBtn.setEnabled(true);
        levelEditorBtn.setEnabled(true);
        
    }
    
    private void switchPanelLogin() {
        if (SessionManager.isSessionActive()) {
            switchPanelToLoggedIn();
        } else {
            switchPanelToLogin();
        }
    }
    
    // MASUKIN DATA PANEL DARI DATA AKUN
    
    private void loadDataUserToLoggedInToLabel() {
        // biar bisa jalan sendiri (jadi yang lain ga usah nunggu)
        SwingWorker<UserAccountDescriptionData, Void> dataWorker = new SwingWorker<UserAccountDescriptionData, Void>() {

            @Override
            protected UserAccountDescriptionData doInBackground() throws Exception {
                // Ambil data sesi
                String idToken = SessionManager.getIdToken();
                String uid = SessionManager.getUid();

                if (idToken == null) {
                    return null; // Pengaman
                }
                // Panggil DatabaseController dari background
                return databaseService.getUserData(idToken, uid);
            }

            @Override
            protected void done() {
                try {
                    UserAccountDescriptionData data = get(); // Ambil hasil

                    if (data != null) {
                        // SUKSES: Update semua label di UI
                        loggedInLabel.setForeground(new Color(0, 102,51));
                        loggedInLabel.setText("You're logged in.");

                        // (Anda bisa format 'lastPlayed' nanti)
                        lastPlayedLoggedInLabel.setText("Last played : " + data.getLastPlayed());

                        lastLevelLoggedInLabel.setText("Last level : " + data.getLastLevel());
                        
                        Integer score = data.getScore();
                        scoreLabel.setText("Score : " + (score == null ? 0 : score));

                    } else {
                        // GAGAL
                        loggedInLabel.setForeground(new Color(255, 51, 51));
                        loggedInLabel.setText("FAILED TO LOAD USER DATA. PLEASE RELOGIN!");
                        lastPlayedLoggedInLabel.setText("Last played : ?");
                        lastLevelLoggedInLabel.setText("Last level : ?");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loggedInLabel.setForeground(new Color(255, 51, 51));
                    loggedInLabel.setText("Error: " + e.getMessage());
                }
            }
        };

        dataWorker.execute();
    }
    
    private void setLabelFromUserData() {
        if (SessionManager.isSessionActive()) {
            // buat ganti isi panel LoggedIn setelah player login
            loggedInLabel.setForeground(new Color(64, 64, 64));
            loggedInLabel.setText("Loading game data....");
            
            usernameLoggedInLabel.setText("Username : " + SessionManager.getUsername());
            lastPlayedLoggedInLabel.setText("Last played : (loading...)" );
            lastLevelLoggedInLabel.setText("Last level : (loading...)" );
            
            loadDataUserToLoggedInToLabel();
        }
    }
    
    private void logoutAccount() {
        SessionManager.clearSession();
        switchPanelLogin();
    }
    
    // UPDATE DATA LAST TIME PLAYED DAN DATA LEVEL

    public void updateLastLevelPlayedData(String namaLevel) { // level terakhir user
        if (!SessionManager.isSessionActive()) {
            JOptionPane.showMessageDialog(null, "Cannot save data user last level played: user has logout");
            return;
        }

        // Ambil data sesi dengan mudah
        String token = SessionManager.getIdToken();
        String userId = SessionManager.getUid();

        // Panggil database (di background thread)
        new Thread(() -> {
            databaseService.updateUserLastLevel(token, userId, namaLevel);
        }).start();

    }
    
    public void updateLastTimePlayedDataToLatest() { // waktu terakhir user bermain
        if (!SessionManager.isSessionActive()) {
            JOptionPane.showMessageDialog(null, "Cannot save data user last time played: user has logout");
            return;
        }
        
        // Ambil data sesi dengan mudah
        String token = SessionManager.getIdToken();
        String userId = SessionManager.getUid();

        // Panggil database (di background thread)
        new Thread(() -> {
            databaseService.updateUserLastPlayed(token, userId);
        }).start();
        
    }
    
    // FUNGSI UNTUK MEMULAI / SAAT GAME SELESAI
    
    private void startGame() {
        if (SessionManager.isSessionActive()) {
//            SelectLevelDialog selectLevelDialog = new SelectLevelDialog(this, true, this);
//            selectLevelDialog.setLocationRelativeTo(this);
//            selectLevelDialog.setVisible(true);
              Create create = new Create();
              LevelSelectorFrame.show(SessionManager.getUsername(), create, this);
        } else {
            JOptionPane.showMessageDialog(this, "Cannot play the game: there is no logged in user");
        }
    }
    
    public void updateDashboard() {
        updateLastTimePlayedDataToLatest();
        setLabelFromUserData();
    }
    
    // Dipanggil oleh gameLogic ketika level selesai
    public void onLevelCompleted() {
        if (!SessionManager.isSessionActive()) {
            JOptionPane.showMessageDialog(this, "Cannot update dashboard: user is not logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setLoading(true); // Disable UI temporarily

        SwingWorker<Integer, Void> scoreUpdateWorker = new SwingWorker<Integer, Void>() {
            private Integer newScore = 0;

            @Override
            protected Integer doInBackground() throws Exception {
                String idToken = SessionManager.getIdToken();
                String uid = SessionManager.getUid();
                UserAccountDescriptionData currentUserData = databaseService.getUserData(idToken, uid); // Get latest data

                Integer currentScore = currentUserData.getScore();
                newScore = (currentScore == null ? 0 : currentScore) + 1;

                boolean success = databaseService.updateUserScore(idToken, uid, newScore);
                
                if (!success) {
                    throw new Exception("Failed to update score in database.");
                }
                return newScore;
            }

            @Override
            protected void done() {
                try {
                    Integer finalScore = get();
                    // Update UI on EDT
                    scoreLabel.setText("Score : " + finalScore);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Launcher.this, "Error updating score: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setLoading(false); // Re-enable UI
                }
            }
        };
        scoreUpdateWorker.execute();
    }
    
    // BUAT BUTTON DAN TEXT FIELD TIDAK BISA DIPENCET SEMENTARA SAAT PROGRAM SEDANG LOADING
    
    /**
     * Method helper untuk mengaktifkan/menonaktifkan UI saat loading.
     */
    private void setLoading(boolean loading) {
        // Atur aktivasi semua field
        usernameTxt.setEnabled(!loading);
        passwordTxt.setEnabled(!loading);

        // Atur akktivasi semua tombol
        loginBtn.setEnabled(!loading);
        registerBtn.setEnabled(!loading);
        resetPasswordBtn.setEnabled(!loading);
        levelEditorBtn.setEnabled(!loading);
        resendVerificationBtn.setEnabled(!loading);
        if (SessionManager.isSessionActive()) {
            playBtn.setEnabled(!loading);
        }
        changeAccountBtn.setEnabled(!loading);

        // Ubah kursor
        setCursor(loading ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR) : java.awt.Cursor.getDefaultCursor());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        AccountConditionPanel = new javax.swing.JPanel();
        AccountConditionCardPanel = new javax.swing.JPanel();
        UserLoginPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        usernameTxt = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        passwordTxt = new javax.swing.JPasswordField();
        resendVerificationBtn = new javax.swing.JButton();
        resetPasswordBtn = new javax.swing.JButton();
        loginBtn = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warna dasar [0,121,121] → warna lebih gelap & terang untuk efek hover/press
                Color normalTop = new Color(0, 150, 150);
                Color normalBottom = new Color(0, 121, 121);

                Color hoverTop = new Color(0, 170, 170);
                Color hoverBottom = new Color(0, 145, 145);

                Color pressTop = new Color(0, 110, 110);
                Color pressBottom = new Color(0, 90, 90);

                GradientPaint gradient;

                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, pressTop, 0, getHeight(), pressBottom);
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(0, 0, hoverTop, 0, getHeight(), hoverBottom);
                } else {
                    gradient = new GradientPaint(0, 0, normalTop, 0, getHeight(), normalBottom);
                }

                g2.setPaint(gradient);

                // Bentuk oval panjang (pill button)
                int arc = getHeight(); // supaya ujungnya bulat penuh
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                super.paintComponent(g);
                g2.dispose();
            }
        }
        ;
        registerBtn = new javax.swing.JButton();
        SeparatorLine = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        UserLoggedInPanel = new javax.swing.JPanel();
        changeAccountBtn = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warna dasar [200,50,50]
                Color normalTop = new Color(200, 50, 50);
                Color normalBottom = new Color(170, 40, 40);

                // Hover → lebih terang
                Color hoverTop = new Color(220, 70, 70);
                Color hoverBottom = new Color(190, 60, 60);

                // Pressed → lebih gelap
                Color pressTop = new Color(150, 30, 30);
                Color pressBottom = new Color(120, 25, 25);

                GradientPaint gradient;

                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, pressTop, 0, getHeight(), pressBottom);
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(0, 0, hoverTop, 0, getHeight(), hoverBottom);
                } else {
                    gradient = new GradientPaint(0, 0, normalTop, 0, getHeight(), normalBottom);
                }

                g2.setPaint(gradient);

                int arc = 25; // Rounded corner
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        loggedInLabel = new javax.swing.JLabel();
        usernameLoggedInLabel = new javax.swing.JLabel();
        playerIconLabel = new javax.swing.JLabel();
        lastLevelLoggedInLabel = new javax.swing.JLabel();
        lastPlayedLoggedInLabel = new javax.swing.JLabel();
        scoreLabel = new javax.swing.JLabel();
        PlayGamePanel = new javax.swing.JPanel();
        playBtn = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warna dasar [0,121,121] → warna lebih gelap & terang untuk efek hover/press
                Color normalTop = new Color(0, 150, 150);
                Color normalBottom = new Color(0, 121, 121);

                Color hoverTop = new Color(0, 170, 170);
                Color hoverBottom = new Color(0, 145, 145);

                Color pressTop = new Color(0, 110, 110);
                Color pressBottom = new Color(0, 90, 90);

                GradientPaint gradient;

                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, pressTop, 0, getHeight(), pressBottom);
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(0, 0, hoverTop, 0, getHeight(), hoverBottom);
                } else {
                    gradient = new GradientPaint(0, 0, normalTop, 0, getHeight(), normalBottom);
                }

                g2.setPaint(gradient);

                // Bentuk oval panjang (pill button)
                int arc = 15; // supaya ujungnya bulat penuh
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        levelStatusLabel1 = new javax.swing.JLabel();
        NewLevelPanel = new javax.swing.JPanel();
        levelEditorBtn = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warna dasar [0,121,121] → warna lebih gelap & terang untuk efek hover/press
                Color normalTop = new Color(0, 150, 150);
                Color normalBottom = new Color(0, 121, 121);

                Color hoverTop = new Color(0, 170, 170);
                Color hoverBottom = new Color(0, 145, 145);

                Color pressTop = new Color(0, 110, 110);
                Color pressBottom = new Color(0, 90, 90);

                GradientPaint gradient;

                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, pressTop, 0, getHeight(), pressBottom);
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(0, 0, hoverTop, 0, getHeight(), hoverBottom);
                } else {
                    gradient = new GradientPaint(0, 0, normalTop, 0, getHeight(), normalBottom);
                }

                g2.setPaint(gradient);

                // Bentuk oval panjang (pill button)
                int arc = 15; // supaya ujungnya bulat penuh
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        levelStatusLabel = new javax.swing.JLabel();
        CoverPanel = new javax.swing.JPanel();
        pictureLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        background.setBackground(new java.awt.Color(186, 238, 218));

        AccountConditionPanel.setBackground(new java.awt.Color(255, 255, 255));
        AccountConditionPanel.setBorder(
            javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(10,10,10,10),
                javax.swing.BorderFactory.createLineBorder(
                    new java.awt.Color(225,225,225), 1, true
                )
            )
        );

        AccountConditionCardPanel.setBackground(new java.awt.Color(255, 255, 255));
        AccountConditionCardPanel.setLayout(new java.awt.CardLayout());

        UserLoginPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("You need to login first");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(40, 40, 40));
        jLabel2.setText("Username");

        usernameTxt.setText("Enter your username");
        usernameTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                usernameTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                usernameTxtFocusLost(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(40, 40, 40));
        jLabel17.setText("Password");

        passwordTxt.setEchoChar((char)0); // biar placeholder kelihatan
        passwordTxt.setText("Enter your password");
        passwordTxt.setForeground(new java.awt.Color(153,153,153));
        passwordTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordTxtFocusLost(evt);
            }
        });
        passwordTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passwordTxtKeyPressed(evt);
            }
        });

        resendVerificationBtn.setFont(new java.awt.Font("Segoe UI Semibold", 2, 10)); // NOI18N
        resendVerificationBtn.setForeground(new java.awt.Color(200, 50, 50));
        resendVerificationBtn.setText("Didn’t get the verification email?");
        resendVerificationBtn.setBorderPainted(false);
        resendVerificationBtn.setContentAreaFilled(false);
        resendVerificationBtn.setFocusPainted(false);
        // Cursor tangan (hyperlink)
        resendVerificationBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        resendVerificationBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                resendVerificationBtn.setForeground(new java.awt.Color(230, 100, 100)); // lebih terang saat hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                resendVerificationBtn.setForeground(new java.awt.Color(200, 80, 80)); // balik normal
            }
        });
        resendVerificationBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        resendVerificationBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resendVerificationBtnActionPerformed(evt);
            }
        });

        resetPasswordBtn.setFont(new java.awt.Font("Segoe UI Semibold", 2, 10)); // NOI18N
        resetPasswordBtn.setForeground(new java.awt.Color(200, 50, 50));
        resetPasswordBtn.setText("Forgot Password?");
        resetPasswordBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        // Hilangkan tampilan button → jadi mirip hyperlink
        resetPasswordBtn.setBorderPainted(false);
        resetPasswordBtn.setContentAreaFilled(false);
        resetPasswordBtn.setFocusPainted(false);
        resetPasswordBtn.setOpaque(false);

        // Cursor tangan (hyperlink)
        resetPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        resetPasswordBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                resetPasswordBtn.setForeground(new java.awt.Color(230, 100, 100)); // lebih terang saat hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                resetPasswordBtn.setForeground(new java.awt.Color(200, 80, 80)); // balik normal
            }
        });
        resetPasswordBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPasswordBtnActionPerformed(evt);
            }
        });

        loginBtn.setBackground(new java.awt.Color(255, 102, 102));
        loginBtn.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        loginBtn.setForeground(new java.awt.Color(228, 231, 255));
        loginBtn.setContentAreaFilled(false);
        loginBtn.setLabel("Login");
        loginBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        loginBtn.setOpaque(false);
        loginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginBtnActionPerformed(evt);
            }
        });

        registerBtn.setFont(new java.awt.Font("Segoe UI Semibold", 3, 12)); // NOI18N
        registerBtn.setForeground(new java.awt.Color(0, 102, 102));
        registerBtn.setText("Register");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(false);

        // Cursor tangan (hyperlink feel)
        registerBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Hover effect (lebih terang)
        registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                registerBtn.setForeground(new java.awt.Color(0,140,140));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                registerBtn.setForeground(new java.awt.Color(0,102,102));
            }
        });
        registerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerBtnActionPerformed(evt);
            }
        });

        SeparatorLine.setText("————— Or  —————");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel3.setText("Don't have an account?");

        javax.swing.GroupLayout UserLoginPanelLayout = new javax.swing.GroupLayout(UserLoginPanel);
        UserLoginPanel.setLayout(UserLoginPanelLayout);
        UserLoginPanelLayout.setHorizontalGroup(
            UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UserLoginPanelLayout.createSequentialGroup()
                .addGap(0, 122, Short.MAX_VALUE)
                .addGroup(UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserLoginPanelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(SeparatorLine))
                    .addGroup(UserLoginPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, 0)
                        .addComponent(registerBtn))
                    .addGroup(UserLoginPanelLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(120, 120, 120))
            .addGroup(UserLoginPanelLayout.createSequentialGroup()
                .addGroup(UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserLoginPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UserLoginPanelLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(passwordTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(resetPasswordBtn)
                            .addGroup(UserLoginPanelLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(usernameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(UserLoginPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(resendVerificationBtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        UserLoginPanelLayout.setVerticalGroup(
            UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserLoginPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(resendVerificationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(resetPasswordBtn)
                .addGap(34, 34, 34)
                .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(SeparatorLine)
                .addGap(18, 18, 18)
                .addGroup(UserLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        AccountConditionCardPanel.add(UserLoginPanel, "card2");

        UserLoggedInPanel.setBackground(new java.awt.Color(255, 255, 255));

        changeAccountBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 10)); // NOI18N
        changeAccountBtn.setForeground(new java.awt.Color(228, 231, 255));
        changeAccountBtn.setText("Logout");
        changeAccountBtn.setBorderPainted(false);
        changeAccountBtn.setContentAreaFilled(false);
        changeAccountBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        changeAccountBtn.setOpaque(false);
        changeAccountBtn.setFocusPainted(false);
        changeAccountBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeAccountBtnActionPerformed(evt);
            }
        });

        loggedInLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        loggedInLabel.setForeground(new java.awt.Color(0, 102, 51));
        loggedInLabel.setText("You're logged in!");

        usernameLoggedInLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        usernameLoggedInLabel.setText("Username : ?");

        playerIconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        playerIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/levelAssets/walk1.png"))); // NOI18N

        lastLevelLoggedInLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lastLevelLoggedInLabel.setText("Last level : ?");

        lastPlayedLoggedInLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lastPlayedLoggedInLabel.setText("Last played : ?");

        scoreLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        scoreLabel.setText("Score : ?");

        PlayGamePanel.setBackground(new java.awt.Color(255, 255, 255));

        playBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        playBtn.setForeground(new java.awt.Color(228, 231, 255));
        playBtn.setText("Play Game");
        playBtn.setBorderPainted(false);
        playBtn.setContentAreaFilled(false);
        playBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        playBtn.setOpaque(false);
        playBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBtnActionPerformed(evt);
            }
        });

        levelStatusLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        levelStatusLabel1.setText("Start the game!");

        javax.swing.GroupLayout PlayGamePanelLayout = new javax.swing.GroupLayout(PlayGamePanel);
        PlayGamePanel.setLayout(PlayGamePanelLayout);
        PlayGamePanelLayout.setHorizontalGroup(
            PlayGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayGamePanelLayout.createSequentialGroup()
                .addGroup(PlayGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PlayGamePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(playBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PlayGamePanelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(levelStatusLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PlayGamePanelLayout.setVerticalGroup(
            PlayGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayGamePanelLayout.createSequentialGroup()
                .addComponent(levelStatusLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        NewLevelPanel.setBackground(new java.awt.Color(255, 255, 255));

        levelEditorBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        levelEditorBtn.setForeground(new java.awt.Color(228, 231, 255));
        levelEditorBtn.setBorderPainted(false);
        levelEditorBtn.setContentAreaFilled(false);
        levelEditorBtn.setLabel("New Level");
        levelEditorBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        levelEditorBtn.setOpaque(false);
        levelEditorBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelEditorBtnActionPerformed(evt);
            }
        });

        levelStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        levelStatusLabel.setText("Create your own level!");

        javax.swing.GroupLayout NewLevelPanelLayout = new javax.swing.GroupLayout(NewLevelPanel);
        NewLevelPanel.setLayout(NewLevelPanelLayout);
        NewLevelPanelLayout.setHorizontalGroup(
            NewLevelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewLevelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(levelStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(NewLevelPanelLayout.createSequentialGroup()
                .addComponent(levelEditorBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        NewLevelPanelLayout.setVerticalGroup(
            NewLevelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewLevelPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(levelStatusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelEditorBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout UserLoggedInPanelLayout = new javax.swing.GroupLayout(UserLoggedInPanel);
        UserLoggedInPanel.setLayout(UserLoggedInPanelLayout);
        UserLoggedInPanelLayout.setHorizontalGroup(
            UserLoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserLoggedInPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(UserLoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(UserLoggedInPanelLayout.createSequentialGroup()
                        .addGroup(UserLoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lastLevelLoggedInLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lastPlayedLoggedInLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(playerIconLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(loggedInLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(usernameLoggedInLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scoreLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(61, 61, 61)
                        .addComponent(changeAccountBtn))
                    .addGroup(UserLoggedInPanelLayout.createSequentialGroup()
                        .addComponent(NewLevelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PlayGamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        UserLoggedInPanelLayout.setVerticalGroup(
            UserLoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserLoggedInPanelLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(UserLoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loggedInLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeAccountBtn))
                .addGap(3, 3, 3)
                .addComponent(playerIconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameLoggedInLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastPlayedLoggedInLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastLevelLoggedInLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scoreLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(UserLoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(NewLevelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PlayGamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );

        AccountConditionCardPanel.add(UserLoggedInPanel, "card3");

        javax.swing.GroupLayout AccountConditionPanelLayout = new javax.swing.GroupLayout(AccountConditionPanel);
        AccountConditionPanel.setLayout(AccountConditionPanelLayout);
        AccountConditionPanelLayout.setHorizontalGroup(
            AccountConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountConditionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AccountConditionCardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AccountConditionPanelLayout.setVerticalGroup(
            AccountConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountConditionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AccountConditionCardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        CoverPanel.setBackground(new java.awt.Color(186, 238, 218));

        pictureLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/cover/THE PUZZLE CRAFT COVER(4)(2).png"))); // NOI18N

        javax.swing.GroupLayout CoverPanelLayout = new javax.swing.GroupLayout(CoverPanel);
        CoverPanel.setLayout(CoverPanelLayout);
        CoverPanelLayout.setHorizontalGroup(
            CoverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pictureLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        CoverPanelLayout.setVerticalGroup(
            CoverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CoverPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(pictureLabel))
        );

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundLayout.createSequentialGroup()
                .addComponent(CoverPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(AccountConditionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CoverPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AccountConditionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void usernameTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usernameTxtFocusGained
        // TODO add your handling code here:
        if (usernameTxt.getText().equals("Enter your username")){
            usernameTxt.setText("");
            usernameTxt.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_usernameTxtFocusGained

    private void usernameTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usernameTxtFocusLost
        // TODO add your handling code here:
        if (usernameTxt.getText().equals("")){
            usernameTxt.setText("Enter your username");
            usernameTxt.setForeground(new Color (153, 153, 153));
        }
    }//GEN-LAST:event_usernameTxtFocusLost

    private void playBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBtnActionPerformed
        // TODO add your handling code here:
        startGame();
    }//GEN-LAST:event_playBtnActionPerformed

    // ini buat ngecek validitas data buat login
    private boolean isLoginDataFieldValid() {
        usernameText = usernameTxt.getText();
        passwordText = new String(passwordTxt.getPassword());
        System.out.println("Username: " + usernameText);
        System.out.println("Password: " + passwordText);
        
        if (ValidationUtils.isEmpty(usernameText) || ValidationUtils.isEmpty(passwordText)) {
            JOptionPane.showMessageDialog(this, "Please input all of the fields", "Error Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isValidUsername(usernameText)) {
            JOptionPane.showMessageDialog(this, "Username format is not valid.", "Error Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isValidPassword(passwordText)) {
            JOptionPane.showMessageDialog(this, "Password format is not valid.", "Error Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void login() {
        // cek dlu apakah data yang dimasukkan user itu valid
        if (!isLoginDataFieldValid()) {
            return;
        }

        // dari sini udah aman sih harusnya
        setLoading(true);

        SwingWorker<AuthResponse, Void> loginWorker = new SwingWorker<AuthResponse, Void>() {

            @Override
            protected AuthResponse doInBackground() throws Exception {

                // lookup email
                String email = databaseService.getEmailFromUsername(usernameText);

                if (email == null) {
                    // Username tidak ditemukan di database
                    return null;
                }

                LoginData loginData = new LoginData(email, usernameText, passwordText);

                // login dengan menggunakna loginData yang telah diisi user
                return authService.login(loginData);
            }

            @Override
            protected void done() {
                try {
                    AuthResponse response = get(); // ambil hasil

                    if (response == null) {
                        System.err.println("Akun tidak ditemukan.");
                        throw new Exception("INVALID_CREDENTIALS");
                    }

                    if (response.isEmailVerified()) {
                        // jika email terverifikasi artinya login berhasil

                        SessionManager.startSession(response, usernameText);

                        // ganti panel card
                        switchPanelLogin();
                    } else {
                        // jika email belum terverifikasi

                        JOptionPane.showMessageDialog(Launcher.this,
                                "Login Failed: Your account is not verified. Please find a verification link in inbox/spam folder of your email",
                                "Failed verification", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e) {

                    Throwable cause = e.getCause();

                    String errorMessage = (cause != null) ? cause.getMessage() : e.getMessage();
                    if (errorMessage != null && errorMessage.contains("INVALID_CREDENTIALS")) {
                        JOptionPane.showMessageDialog(Launcher.this,
                                "Login Failed: Wrong username or password",
                                "Login Failed", JOptionPane.ERROR_MESSAGE);

                    } else {
                        // Error umum
                        JOptionPane.showMessageDialog(Launcher.this,
                                "Error occurred: " + errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } finally {
                    setLoading(false);
                }
            }
        };

        loginWorker.execute();

    }
    
    private void loginBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginBtnActionPerformed

        login();
    }//GEN-LAST:event_loginBtnActionPerformed

    private void registerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerBtnActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        RegisterDialog regDialog = new RegisterDialog(this, true, authService, databaseService);
        regDialog.setVisible(true);
    }//GEN-LAST:event_registerBtnActionPerformed

    private void resetPasswordBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPasswordBtnActionPerformed
        // TODO add your handling code here:
        int resetConfirmation = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to reset your password?", "Reset password confirmation", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (resetConfirmation == javax.swing.JOptionPane.YES_OPTION) {
            ResetPasswordDialog regDialog = new ResetPasswordDialog(this, true, authService);
            regDialog.setVisible(true);
        }
    }//GEN-LAST:event_resetPasswordBtnActionPerformed

    private void changeAccountBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeAccountBtnActionPerformed
        // TODO add your handling code here:
        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/assets/images/logo/warning.png"));

Image scaled = rawIcon.getImage().getScaledInstance(70, 70, java.awt.Image.SCALE_SMOOTH);
ImageIcon smallIcon = new ImageIcon(scaled);

int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Logout Confirmation",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        smallIcon
);


    if (confirm == JOptionPane.YES_OPTION) {
        logoutAccount();
    }
    }//GEN-LAST:event_changeAccountBtnActionPerformed

    private void resendVerificationBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resendVerificationBtnActionPerformed
        // cek dlu apakah data yang dimasukkan user itu valid
        if (!isLoginDataFieldValid()) return;
        
        setLoading(true);
        
        // Buat SwingWorker BARU
        SwingWorker<AuthResponse, Void> resendWorker = new SwingWorker<AuthResponse, Void>() {

            @Override
            protected AuthResponse doInBackground() throws Exception {
                // lookup email
                String email = databaseService.getEmailFromUsername(usernameText);
                if (email == null) {
                    throw new Exception("INVALID_CREDENTIALS");
                }

                LoginData loginData = new LoginData(email, usernameText, passwordText);

                // Panggil 'login()' (yang sudah direfactor)
                return authService.login(loginData);
            }

            @Override
            protected void done() {
                try {
                    AuthResponse response = get(); // Ambil hasil

                    // cek status verifikasi
                    if (response.isEmailVerified()) {
                        JOptionPane.showMessageDialog(Launcher.this,
                                "You are already verified. You can login now.",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // jika belum terverifikasi
                        new Thread(() -> {
                            try {
                                authService.sendVerificationEmail(response.getIdToken());
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(Launcher.this,
                                            "Resend Verificaton has been sent to your email.",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                });
                            } catch (Exception ex) {
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(Launcher.this,
                                            "Resend Verificaton Failed: " + ex.getMessage(),
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                });
                            }
                        }).start();
                    }

                } catch (Exception e) {
                    // gagal (kemungkinan besar password salah)
                    Throwable cause = e.getCause();
                    String errorMessage = (cause != null) ? cause.getMessage() : e.getMessage();

                    if ("INVALID_CREDENTIALS".equals(errorMessage)) {
                        JOptionPane.showMessageDialog(Launcher.this,
                                "Resend Verificaton Failed: Wrong Username or Password.",
                                "Resend Verificaton Failed", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(Launcher.this,
                                "Terjadi error: " + errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } finally {
                    setLoading(false);
                }
            }
        };

        resendWorker.execute();
    }//GEN-LAST:event_resendVerificationBtnActionPerformed

    private void levelEditorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_levelEditorBtnActionPerformed
        // TODO add your handling code here:
        NewLevelInput.show(SessionManager.getUsername());
//        String currentUser = SessionManager.getUsername();
//
//        // cek user apakah sudah pernah membuat level
//        boolean hasMadeLevel = LevelChecker.isUserHasLevels(currentUser);
//        if (!hasMadeLevel) {
//            CreateNewLevelDialog createLevelDialog = new CreateNewLevelDialog(this, true);
//            createLevelDialog.setLocationRelativeTo(this);
//            createLevelDialog.setVisible(true);
//            return;
//        }
//
//        String levelName = LevelChecker.getLevelNameByUsername(SessionManager.getUsername());
//
//
//        gameLogic.Create create = new gameLogic.Create();
//
//        RunEditor.build(create, levelName, ReadData.getLayerData(levelName, 1), ReadData.getLayerData(levelName, 2), currentUser, GetUser.getUsername(levelName));
//        
    }//GEN-LAST:event_levelEditorBtnActionPerformed

    private void passwordTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordTxtFocusGained
        // TODO add your handling code here:
        if (String.valueOf(passwordTxt.getPassword()).equals("Enter your password")) {
            passwordTxt.setText("");
            passwordTxt.setEchoChar('•');
            passwordTxt.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_passwordTxtFocusGained

    private void passwordTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordTxtFocusLost
        // TODO add your handling code here:
        if (String.valueOf(passwordTxt.getPassword()).equals("")){
            passwordTxt.setText("Enter your password");
            passwordTxt.setEchoChar((char) 0);
            passwordTxt.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_passwordTxtFocusLost

    private void passwordTxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordTxtKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode()== java.awt.event.KeyEvent.VK_ENTER) {
            login();
        }
    }//GEN-LAST:event_passwordTxtKeyPressed

    
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            AuthService authService = new FirebaseAuthService();
            DatabaseService databaseService = new FirestoreDatabaseService();
            new Launcher(authService, databaseService).setVisible(true);
        });
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccountConditionCardPanel;
    private javax.swing.JPanel AccountConditionPanel;
    private javax.swing.JPanel CoverPanel;
    private javax.swing.JPanel NewLevelPanel;
    private javax.swing.JPanel PlayGamePanel;
    private javax.swing.JLabel SeparatorLine;
    private javax.swing.JPanel UserLoggedInPanel;
    private javax.swing.JPanel UserLoginPanel;
    private javax.swing.JPanel background;
    private javax.swing.JButton changeAccountBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lastLevelLoggedInLabel;
    private javax.swing.JLabel lastPlayedLoggedInLabel;
    private javax.swing.JButton levelEditorBtn;
    private javax.swing.JLabel levelStatusLabel;
    private javax.swing.JLabel levelStatusLabel1;
    private javax.swing.JLabel loggedInLabel;
    private javax.swing.JButton loginBtn;
    private javax.swing.JPasswordField passwordTxt;
    private javax.swing.JLabel pictureLabel;
    private javax.swing.JButton playBtn;
    private javax.swing.JLabel playerIconLabel;
    private javax.swing.JButton registerBtn;
    private javax.swing.JButton resendVerificationBtn;
    private javax.swing.JButton resetPasswordBtn;
    private javax.swing.JLabel scoreLabel;
    private javax.swing.JLabel usernameLoggedInLabel;
    private javax.swing.JTextField usernameTxt;
    // End of variables declaration//GEN-END:variables
}
