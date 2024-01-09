package com.sherlockgy;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileHashGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("File Hash GUI");
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        // 设置字体大小
        textArea.setFont(new Font("monospaced", Font.PLAIN, 18));
        // 设置行距
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setPreferredSize(new Dimension(600, 320));

        // 创建拖拽区域
        JPanel dragPanel = new JPanel(new BorderLayout()); // 设置布局为BorderLayout
        dragPanel.setBackground(Color.LIGHT_GRAY);
        JLabel dragLabel = new JLabel("拖拽区", SwingConstants.CENTER);
        dragPanel.add(dragLabel, BorderLayout.CENTER); // 将JLabel添加到JPanel的中心位置

        new DropTarget(dragPanel, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                java.util.List files = null;
                try {
                    files = (java.util.List) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File file = (File) files.get(0);
                try {
                    textArea.setText("");
                    String sha1 = calculateHash(file, "SHA-1");
                    String md5 = calculateHash(file, "MD5");
                    textArea.append("File: \n" + file.getAbsolutePath() + "\n\nSHA-1: \n" + sha1 + "\n\nMD5: \n" + md5 + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JButton button = new JButton("选择文件");
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    textArea.setText("");
                    String sha1 = calculateHash(selectedFile, "SHA-1");
                    String md5 = calculateHash(selectedFile, "MD5");
                    textArea.append("File: \n" + selectedFile.getAbsolutePath() + "\n\nSHA-1: \n" + sha1 + "\n\nMD5: \n" + md5 + "\n");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 创建清空按钮
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> textArea.setText("")); // 当点击清空按钮时，清空文本区域的内容

        // 创建一个面板，包含选择文件按钮和清空按钮
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(button, BorderLayout.NORTH);
        buttonPanel.add(clearButton, BorderLayout.SOUTH);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.NORTH); // 将文本区域添加到窗口的顶部
        frame.add(dragPanel, BorderLayout.CENTER); // 将拖拽区添加到窗口的中间
        frame.add(buttonPanel, BorderLayout.SOUTH); // 将按钮面板添加到窗口的底部
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setVisible(true);
    }

    private static String calculateHash(File file, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];
        int nread;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        byte[] mdbytes = md.digest();
        return DatatypeConverter.printHexBinary(mdbytes).toLowerCase();
    }
}
