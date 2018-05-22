package com.github.anthonywww.projectdelta;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class JFrameInit {

	private JFrame frame;
	private JTextField xInput;
	private JTextField yInput;

	/**
	 * Create the application.
	 */
	public JFrameInit() {
		frame = new JFrame();
		frame.setSize(260, 80);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Location");
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/textures/icon.png")));
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		frame.getContentPane().add(panel);
		
		JLabel lblX = new JLabel("X:");
		panel.add(lblX);
		lblX.setHorizontalAlignment(SwingConstants.CENTER);
		
		xInput = new JTextField();
		xInput.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(xInput);
		xInput.setColumns(6);
		PlainDocument xInputDoc = (PlainDocument) xInput.getDocument();
		xInputDoc.setDocumentFilter(new MyIntFilter());

		JLabel lblY = new JLabel("Y:");
		panel.add(lblY);
		lblY.setHorizontalAlignment(SwingConstants.CENTER);
		
		yInput = new JTextField();
		yInput.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(yInput);
		yInput.setColumns(6);
		PlainDocument yInputDoc = (PlainDocument) yInput.getDocument();
		yInputDoc.setDocumentFilter(new MyIntFilter());
		
		JButton btnOk = new JButton("OK");
		panel.add(btnOk);
		
		
		xInput.setText("0");
		yInput.setText("0");
		xInput.requestFocusInWindow();
		
		
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btnOk) {
					int x = -1;
					int y = -1;
					try {
						x = Integer.parseInt(xInput.getText());
						y = Integer.parseInt(yInput.getText());
					} catch (NumberFormatException ex) {
						xInput.setText("0");
						yInput.setText("0");
						return;
					}
					
					if (x <= -1 || y <= -1) {
						xInput.setText("0");
						yInput.setText("0");
						return;
					}
					
					frame.dispose();
					new ProjectDelta(x, y);
				}
			}
		});
		
		frame.setVisible(true);
		frame.requestFocus();
	}
	
	
	
	private class MyIntFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);
			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			} else {
				// warn the user and don't allow the insert
			}
		}

		private boolean test(String text) {
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			} else {
				// don't allow the insert
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (test(sb.toString())) {
				super.remove(fb, offset, length);
			} else {
				// don't allow the insert
			}
		}
	}
}
