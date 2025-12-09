/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package main;

/**
 *
 * @author User
 */

import controller.AuthService;
import controller.DatabaseService;
import javax.swing.JOptionPane;
import controller.AuthResponse;
import model.RegisterData;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.Cursor;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import utils.*;
public class RegisterDialog extends javax.swing.JDialog {
    
    // ini variabel bawaan dari apache netbeans
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegisterDialog.class.getName());

    private final AuthService authService;
    private final DatabaseService databaseService;
    
    private javax.swing.Timer typingTimer;
    
    private boolean isUsernameTaken = true;
    
    // properti buat nyimpen teks dari input field
    private String emailTextInput, usernameTextInput, passwordTextInput, repeatPasswordTextInput;

    // konstruktor
    public RegisterDialog(java.awt.Frame parent, boolean modal, AuthService authService, DatabaseService databaseService) {
        super(parent, modal);
        this.authService = authService;
        this.databaseService = databaseService;
        initComponents();
        setLocationRelativeTo(parent);
        addEmailTextListener();
        addUsernameTextListener();
        addPasswordTextListener();
        addRepeatPasswordTextListener();
        setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
    }
    
    // ini buat update property yang akan menyimpan variabel yang isinya berasal dari text field (email, username, password, dan repeat password)
    private void updateEmailProperty() {
        emailTextInput = emailTxt.getText();
    }
    
    private void updateUsernameProperty() {
        usernameTextInput = usernameTxt.getText();
    }
    
    private void updatePasswordProperty() {
        passwordTextInput = passwordTxt.getText();
    }
    
    private void updateRepeatPasswordProperty() {
        repeatPasswordTextInput = repeatPasswordTxt.getText();
    }
    
    private void handleTypingEmail() {
        updateEmailProperty();
        
        if (ValidationUtils.isEmpty(emailTextInput)) {
            emailStatusLabel.setText("Email can't be empty.");
        } else if (!ValidationUtils.isValidEmail(emailTextInput)) {
            emailStatusLabel.setText("<html>Please use the correct format of email (example@example.com).</html>");
        } else {
            emailStatusLabel.setText("Email is good.");
        }
    }
    
    private void handleTypingUsername() {
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }
        
        updateUsernameProperty();

        if (ValidationUtils.isEmpty(usernameTextInput)) {
            usernameStatusLabel.setText("Username can't be empty!");
            return;
        } else if (!ValidationUtils.isValidUsername(usernameTextInput)) {
            usernameStatusLabel.setText("Username needs at least " + AppConstants.MIN_USERNAME_LENGTH + " characters");
            return; 
        } else {
            usernameStatusLabel.setText("Checking username...");
        }

        // Setiap kali user ngetik : mulai ulang timer delay 3 detik
        typingTimer.start();
    }
    
    
    
    private void handleTypingPassword() {
        
        updatePasswordProperty();
        updateRepeatPasswordProperty();
        
        if (ValidationUtils.isEmpty(passwordTextInput)) {
            passwordStatusLabel.setText("Password can't be empty.");
        } else if (!ValidationUtils.isValidPassword(passwordTextInput)) {
            passwordStatusLabel.setText("Password needs at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters.");
        } else if (!ValidationUtils.isPasswordConfirmationValid(passwordTextInput, repeatPasswordTextInput)) {
            passwordStatusLabel.setText("<html>Please input the same password that have you typed on Repeat password field.</html>");
        } else {
            passwordStatusLabel.setText("Password is good.");
        }
    }
    
    // Fungsi pengecekan username (dipanggil setelah user berhenti mengetik 2 detik)
    private void checkUsername() {
        // ini dibuat swingworker biar bisa berjalan di background
        new javax.swing.SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return databaseService.isUsernameTaken(usernameTextInput);
            }

            @Override
            protected void done() {
                try {
                    boolean taken = get();
                    isUsernameTaken = taken;
                    if (taken) {
                        usernameStatusLabel.setText("Username is already taken.");
                    } else {
                        usernameStatusLabel.setText("You can use this username.");
                    }
                } catch (Exception e) {
                    usernameStatusLabel.setText("Error checking username.");
                }
            }
        }.execute();
    }
    
    private void addEmailTextListener() {
        emailTxt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingEmail();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingEmail();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingEmail();
            }
        });
        
        emailTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                handleTypingEmail();
            }
        });
    }

    private void addUsernameTextListener() {
        // Timer untuk menunda pengecekan username (3 detik setelah berhenti mengetik)
        typingTimer = new javax.swing.Timer(2000, e -> checkUsername());
        typingTimer.setRepeats(false);

        usernameTxt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingUsername();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingUsername();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingUsername();
            }
        });

        usernameTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                handleTypingUsername();
            }
        });
    }
    
    private void addPasswordTextListener() {
        passwordTxt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingPassword();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingPassword();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingPassword();
            }
        });
        
        passwordTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                handleTypingPassword();
            }
        });
    }
    
    private void addRepeatPasswordTextListener() {
        repeatPasswordTxt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingPassword();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingPassword();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleTypingPassword();
            }
        });
    }
    
    private void sendRegisterData(RegisterData regData) {
        javax.swing.JDialog sentDialog = new javax.swing.JDialog(this, "Status", false);
        sentDialog.setSize(350, 120);
        sentDialog.setLayout(new java.awt.BorderLayout());
        sentDialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        sentDialog.setLocationRelativeTo(this);

        setLoading(true);

        javax.swing.JLabel statusLabel = new javax.swing.JLabel("<html><center>Preparing...</center></html>");
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sentDialog.add(statusLabel, java.awt.BorderLayout.CENTER);

        sentDialog.setVisible(true);

        // Worker agar UI ga nge-freeze
        javax.swing.SwingWorker<String, Void> worker = new javax.swing.SwingWorker<>() {

            @Override
            protected String doInBackground() throws Exception {

                // cek username
                if (databaseService.isUsernameTaken(regData.getUsername())) {
                    throw new Exception("USERNAME_TAKEN");
                }

                // kirim response untuk verifikasi
                AuthResponse authResponse = authService.register(regData);

                // jika register sukses
                String uid = authResponse.getLocalId();
                String idToken = authResponse.getIdToken();

                // klaim Username
                if (!databaseService.claimUsername(idToken, uid, usernameTextInput, emailTextInput)) {
                    // Jika 'claim' gagal, kita lempar error
                    throw new Exception("USERNAME_CLAIM_FAILED");
                }

                // simpan Data User
                if (!databaseService.saveNewUser(idToken, uid, regData.getUsername(), regData.getEmail())) {
                    // Jika 'save' gagal, kita lempar error
                    throw new Exception("SAVE_USER_FAILED");
                }

                // SUCCES akan di-return jika semua langkah berhasil
                return "SUCCESS";
            }

            @Override
            protected void done() {
                try {
                    String result = get();

                    // Ini HANYA berjalan jika 'result' adalah "SUCCESS"
                    statusLabel.setText("<html><center>Registration successful!<br>Check your email inbox or spam to verify your email.</center></html>");

                    // Tutup dialog 2 detik setelah pesan sukses
                    new javax.swing.Timer(2000, e -> {
                        sentDialog.dispose();
                        setLoading(false);
                        RegisterDialog.this.dispose();
                    }).start();

                } catch (Exception e) {
                    // Menangkap SEMUA error yang di-lempar (throw)

                    Throwable cause = e.getCause();
                    String errorMessage = (cause != null) ? cause.getMessage() : e.getMessage();

                    // Cek "kode rahasia" kita
                    if ("USERNAME_TAKEN".equals(errorMessage)) {
                        statusLabel.setText("<html><center>Registration failed:<br>Username already taken.</center></html>");
                    } else if ("EMAIL_EXISTS_OR_WEAK_PASSWORD".equals(errorMessage)) {
                        statusLabel.setText("<html><center>Registration failed:<a'br>Email is already in use or password is too weak.</center></html>");
                    } else if ("USERNAME_CLAIM_FAILED".equals(errorMessage)) {
                        statusLabel.setText("<html><center>Registration failed:<br>Could not claim username (race condition).</center></html>");
                    } else if ("SAVE_USER_FAILED".equals(errorMessage)) {
                        statusLabel.setText("<html><center>Registration failed:<br>Could not save user data.</center></html>");
                    } else {
                        // Error umum
                        statusLabel.setText("<html><center>An unknown error occurred:<br>" + errorMessage + "</center></html>");
                        e.printStackTrace();
                    }

                    new javax.swing.Timer(3000, ev -> {
                        sentDialog.dispose();
                        setLoading(false);
                    }).start();
                }
            }
        };

        worker.execute(); // jalankan worker di background
    }

    
    /**
     * Method helper untuk mengaktifkan/menonaktifkan UI saat loading.
     */
    private void setLoading(boolean loading) {
        // Atur aktivasi semua field
        emailTxt.setEnabled(!loading);
        usernameTxt.setEnabled(!loading);
        passwordTxt.setEnabled(!loading);
        repeatPasswordTxt.setEnabled(!loading);
        
        // Atur akktivasi semua tombol
        createBtn.setEnabled(!loading);
        cancelBtn.setEnabled(!loading);
        
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

        registerLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        emailTxt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        usernameTxt = new javax.swing.JTextField();
        passwordTxt = new javax.swing.JPasswordField();
        repeatPasswordTxt = new javax.swing.JPasswordField();
        createBtn = new javax.swing.JButton() {
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
        };
        cancelBtn = new javax.swing.JButton() {
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

                int arc = getHeight(); // Rounded corner
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        jLabel6 = new javax.swing.JLabel();
        usernameStatusLabel = new javax.swing.JLabel();
        passwordStatusLabel = new javax.swing.JLabel();
        emailStatusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        registerLabel.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        registerLabel.setForeground(new java.awt.Color(0, 102, 102));
        registerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        registerLabel.setText("Register a new acount");

        jLabel2.setText("Email");

        jLabel3.setText("Username");

        jLabel4.setText("Password");

        jLabel5.setText("Repeat password");

        createBtn.setForeground(new java.awt.Color(222, 224, 237));
        createBtn.setText("Create account");
        createBtn.setToolTipText("");
        createBtn.setBorderPainted(false);
        createBtn.setContentAreaFilled(false);
        createBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        createBtn.setOpaque(false);
        createBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBtnActionPerformed(evt);
            }
        });

        cancelBtn.setForeground(new java.awt.Color(222, 224, 237));
        cancelBtn.setText("Cancel");
        cancelBtn.setToolTipText("");
        cancelBtn.setBorderPainted(false);
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cancelBtn.setOpaque(false);
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        jLabel6.setText("You will need to verify your email to login to this account.");

        usernameStatusLabel.setText("Type your username");
        usernameStatusLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        passwordStatusLabel.setText("Type your password");
        passwordStatusLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        emailStatusLabel.setText("Type your email");
        emailStatusLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(passwordTxt, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(usernameTxt, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(passwordStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(97, 97, 97))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(emailStatusLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(usernameStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 23, Short.MAX_VALUE)
                                        .addComponent(createBtn)
                                        .addGap(18, 18, 18)
                                        .addComponent(cancelBtn)))
                                .addGap(100, 100, 100))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(repeatPasswordTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(registerLabel)
                .addGap(228, 228, 228))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(registerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(emailStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(emailTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(usernameStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(passwordStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(repeatPasswordTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelBtn)
                    .addComponent(createBtn))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        // TODO add your handling code here:
        RegisterDialog.this.dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        // TODO add your handling code here:
        
        updateEmailProperty();
        updateUsernameProperty();
        updatePasswordProperty();
        updateRepeatPasswordProperty();
        
        if (ValidationUtils.isEmpty(emailTextInput) || ValidationUtils.isEmpty(usernameTextInput) || ValidationUtils.isEmpty(passwordTextInput) || ValidationUtils.isEmpty(repeatPasswordTextInput)) {
            JOptionPane.showMessageDialog(this, "Please fill out all the form correctly", "The form is not filled", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ValidationUtils.isPasswordConfirmationValid(passwordTextInput, repeatPasswordTextInput)) {
            JOptionPane.showMessageDialog(this, "Password and repeated password are different!", "Error password", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ValidationUtils.isValidEmail(emailTextInput) || !ValidationUtils.isValidUsername(usernameTextInput) || !ValidationUtils.isValidPassword(passwordTextInput)) {
            JOptionPane.showMessageDialog(this, "Some of your text input has an error format", "Error formatting", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isUsernameTaken) {
            JOptionPane.showMessageDialog(this, "Username that you've inputed has been taken by others", "Error username", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        RegisterData REGISTER_DATA = new RegisterData(emailTextInput, usernameTextInput, passwordTextInput);
        
        // data siap untuk dikirimkan!
        sendRegisterData(REGISTER_DATA);
        
    }//GEN-LAST:event_createBtnActionPerformed
    
    
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                RegisterDialog dialog = new RegisterDialog(new javax.swing.JFrame(), true, null, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton createBtn;
    private javax.swing.JLabel emailStatusLabel;
    private javax.swing.JTextField emailTxt;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel passwordStatusLabel;
    private javax.swing.JPasswordField passwordTxt;
    private javax.swing.JLabel registerLabel;
    private javax.swing.JPasswordField repeatPasswordTxt;
    private javax.swing.JLabel usernameStatusLabel;
    private javax.swing.JTextField usernameTxt;
    // End of variables declaration//GEN-END:variables
}
