package client.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class PhotoPanel extends JPanel{
    JPanel upside_panel;
    JButton upload_bt;
    JLabel upside_label;
    
    JPanel downside_panel;
    JLabel img_label;

    public PhotoPanel(){
        
    	setLayout(new BorderLayout(5, 5));
        setBackground(new Color(220, 255, 255));

        upside_panel = new JPanel();
        upside_panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        upside_panel.setPreferredSize(new Dimension(0, 35));
        upside_panel.setBackground(new Color(220, 255, 255));
        
        add(upside_panel, BorderLayout.NORTH);
        upside_panel.setLayout(new BorderLayout(5, 5));
        upload_bt = new JButton("업로드");
        upside_label = new JLabel("사진판");
        upside_label.setFont(new Font("Dialog", Font.BOLD, 20));
        upside_panel.add(upside_label, BorderLayout.CENTER);
        upside_panel.add(upload_bt, BorderLayout.EAST);
        
        upload_bt.addActionListener(new MyActionListener());

        downside_panel = new JPanel();
        downside_panel.setBackground(new Color(220, 255, 255));
        add(downside_panel, BorderLayout.CENTER);
        
        downside_panel.setLayout(new FlowLayout());

        img_label = new JLabel("이미지 파일을 선택하세요.");
        img_label.setHorizontalAlignment(SwingConstants.CENTER);
        downside_panel.add(img_label);

        // 추가예정



    }
    private class MyActionListener implements ActionListener {
        private JFileChooser chooser;
        public MyActionListener() {
            chooser = new JFileChooser();
        }
        public void actionPerformed(ActionEvent e){
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "JPG & GIF & PNG Images", 
            "JPG", "GIF", "PNG");
            chooser.setFileFilter(filter);
            int ret = chooser.showOpenDialog(null);
            if (ret != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null,
                     "파일을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String filePath = chooser.getSelectedFile().getPath();

            ImageIcon originalIcon = new ImageIcon(filePath);
        
            // 이미지를 표시할 공간 (downside_panel)의 크기를 가져옵니다
            int panelWidth = downside_panel.getWidth() > 0 ? downside_panel.getWidth() : 600; 
            int panelHeight = downside_panel.getHeight() > 0 ? downside_panel.getHeight() : 400;

            // 이미지 크기를 패널 크기에 맞게 조정합니다
            Image originalImage = originalIcon.getImage();
            
            // ImageUtil 같은 별도 클래스 없이 간단하게 조정합니다
            Image scaledImage = originalImage.getScaledInstance(
                panelWidth, 
                panelHeight, 
                Image.SCALE_SMOOTH // 부드러운 스케일링
            );
            
            // 조정된 이미지로 새로운 아이콘을 생성하여 라벨에 설정합니다.
            img_label.setIcon(new ImageIcon(scaledImage));
            img_label.setText(null); // 이미지가 있을 때는 텍스트를 숨깁니다.
            
            // 변경된 컴포넌트가 화면에 즉시 반영되도록 부모 컨테이너를 갱신합니다.
            downside_panel.revalidate();
            downside_panel.repaint();

            // img_label.setIcon(new ImageIcon(filePath));
        }
    }
}
