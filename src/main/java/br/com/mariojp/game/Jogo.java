package br.com.mariojp.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Jogo extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private Timer timer;
    private Nave nave;
    private int score = 0;
    private ArrayList<Inimigo> inimigos = new ArrayList<Inimigo>();
    private Random random = new Random();
    private boolean endgame = false;
    private final int DELAY = 10;
    private final int B_WIDTH = 800;
    private final int B_HEIGHT = 600;
	private JButton returnButton;  // Botão de retorno
    private JLabel mensagemLabel;  

    public Jogo() {
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
    }

    public void initGame() {
         addKeyListener(new TAdapter());
        setFocusable(true);
        setLayout(null);  // Desativar o layout para posicionar livremente os componentes
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
        nave = Nave.getInstance(40, 60, B_WIDTH);

        returnButton = new JButton("Retornar");
        returnButton.setBounds(10, 10, 100, 30);
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarJogo();
            }
        });
        returnButton.setVisible(false);  // Inicialmente invisível
        add(returnButton);

        mensagemLabel = new JLabel();
        mensagemLabel.setBounds(300, 250, 200, 30);
        mensagemLabel.setForeground(Color.WHITE);
        add(mensagemLabel);

        timer = new Timer(DELAY, this);
        timer.start();
    }

	public void startGame() {
		inimigos.clear();
		score = 0;
		endgame = false;
		initGame();
		requestFocusInWindow(); // Adicione esta linha
	}

	private void reiniciarJogo() {
        endgame = false;
        returnButton.setVisible(false);
        score = 0;
        nave.setVisible(true);
        nave.setX(40);
        nave.setX(60);
        nave.ajustarVelocidade(3);
        inimigos.clear();
        mensagemLabel.setText("");  // Limpa a mensagem ao reiniciar o jogo
        timer.start();
    }

	 private void drawGameOver(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fm = getFontMetrics(small);
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2, B_HEIGHT / 2);
        returnButton.setVisible(true);  // Torna o botão visível ao final do jogo
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!endgame) {
            desenhar(g);
        } else {
            drawGameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void desenhar(Graphics g) {
        g.drawImage(nave.getImage(), nave.getX(), nave.getY(), this);

        for (Missil m : nave.getMissiles()) {
            if (m.isVisible()) {
                g.drawImage(m.getImage(), m.getX(), m.getY(), this);
            }
        }
        for (Inimigo i : inimigos) {
            if (i.isVisible()) {
                g.drawImage(i.getImage(), i.getX(), i.getY(), this);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("PONTOS : " + score, 5, 15);
    }

    private void updateMissiles() {
        ArrayList<?> ms = nave.getMissiles();
        for (int i = 0; i < ms.size(); i++) {
            Missil m = (Missil) ms.get(i);
            if (m.isVisible()) {
                m.move();
            } else {
                ms.remove(i);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        stopGame();
        updateNave();
        updateMissiles();
        updateInimigo();
        checkCollisions();
        repaint();
    }

	public void checkCollisions() {
        Rectangle r3 = nave.getBounds();
        for (Inimigo alien : inimigos) {
            Rectangle r2 = alien.getBounds();
            if (r3.intersects(r2)) {
                nave.setVisible(false);
                alien.setVisible(false);
                endgame = true;
            }
        }
        ArrayList<Missil> ms = nave.getMissiles();
        for (Missil m : ms) {
            Rectangle r1 = m.getBounds();
            for (Inimigo alien : inimigos) {
                Rectangle r2 = alien.getBounds();
                if (r1.intersects(r2)) {
                    m.setVisible(false);
                    alien.setVisible(false);
                    score++;
                    if (score > 20) {
                        endgame = true;
                        exibirMensagemParabens();
                    }
                }
            }
        }
    }

	private void exibirMensagemParabens() {
        mensagemLabel.setText("    Parabéns! Você ganhou!");
    }

	private void updateInimigo() {
		while (inimigos.size() < 5) {
			inimigos.add(new Inimigo(B_WIDTH, random.nextInt(B_HEIGHT - 20) + 10));
		}

		for (int i = 0; i < inimigos.size(); i++) {
			Inimigo inimigo = inimigos.get(i);
			if (inimigo.isVisible()) {
				inimigo.move();
			} else {
				inimigos.remove(inimigo);
			}
		}

	}

	private void stopGame() {
		if (endgame) {
			timer.stop();
		}
	}

	private void updateNave() {
		nave.move();
	}

	private class TAdapter extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			nave.keyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			nave.keyPressed(e);
		}
	}
}